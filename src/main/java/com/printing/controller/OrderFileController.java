package com.printing.controller;

import com.printing.model.OrderFileStorage;
import com.printing.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*") // Настрой под свой фронтенд при необходимости
public class OrderFileController {


    @Autowired
    private com.printing.repository.OrderFileStorageRepository fileStorageRepository;

    @Autowired
    private OrderService orderService;

    // 1. Эндпоинт для загрузки файла к уже созданному заказу
    @PostMapping("/{orderId}/upload")
    public ResponseEntity<String> uploadFile(@PathVariable Long orderId, @RequestParam("file") MultipartFile file) {
        try {
            orderService.storeFile(orderId, file);
            return ResponseEntity.ok("Файл успешно загружен и сохранен в БД.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Ошибка при обработке файла: " + e.getMessage());
        }
    }

    // 2. Эндпоинт для Администратора: получить список файлов заказа
    @GetMapping("/{orderId}/files")
    public ResponseEntity<List<OrderFileStorage>> getOrderFiles(@PathVariable Long orderId) {
        List<OrderFileStorage> files = orderService.getFilesByOrderId(orderId);
        // Очищаем байты данных в списке, чтобы не перегружать сеть метаданными
        files.forEach(f -> f.setData(null));
        return ResponseEntity.ok(files);
    }

    // 3. Эндпоинт для Администратора: СКАЧАТЬ конкретный файл из БД по его ID таблицы хранения
    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        // 1. Используем репозиторий напрямую (или через новый метод сервиса),
        // чтобы найти файл именно по ID самой записи в таблице хранения, а не по ID заказа!
        return fileStorageRepository.findById(fileId)
                .map(file -> {
                    // 2. Безопасно кодируем русское имя файла для HTTP-заголовков
                    String encodedFileName = java.net.URLEncoder.encode(file.getFileName(), java.nio.charset.StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20"); // Заменяем плюсы на красивые пробелы

                    // Формируем правильный заголовок CONTENT_DISPOSITION для браузеров
                    String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(file.getFileType()))
                            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                            .body(file.getData());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}