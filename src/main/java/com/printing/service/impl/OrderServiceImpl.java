package com.printing.service.impl;

import com.printing.dto.OrderRequestDTO;
import com.printing.dto.OrderResponseDTO;
import com.printing.model.Order;
import com.printing.repository.OrderRepository;
import com.printing.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private com.printing.repository.PriceConfigRepository priceConfigRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private com.printing.repository.OrderFileStorageRepository fileStorageRepository;

    private final Random random = new Random();

    @jakarta.annotation.PostConstruct
    public void initPrices() {
        if (priceConfigRepository.count() == 0) {
            // --- ФОРМАТЫ ---
            priceConfigRepository.save(new com.printing.model.PriceConfig("A0", "\u0424\u043e\u0440\u043c\u0430\u0442 \u04100 (\u0437\u0430 \u0448\u0442.)", 150)); // Формат А0 (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("A1", "\u0424\u043e\u0440\u043c\u0430\u0442 \u04101 (\u0437\u0430 \u0448\u0442.)", 90));  // Формат А1 (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("A2", "\u0424\u043e\u0440\u043c\u0430\u0442 \u04102 (\u0437\u0430 \u0448\u0442.)", 45));  // Формат А2 (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("A3", "\u0424\u043e\u0440\u043c\u0430\u0442 \u04103 (\u0437\u0430 \u0448\u0442.)", 20));  // Формат А3 (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("A4", "\u0424\u043e\u0440\u043c\u0430\u0442 \u04104 (\u0437\u0430 \u0448\u0442.)", 10));  // Формат А4 (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("A5", "\u0424\u043e\u0440\u043c\u0430\u0442 \u04105 (\u0437\u0430 \u0448\u0442.)", 7));   // Формат А5 (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("A6", "\u0424\u043e\u0440\u043c\u0430\u0442 \u04106 (\u0437\u0430 \u0448\u0442.)", 4));   // Формат А6 (за шт.)

            // --- ТИПЫ БУМАГИ ---
            priceConfigRepository.save(new com.printing.model.PriceConfig("coated", "\u041c\u0435\u043b\u043e\u0432\u0430\u043d\u043d\u0430\u044f \u0431\u0443\u043c\u0430\u0433\u0430 (\u0437\u0430 \u0448\u0442.)", 4)); // Мелованная бумага (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("matte", "\u041c\u0430\u0442\u043e\u0432\u0430\u044f \u0431\u0443\u043c\u0430\u0433\u0430 (\u0437\u0430 \u0448\u0442.)", 7));   // Матовая бумага (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("glossy", "\u0413\u043b\u044f\u043d\u0446\u0435\u0432\u0430\u044f \u0431\u0443\u043c\u0430\u0433\u0430 (\u0437\u0430 \u0448\u0442.)", 5)); // Глянцевая бумага (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("cardboard", "\u041a\u0430\u0440\u0442\u043e\u043d (\u0437\u0430 \u0448\u0442.)", 15)); // Картон (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("design", "\u0414\u0438\u0437\u0430\u0439\u043d\u0435\u0440\u0441\u043a\u0438\u0439 \u043a\u0430\u0440\u0442\u043e\u043d (\u0437\u0430 \u0448\u0442.)", 25)); // Дизайнерский картон (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("sticky", "\u0421\u0430\u043c\u043e\u043a\u043b\u0435\u044f\u0449\u0430\u044f\u0441\u044f \u0431\u0443\u043c\u0430\u0433\u0430 (\u0437\u0430 \u0448\u0442.)", 12)); // Самоклеящаяся бумага (за шт.)

            // --- ДОПОЛНИТЕЛЬНЫЕ УСЛУГИ ---
            priceConfigRepository.save(new com.printing.model.PriceConfig("lamination", "\u041b\u0430\u043c\u0438\u043d\u0430\u0446\u0438\u044f (\u0437\u0430 \u0448\u0442.)", 15)); // Ламинация (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("folding", "\u0424\u0430\u043b\u044c\u0446\u043e\u0432\u043a\u0430 (\u0437\u0430 \u0448\u0442.)", 3));    // Фальцовка (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("creasing", "\u0411\u0438\u0433\u043e\u0432\u043a\u0430 (\u0437\u0430 \u0448\u0442.)", 4));     // Биговка (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("gluing", "\u0421\u043a\u043b\u0435\u0439\u043a\u0430 (\u0437\u0430 \u0448\u0442.)", 8));       // Склейка (за шт.)
            priceConfigRepository.save(new com.printing.model.PriceConfig("urgent", "\u0421\u0440\u043e\u0447\u043d\u044b\u0439 \u0437\u0430\u043a\u0430\u0437 (\u0444\u0438\u043a\u0441. \u043d\u0430\u0446\u0435\u043d\u043a\u0430)", 200)); // Срочный заказ (фикс. наценка)
        }
    }

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

        // 1. Базовая цена формата из БД
        int formatPrice = priceConfigRepository.findByItemKey(request.getFormat().trim())
                .map(com.printing.model.PriceConfig::getPrice)
                .orElse(0);

        // 2. Базовая цена бумаги из БД
        int paperPrice = priceConfigRepository.findByItemKey(request.getPaper().trim())
                .map(com.printing.model.PriceConfig::getPrice)
                .orElse(0);

        // 3. Расчет услуг по Варианту Б (Зеркально с JavaScript калькулятором)
        int servicesPricePerPieceSum = 0;
        int fixedUrgentPrice = 0;

        if (request.getServices() != null) {
            for (String serviceKey : request.getServices()) {
                // Добавили .trim() к ключу услуги
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

        // ЗЕРКАЛЬНАЯ ФОРМУЛА: (Цена формата + Цена бумаги + Сумма услуг за шт) * Количество + Срочность
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

    @Override
    @Transactional
    public Optional<OrderResponseDTO> updateOrderStatus(String orderNumber, String newStatus) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(order -> {
                    order.setStatus(newStatus);
                    order.setStatusUpdatedAt(LocalDateTime.now()); // <-- ФИКСИРУЕМ ВРЕМЯ ИЗМЕНЕНИЯ
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

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void storeFile(Long orderId, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException {
        // Проверяем лимит на всякий случай на уровне бизнес-логики (16 МБ = 16777216 Байт)
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

        // Заполняем DTO данными из сущности Order
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

        // Новые поля дат (если они у вас добавлены в OrderResponseDTO)
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatusUpdatedAt(order.getStatusUpdatedAt());

        return dto;
    }


}