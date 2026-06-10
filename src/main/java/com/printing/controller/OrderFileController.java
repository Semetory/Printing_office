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

/**
 * REST-контроллер для изолированной обработки файлов макетов, привязанных к заказам.
 * <p>
 * Обеспечивает потоковое сохранение макетов напрямую в СУБД в виде бинарных массивов (BLOB/BYTEA),
 * чтение метаданных для администратора, а также контролируемое скачивание файлов
 * с поддержкой корректного кодирования кириллических имен макетов в HTTP-заголовках.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderFileController {

    @Autowired
    private com.printing.repository.OrderFileStorageRepository fileStorageRepository;

    @Autowired
    private OrderService orderService;

    /**
     * Загружает бинарный файл макета и привязывает его к ранее созданному идентификатору заказа.
     *
     * @param orderId числовой первичный ключ заказа
     * @param file многокомпонентный объект загружаемого файла (Multipart)
     * @return {@link ResponseEntity} с текстовым результатом выполнения операции
     */
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

    /**
     * Запрашивает перечень всех файлов, относящихся к заказу.
     * <p>
     * Для минимизации сетевого трафика и предотвращения перегрузки оперативной памяти
     * массивы байт (тяжелый бинарный контент) принудительно обнуляются перед сериализацией.
     * </p>
     *
     * @param orderId числовой идентификатор заказа
     * @return {@link ResponseEntity} со списком объектов {@link OrderFileStorage} без бинарного наполнения
     */
    @GetMapping("/{orderId}/files")
    public ResponseEntity<List<OrderFileStorage>> getOrderFiles(@PathVariable Long orderId) {
        List<OrderFileStorage> files = orderService.getFilesByOrderId(orderId);
        files.forEach(f -> f.setData(null));
        return ResponseEntity.ok(files);
    }

    /**
     * Позволяет скачать файл макета из базы данных по его индивидуальному идентификационному ключу хранения.
     * <p>
     * Производит трансляцию и кодирование оригинального имени файла по спецификации UTF-8 (RFC 5987)
     * для предотвращения порчи кириллического названия (искажения кодировки) в целевом браузере сотрудника.
     * </p>
     *
     * @param fileId уникальный ID записи в таблице файлового хранилища
     * @return {@link ResponseEntity} с байтовым массивом содержимого файла и заголовками скачивания
     */
    @GetMapping("/files/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long fileId) {
        return fileStorageRepository.findById(fileId)
                .map(file -> {
                    String encodedFileName = java.net.URLEncoder.encode(file.getFileName(), java.nio.charset.StandardCharsets.UTF_8)
                            .replaceAll("\\+", "%20");

                    String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

                    return ResponseEntity.ok()
                            .contentType(MediaType.parseMediaType(file.getFileType()))
                            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                            .body(file.getData());
                })
                .orElse(ResponseEntity.notFound().build());
    }
}