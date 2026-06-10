package com.printing.service.impl;

import com.printing.dto.OrderRequestDTO;
import com.printing.dto.OrderResponseDTO;
import com.printing.model.Order;
import com.printing.repository.OrderRepository;
import com.printing.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления заказами {@link OrderService}.
 * <p>
 * Класс содержит основную бизнес-логику полиграфии: динамическое прайсирование элементов,
 * зеркальный математический расчет стоимости (синхронизированный с фронтенд-калькулятором JavaScript),
 * генерацию уникальных номеров по маске, менеджмент файлов макетов и обновление статусов.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private com.printing.repository.PriceConfigRepository priceConfigRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private com.printing.repository.OrderFileStorageRepository fileStorageRepository;

    private final Random random = new Random();

    /**
     * Постконструктор для первичного наполнения справочника цен (тарифной сетки).
     * Выполняется автоматически один раз при старте контекста Spring, если таблица конфигурации пуста.
     */
    @jakarta.annotation.PostConstruct
    public void initPrices() {
        if (priceConfigRepository.count() == 0) {
            // --- ФОРМАТЫ ---
            priceConfigRepository.save(new com.printing.model.PriceConfig("A0", "Формат А0 (за шт.)", 150));
            priceConfigRepository.save(new com.printing.model.PriceConfig("A1", "Формат А1 (за шт.)", 90));
            priceConfigRepository.save(new com.printing.model.PriceConfig("A2", "Формат А2 (за шт.)", 45));
            priceConfigRepository.save(new com.printing.model.PriceConfig("A3", "Формат А3 (за шт.)", 20));
            priceConfigRepository.save(new com.printing.model.PriceConfig("A4", "Формат А4 (за шт.)", 10));
            priceConfigRepository.save(new com.printing.model.PriceConfig("A5", "Формат А5 (за шт.)", 7));
            priceConfigRepository.save(new com.printing.model.PriceConfig("A6", "Формат А6 (за шт.)", 4));

            // --- ТИПЫ БУМАГИ ---
            priceConfigRepository.save(new com.printing.model.PriceConfig("coated", "Мелованная бумага (за шт.)", 4));
            priceConfigRepository.save(new com.printing.model.PriceConfig("matte", "Матовая бумага (за шт.)", 7));
            priceConfigRepository.save(new com.printing.model.PriceConfig("glossy", "Глянцевая бумага (за шт.)", 5));
            priceConfigRepository.save(new com.printing.model.PriceConfig("cardboard", "Картон (за шт.)", 15));
            priceConfigRepository.save(new com.printing.model.PriceConfig("design", "Дизайнерский картон (за шт.)", 25));
            priceConfigRepository.save(new com.printing.model.PriceConfig("sticky", "Самоклеящаяся бумага (за шт.)", 12));

            // --- ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ---
            priceConfigRepository.save(new com.printing.model.PriceConfig("lamination", "Ламинация (за шт.)", 15));
            priceConfigRepository.save(new com.printing.model.PriceConfig("folding", "Фальцовка (за шт.)", 3));
            priceConfigRepository.save(new com.printing.model.PriceConfig("creasing", "Биговка (за шт.)", 4));
            priceConfigRepository.save(new com.printing.model.PriceConfig("gluing", "Склейка (за шт.)", 8));
            priceConfigRepository.save(new com.printing.model.PriceConfig("urgent", "Срочный заказ (фикс. наценка)", 200));
        }
    }

    /**
     * Создает новый заказ в системе с автоматическим расчетом его финальной стоимости.
     * <p>
     * Расчет производится по формуле:
     * {@code Итого = (Цена формата + Цена бумаги + Сумма поштучных услуг) * Тираж + Цена срочности}
     * </p>
     *
     * @param request DTO с параметрами конфигурации заказа от клиента
     * @return сформированный ответ {@link OrderResponseDTO} с рассчитанной стоимостью и номером
     */
    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        System.out.println("=== НОВЫЙ ЗАКАЗ ===");
        System.out.println("Формат: " + request.getFormat());
        System.out.println("Бумага: " + request.getPaper());
        System.out.println("Тираж: " + request.getQuantity());
        System.out.println("Выбранные услуги на бэкенде: " + request.getServices());
        System.out.println("====================");

        Order order = new Order();
        order.setOrderNumber(generateOrderNumber(request.getPhone()));
        order.setFullname(request.getFullname());
        order.setPhone(request.getPhone());
        order.setEmail(request.getEmail());
        order.setFormat(request.getFormat());
        order.setPaper(request.getPaper());
        order.setQuantity(request.getQuantity());
        order.setPayment(request.getPayment() != null ? request.getPayment() : "Онлайн");
        order.setFiles(request.getFiles() != null ? request.getFiles() : List.of());
        order.setStatus("Принят");

        int formatPrice = priceConfigRepository.findByItemKey(request.getFormat().trim())
                .map(com.printing.model.PriceConfig::getPrice)
                .orElse(0);

        int paperPrice = priceConfigRepository.findByItemKey(request.getPaper().trim())
                .map(com.printing.model.PriceConfig::getPrice)
                .orElse(0);

        int servicesPricePerPieceSum = 0;
        int fixedUrgentPrice = 0;

        if (request.getServices() != null) {
            for (String serviceKey : request.getServices()) {
                int servicePrice = priceConfigRepository.findByItemKey(serviceKey.trim())
                        .map(com.printing.model.PriceConfig::getPrice)
                        .orElse(0);

                if ("urgent".equals(serviceKey.trim())) {
                    fixedUrgentPrice = servicePrice;
                } else {
                    servicesPricePerPieceSum += servicePrice;
                }
            }
        }

        int calculatedTotal = ((formatPrice + paperPrice + servicesPricePerPieceSum) * request.getQuantity()) + fixedUrgentPrice;

        order.setTotal(calculatedTotal);
        order.setServices(request.getServices());

        Order saved = orderRepository.save(order);
        return mapToResponse(saved);
    }

    @Override
    public Optional<OrderResponseDTO> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(this::mapToResponse);
    }

    @Override
    public List<OrderResponseDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Изменяет текущий рабочий статус заказа с фиксацией времени этого изменения.
     *
     * @param orderNumber уникальный строковый рабочий номер заказа
     * @param newStatus   строковое обозначение нового статуса
     * @return {@link Optional} с DTO ответа обновленного заказа
     */
    @Override
    @Transactional
    public Optional<OrderResponseDTO> updateOrderStatus(String orderNumber, String newStatus) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(order -> {
                    order.setStatus(newStatus);
                    order.setStatusUpdatedAt(LocalDateTime.now());
                    orderRepository.save(order);
                    return convertToResponseDTO(order);
                });
    }

    @Override
    public boolean deleteOrder(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(order -> {
                    orderRepository.delete(order);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Генерирует уникальный номер заказа на основании номера телефона и текущей даты.
     * Сборка номера идет по маске: {@code [4_последние_цифры_телефона][yyMMddHHmmss][3_рандомных_знака]}
     *
     * @param phone номер телефона клиента
     * @return гарантированно уникальная строка номера заказа
     */
    private String generateOrderNumber(String phone) {
        String orderNumber;
        do {
            String digits = phone.replaceAll("\\D", "");
            String lastFour = digits.length() >= 4 ? digits.substring(digits.length() - 4) : "0000";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
            String randomStr = String.format("%03d", random.nextInt(1000));
            orderNumber = lastFour + timestamp + randomStr;
        } while (orderRepository.findByOrderNumber(orderNumber).isPresent());
        return orderNumber;
    }

    private OrderResponseDTO mapToResponse(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setFullname(order.getFullname());
        dto.setPhone(order.getPhone());
        dto.setEmail(order.getEmail());
        dto.setFormat(order.getFormat());
        dto.setPaper(order.getPaper());
        dto.setQuantity(order.getQuantity());
        dto.setPayment(order.getPayment());
        dto.setTotal(order.getTotal());
        dto.setFiles(order.getFiles());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setServices(order.getServices());
        return dto;
    }

    /**
     * Помещает входящий бинарный файл макета в базу данных с привязкой к ID заказа.
     * Имеет встроенный ограничитель на максимальный размер файла в 16 МБ.
     *
     * @param orderId числовой идентификатор заказа
     * @param file    объект загружаемого файла Spring {@link org.springframework.web.multipart.MultipartFile}
     * @throws IOException              при ошибках чтения байтового потока файла
     * @throws IllegalArgumentException если размер макета превышает 16777216 Байт (16 МБ)
     */
    @Override
    @Transactional
    public void storeFile(Long orderId, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        if (file.getSize() > 16777216) {
            throw new IllegalArgumentException("Размер файла превышает допустимые 16 МБ!");
        }

        com.printing.model.OrderFileStorage fileStorage = new com.printing.model.OrderFileStorage();
        fileStorage.setOrderId(orderId);
        fileStorage.setFileName(org.springframework.util.StringUtils.cleanPath(file.getOriginalFilename()));
        fileStorage.setFileType(file.getContentType());
        fileStorage.setData(file.getBytes());

        fileStorageRepository.save(fileStorage);
    }

    @Override
    public List<com.printing.model.OrderFileStorage> getFilesByOrderId(Long orderId) {
        return fileStorageRepository.findByOrderId(orderId);
    }

    private com.printing.dto.OrderResponseDTO convertToResponseDTO(com.printing.model.Order order) {
        if (order == null) {
            return null;
        }
        com.printing.dto.OrderResponseDTO dto = new com.printing.dto.OrderResponseDTO();
        dto.setOrderNumber(order.getOrderNumber());
        dto.setFullname(order.getFullname());
        dto.setPhone(order.getPhone());
        dto.setEmail(order.getEmail());
        dto.setFormat(order.getFormat());
        dto.setPaper(order.getPaper());
        dto.setQuantity(order.getQuantity());
        dto.setPayment(order.getPayment());
        dto.setTotal(order.getTotal());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatusUpdatedAt(order.getStatusUpdatedAt());
        return dto;
    }
}