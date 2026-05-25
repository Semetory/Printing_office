package com.printing.controller;

import com.printing.dto.LoginRequestDTO;
import com.printing.dto.LoginResponseDTO;
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
    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        if (logAdmin.equals(request.getUsername()) && passAdmin.equals(request.getPassword())) {
            return ResponseEntity.ok(new LoginResponseDTO("ADMIN", "/admin.html"));
        } else if (logSysAdmin.equals(request.getUsername()) && passSysAdmin.equals(request.getPassword())) {
            return ResponseEntity.ok(new LoginResponseDTO("SYSADMIN", "/sysadmin.html"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
    }

    // Находим строчки с @Value в начале класса и добавляем внедрение пароля:
    @Value("${spring.datasource.password}")
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
            orderRepository.deleteAll();
            return ResponseEntity.ok("Все таблицы успешно очищены!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка очистки: " + e.getMessage());
        }
    }

    // Вспомогательный метод получения имени БД из jdbc:postgresql://localhost:5432/printing
    private String extractDbName(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
}