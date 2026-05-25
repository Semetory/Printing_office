package com.printing.service;

import com.printing.dto.OrderRequestDTO;
import com.printing.dto.OrderResponseDTO;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO request);
    Optional<OrderResponseDTO> getOrderByNumber(String orderNumber);
    List<OrderResponseDTO> getAllOrders();
    Optional<OrderResponseDTO> updateOrderStatus(String orderNumber, String status);
    boolean deleteOrder(String orderNumber);
}