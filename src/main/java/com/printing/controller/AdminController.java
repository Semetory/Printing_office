package com.printing.controller;

import com.printing.dto.LoginRequestDTO;
import com.printing.dto.LoginResponseDTO;
import com.printing.model.PriceConfig;
import com.printing.model.SystemLog;
import com.printing.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST-контроллер для управления административной панелью типографии.
 * <p>
 * Обеспечивает функции многоролевой аутентификации (ADMIN/SYSADMIN) с защитой от подбора паролей,
 * управление тарифной сеткой, просмотр системных логов, а также низкоуровневые операции
 * обслуживания базы данных (резервное копирование, восстановление из дампа, полная очистка таблиц).
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Value("${LOGADMIN}")
    private String logAdmin;

    @Value("${PASSADMIN}")
    private String passAdmin;

    @Value("${LOGSYSADMIN}")
    private String logSysAdmin;

    @Value("${PASSSYSADMIN}")
    private String passSysAdmin;

    @Value("${spring.datasource.primary.username}")
    private String dbUser;

    @Value("${spring.datasource.primary.jdbc-url}")
    private String dbUrl;

    @Value("${spring.datasource.primary.password}")
    private String dbPassword;

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

    @Autowired
    private com.printing.repository.PriceConfigRepository priceConfigRepository;

    /**
     * Выполняет аутентификацию пользователя в административной панели.
     * <p>
     * Реализует механизм защиты (Brute-Force Protection): при совершении 3 неудачных попыток ввода данных
     * учетная запись блокируется на 10 минут. При успешном входе счетчик ошибок сбрасывается.
     * </p>
     *
     * @param request объект DTO с учетными данными пользователя (логин и пароль)
     * @return {@link ResponseEntity} со статусом 200 (OK) и токеном перенаправления,
     * либо статус 401 (Unauthorized) при неверных данных,
     * либо статус 403 (Forbidden) при активной блокировке.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        String username = request.getUsername();
        LocalDateTime now = LocalDateTime.now();

        com.printing.model.LoginAttempt attempt = loginAttemptRepository.findById(username)
                .orElse(new com.printing.model.LoginAttempt(username, 0, null));

        if (attempt.getLockTime() != null && attempt.getLockTime().isAfter(now)) {
            java.time.Duration duration = java.time.Duration.between(now, attempt.getLockTime());
            long minutesLeft = duration.toMinutes() + 1;
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Лимит попыток исчерпан. Вход заблокирован на " + minutesLeft + " мин.");
        }

        boolean isValidAdmin = logAdmin.equals(username) && passAdmin.equals(request.getPassword());
        boolean isValidSysAdmin = logSysAdmin.equals(username) && passSysAdmin.equals(request.getPassword());

        if (isValidAdmin || isValidSysAdmin) {
            attempt.setAttempts(0);
            attempt.setLockTime(null);
            loginAttemptRepository.save(attempt);

            String role = isValidAdmin ? "ADMIN" : "SYSADMIN";
            String redirectUrl = isValidAdmin ? "/admin.html" : "/sysadmin.html";
            return ResponseEntity.ok(new LoginResponseDTO(role, redirectUrl));
        } else {
            int failedAttempts = attempt.getAttempts() + 1;
            attempt.setAttempts(failedAttempts);

            if (failedAttempts >= 3) {
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

    /**
     * Создает резервную копию (дамп) основной базы данных и передает ее клиенту в виде файла.
     * <p>
     * Метод использует утилиту {@code pg_dump}, подключаясь по петлевому IPv4-адресу (127.0.0.1)
     * для исключения конфликтов IPv6 сетевого стека Windows.
     * </p>
     *
     * @param response объект HTTP-ответа сервлета для записи бинарного потока файла дампа
     */
    @GetMapping("/database/download")
    public void downloadDatabase(jakarta.servlet.http.HttpServletResponse response) {
        String dbName = extractDbName(dbUrl);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"database_backup.sql\"");

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "pg_dump", "-h", "127.0.0.1", "-U", dbUser, "-w", "-F", "p", dbName
            );
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
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Восстанавливает структуру и данные основной базы данных из загруженного SQL-файла.
     * <p>
     * Перед развертыванием дампа каскадно удаляет все текущие записи из таблицы заказов.
     * Использует системную утилиту {@code psql}.
     * </p>
     *
     * @param file загружаемый SQL-файл резервной копии
     * @return {@link ResponseEntity} со статусным текстовым сообщением о результате операции
     */
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

            ProcessBuilder pb = new ProcessBuilder(
                    "psql", "-h", "127.0.0.1", "-U", dbUser, "-d", dbName, "-f", tempFile.getAbsolutePath()
            );
            pb.environment().put("PGPASSWORD", dbPassword);
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            Process process = pb.start();
            int exitCode = process.waitFor();

            tempFile.delete();

            if (exitCode == 0) {
                return ResponseEntity.ok("База данных успешно восстановлена!");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Ошибка psql при импорте SQL-дампа. Код: " + exitCode);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Производит полную очистку операционных таблиц основной БД и осуществляет
     * повторную инициализацию базового прайс-листа. Логирует данное событие.
     *
     * @return {@link ResponseEntity} с подтверждением очистки
     */
    @DeleteMapping("/database/clear")
    public ResponseEntity<String> clearDatabase() {
        try {
            orderRepository.deleteAll();
            priceConfigRepository.deleteAll();
            if (orderService instanceof com.printing.service.impl.OrderServiceImpl) {
                ((com.printing.service.impl.OrderServiceImpl) orderService).initPrices();
            }
            logSystemEvent("Очистка и пересоздание основной БД", "Успешно очищено. Прайс-лист инициализирован заново.");
            return ResponseEntity.ok("Все таблицы успешно очищены, прайс-лист пересоздан!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка очистки: " + e.getMessage());
        }
    }

    /**
     * Очищает архивную базу данных и фиксирует операцию в системном журнале.
     *
     * @return {@link ResponseEntity} со статусным ответом
     */
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

    /**
     * Предоставляет полный перечень ценовых конфигураций для редактирования в админ-панели.
     *
     * @return {@link ResponseEntity} со списком всех записей {@link PriceConfig}
     */
    @GetMapping("/prices")
    public ResponseEntity<List<com.printing.model.PriceConfig>> getPrices() {
        return ResponseEntity.ok(priceConfigRepository.findAll());
    }

    /**
     * Возвращает полный список записей системного журнала событий безопасности.
     *
     * @return {@link ResponseEntity} со списком объектов {@link SystemLog}
     */
    @GetMapping("/system/logs")
    public ResponseEntity<List<SystemLog>> getSystemLogs() {
        return ResponseEntity.ok(logRepository.findAll());
    }

    /**
     * Обновляет базовую стоимость указанной номенклатурной позиции или услуги.
     * Включает валидационную защиту от установления отрицательной стоимости.
     *
     * @param key уникальный текстовый идентификатор тарифа (item_key)
     * @param newPrice новое значение цены
     * @return {@link ResponseEntity} с уведомлением об успешном обновлении, либо
     * статус 400 (Bad Request) при некорректной стоимости.
     */
    @PutMapping("/prices/{key}")
    public ResponseEntity<?> updatePrice(@PathVariable String key, @RequestParam Integer newPrice) {
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

    private void logSystemEvent(String action, String details) {
        logRepository.save(new com.printing.model.SystemLog(action, details));
    }

    private String extractDbName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}