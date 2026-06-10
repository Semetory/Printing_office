package com.printing.service.impl;

import com.printing.dto.OrderRequestDTO;
import com.printing.dto.OrderResponseDTO;
import com.printing.model.Order;
import com.printing.model.PriceConfig;
import com.printing.model.OrderFileStorage;
import com.printing.repository.OrderFileStorageRepository;
import com.printing.repository.OrderRepository;
import com.printing.repository.PriceConfigRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Набор модульных (Unit) тестов для проверки бизнес-логики {@link OrderServiceImpl}.
 * <p>
 * Тесты изолированы от реальной базы данных и сетевого окружения с помощью фреймворка Mockito
 * (расширение {@link MockitoExtension}).
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private PriceConfigRepository priceConfigRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderFileStorageRepository fileStorageRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderRequestDTO validRequest;

    /**
     * Предварительная настройка окружения перед запуском каждого тестового сценария.
     */
    @BeforeEach
    void setUp() {
        validRequest = new OrderRequestDTO();
        validRequest.setFullname("Иван Иванов");
        validRequest.setPhone("+79991112233");
        validRequest.setEmail("ivan@example.com");
        validRequest.setFormat("A4");
        validRequest.setPaper("glossy");
        validRequest.setQuantity(100);
        validRequest.setPayment("Онлайн");
        validRequest.setServices(List.of("lamination", "urgent"));
    }

    /**
     * Проверяет корректность математических расчетов стоимости заказа,
     * включая обработку фиксированных наценок за срочность ("urgent").
     */
    @Test
    @DisplayName("Успешное создание заказа со сложным расчетом стоимости (включая срочность)")
    void createOrder_Success_CalculatesCorrectTotal() {
        when(priceConfigRepository.findByItemKey("A4")).thenReturn(Optional.of(new PriceConfig("A4", "Формат А4", 10)));
        when(priceConfigRepository.findByItemKey("glossy")).thenReturn(Optional.of(new PriceConfig("glossy", "Глянцевая", 5)));
        when(priceConfigRepository.findByItemKey("lamination")).thenReturn(Optional.of(new PriceConfig("lamination", "Ламинация", 15)));
        when(priceConfigRepository.findByItemKey("urgent")).thenReturn(Optional.of(new PriceConfig("urgent", "Срочно", 200)));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order passedOrder = invocation.getArgument(0);
            passedOrder.setId(1L);
            return passedOrder;
        });

        OrderResponseDTO response = orderService.createOrder(validRequest);

        assertNotNull(response);
        assertEquals("Принят", response.getStatus());
        assertEquals(3200, response.getTotal(), "Итоговая стоимость рассчитана неверно!");
        assertNotNull(response.getOrderNumber());
        assertTrue(response.getOrderNumber().startsWith("2233"), "Номер заказа должен начинаться на последние 4 цифры телефона");

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Проверяет извлечение заказа по строковому номеру, если он присутствует в репозитории.
     */
    @Test
    @DisplayName("Получение заказа по уникальному номеру")
    void getOrderByNumber_ReturnsOrder_WhenExists() {
        String testOrderNumber = "2233260610120000123";
        Order mockOrder = new Order();
        mockOrder.setOrderNumber(testOrderNumber);
        mockOrder.setFullname("Петр Петров");
        mockOrder.setStatus("Принят");

        when(orderRepository.findByOrderNumber(testOrderNumber)).thenReturn(Optional.of(mockOrder));

        Optional<OrderResponseDTO> result = orderService.getOrderByNumber(testOrderNumber);

        assertTrue(result.isPresent());
        assertEquals("Петр Петров", result.get().getFullname());
        assertEquals(testOrderNumber, result.get().getOrderNumber());
    }

    /**
     * Проверяет смену статуса заказа и обязательную фиксацию времени этого действия.
     */
    @Test
    @DisplayName("Обновление статуса заказа и фиксация времени изменения")
    void updateOrderStatus_SuccessfullyUpdatesStatusAndDate() {
        String testOrderNumber = "12345";
        Order mockOrder = new Order();
        mockOrder.setOrderNumber(testOrderNumber);
        mockOrder.setStatus("Принят");

        when(orderRepository.findByOrderNumber(testOrderNumber)).thenReturn(Optional.of(mockOrder));

        Optional<OrderResponseDTO> result = orderService.updateOrderStatus(testOrderNumber, "В печати");

        assertTrue(result.isPresent());
        assertEquals("В печати", result.get().getStatus());
        assertNotNull(mockOrder.getStatusUpdatedAt(), "Дата изменения статуса должна быть зафиксирована!");
        verify(orderRepository, times(1)).save(mockOrder);
    }

    /**
     * Гарантирует падение метода с ошибкой, если пользователь пытается загрузить файл
     * объемом строго больше 16 МБ.
     */
    @Test
    @DisplayName("Сохранение файла: ошибка, если размер макета превышает 16 МБ")
    void storeFile_ThrowsException_WhenFileIsTooLarge() {
        byte[] largeContent = new byte[17 * 1024 * 1024]; // 17 МБ
        MockMultipartFile largeFile = new MockMultipartFile(
                "layout", "big_makat.pdf", "application/pdf", largeContent
        );

        assertThrows(IllegalArgumentException.class, () ->
                orderService.storeFile(1L, largeFile)
        );

        verifyNoInteractions(fileStorageRepository);
    }

    /**
     * Проверяет штатное и успешное сохранение файлов, укладывающихся в лимит размера.
     */
    @Test
    @DisplayName("Успешное сохранение файла в рамках лимита")
    void storeFile_Success_WhenSizeIsOk() throws IOException {
        MockMultipartFile normalFile = new MockMultipartFile(
                "layout", "preview.png", "image/png", "test image content".getBytes()
        );

        orderService.storeFile(1L, normalFile);

        verify(fileStorageRepository, times(1)).save(any(OrderFileStorage.class));
    }
}