package com.printing.controller;

import com.printing.dto.LoginRequestDTO;
import com.printing.dto.LoginResponseDTO;
import com.printing.model.SystemLog;
import com.printing.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    // Внедряем значения напрямую из .env (благодаря spring-dotenv)
    @Value("${LOGADMIN}")
    private String logAdmin;

    @Value("${PASSADMIN}")
    private String passAdmin;

    @Value("${LOGSYSADMIN}")
    private String logSysAdmin;

    @Value("${PASSSYSADMIN}")
    private String passSysAdmin;

    // Свойства подключения к БД из твоего application.properties
    @Value("${spring.datasource.primary.username}")
    private String dbUser;

    @Value("${spring.datasource.primary.jdbc-url}")
    private String dbUrl;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private com.printing.repository.LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private com.printing.service.OrderService orderService;

    @Autowired
    private com.printing.repository.archive.ArchiveOrderRepository archiveOrderRepository;

    @Autowired
    private com.printing.repository.SystemLogRepository logRepository;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        String username = request.getUsername();
        LocalDateTime now = LocalDateTime.now();

        // 1. Проверяем, существует ли уже запись о попытках для этого пользователя
        com.printing.model.LoginAttempt attempt = loginAttemptRepository.findById(username)
                .orElse(new com.printing.model.LoginAttempt(username, 0, null));

        // 2. Проверяем, находится ли пользователь под активной блокировкой
        if (attempt.getLockTime() != null && attempt.getLockTime().isAfter(now)) {
            java.time.Duration duration = java.time.Duration.between(now, attempt.getLockTime());
            long minutesLeft = duration.toMinutes() + 1;
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Лимит попыток исчерпан. Вход заблокирован на " + minutesLeft + " мин.");
        }

        // Проверка логина и пароля
        boolean isValidAdmin = logAdmin.equals(username) && passAdmin.equals(request.getPassword());
        boolean isValidSysAdmin = logSysAdmin.equals(username) && passSysAdmin.equals(request.getPassword());

        if (isValidAdmin || isValidSysAdmin) {
            // Успешный вход — сбрасываем счетчик ошибок в ноль
            attempt.setAttempts(0);
            attempt.setLockTime(null);
            loginAttemptRepository.save(attempt);

            String role = isValidAdmin ? "ADMIN" : "SYSADMIN";
            String redirectUrl = isValidAdmin ? "/admin.html" : "/sysadmin.html";
            return ResponseEntity.ok(new LoginResponseDTO(role, redirectUrl));
        } else {
            // Ошибка входа — увеличиваем счетчик
            int failedAttempts = attempt.getAttempts() + 1;
            attempt.setAttempts(failedAttempts);

            if (failedAttempts >= 3) {
                // Ставим блокировку на 10 минут от текущего времени сервера
                attempt.setLockTime(now.plusMinutes(10));
                loginAttemptRepository.save(attempt);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Вы ввели неверные данные 3 раза. Вход заблокирован на 10 минут.");
            } else {
                loginAttemptRepository.save(attempt);
                int remains = 3 - failedAttempts;
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Введенные данные не верны. Осталось попыток: " + remains);
            }
        }
    }

    // Находим строчки с @Value в начале класса и добавляем внедрение пароля:
    @Value("${spring.datasource.primary.password}")
    private String dbPassword; // Теперь пароль автоматически подтянется из ваших настроек БД!

    // Обновленный метод скачивания
    @GetMapping("/database/download")
    public void downloadDatabase(jakarta.servlet.http.HttpServletResponse response) {
        String dbName = extractDbName(dbUrl);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"database_backup.sql\"");

        try {
            // ВМЕСТО "localhost" строго пишем "127.0.0.1", чтобы обойти баг IPv6 (::1) в Windows
            ProcessBuilder pb = new ProcessBuilder(
                    "pg_dump",
                    "-h", "127.0.0.1",
                    "-U", dbUser,
                    "-w",
                    "-F", "p",
                    dbName
            );

            // Берем реальный пароль СУБД, с которым успешно стартует Hibernate
            pb.environment().put("PGPASSWORD", dbPassword);

            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();

            try (java.io.InputStream is = process.getInputStream();
                 java.io.OutputStream os = response.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                os.flush();
            }

            int exitCode = process.waitFor();
            System.out.println("[SysAdmin] pg_dump завершился с кодом: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // 2. ЗАГРУЗКА БД (ИМПОРТ) — с поддержкой каскадных связей
    @PostMapping("/database/upload")
    public ResponseEntity<String> uploadDatabase(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл пуст");
        }

        try {
            String dbName = extractDbName(dbUrl);
            File tempFile = File.createTempFile("db_restore_", ".sql");
            file.transferTo(tempFile);

            orderRepository.deleteAll();

            // Тоже меняем на 127.0.0.1
            ProcessBuilder pb = new ProcessBuilder(
                    "psql", "-h", "127.0.0.1", "-U", dbUser, "-d", dbName, "-f", tempFile.getAbsolutePath()
            );
            pb.environment().put("PGPASSWORD", dbPassword); // Динамический пароль
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();
            int exitCode = process.waitFor();

            tempFile.delete();

            if (exitCode == 0) {
                return ResponseEntity.ok("База данных успешно восстановлена!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка psql при импорте SQL-дампа. Код: " + exitCode);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    // 3. Очистить БД
    @DeleteMapping("/database/clear")
    public ResponseEntity<String> clearDatabase() {
        try {
            // 1. Очищаем основные таблицы заказа
            orderRepository.deleteAll();
            priceConfigRepository.deleteAll();
            // 2. Вызываем метод пересоздания прайса БЕЗ перезапуска сервера
            if (orderService instanceof com.printing.service.impl.OrderServiceImpl) {
                ((com.printing.service.impl.OrderServiceImpl) orderService).initPrices();
            }
            // Записываем событие в лог сисадмина (Логику логов см. в Шаге 3)
            logSystemEvent("Очистка и пересоздание основной БД", "Успешно очищено. Прайс-лист инициализирован заново.");
            return ResponseEntity.ok("Все таблицы успешно очищены, прайс-лист пересоздан!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка очистки: " + e.getMessage());
        }
    }

    // Очистка и пересоздание архива
    @DeleteMapping("/archive/clear")
    public ResponseEntity<String> clearArchive() {
        try {
            archiveOrderRepository.deleteAll();
            logSystemEvent("Очистка архива", "Архивная база данных была полностью очищена сисадмином.");
            return ResponseEntity.ok("Архив успешно очищен!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Вспомогательный метод добавления в лог
    private void logSystemEvent(String action, String details) {
        logRepository.save(new com.printing.model.SystemLog(action, details));
    }

    // Вспомогательный метод получения имени БД из jdbc:postgresql://localhost:5432/printing
    private String extractDbName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    @Autowired
    private com.printing.repository.PriceConfigRepository priceConfigRepository;

    // Получить все цены
    @GetMapping("/prices")
    public ResponseEntity<List<com.printing.model.PriceConfig>> getPrices() {
        return ResponseEntity.ok(priceConfigRepository.findAll());
    }

    // Получение системных логов для панели сисадмина
    @GetMapping("/system/logs")
    public ResponseEntity<List<SystemLog>> getSystemLogs() {
        return ResponseEntity.ok(logRepository.findAll());
    }

    // Обновить цену конкретной позиции
    @PutMapping("/prices/{key}")
    public ResponseEntity<?> updatePrice(@PathVariable String key, @RequestParam Integer newPrice) {
        // ЗАЩИТА: Цена не может быть меньше нуля
        if (newPrice == null || newPrice < 0) {
            return ResponseEntity.badRequest().body("Цена не может быть отрицательной или пустой!");
        }

        return priceConfigRepository.findById(key)
                .map(config -> {
                    config.setPrice(newPrice);
                    priceConfigRepository.save(config);
                    return ResponseEntity.ok("Цена успешно обновлена");
                })
                .orElse(ResponseEntity.notFound().build());
    }

}