package com.printing.service.impl;

import com.printing.dto.OrderRequestDTO;
import com.printing.dto.OrderResponseDTO;
import com.printing.model.Order;
import com.printing.repository.OrderRepository;
import com.printing.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    private final Random random = new Random();

    @Override
    public OrderResponseDTO createOrder(OrderRequestDTO request) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber(request.getPhone()));
        order.setFullname(request.getFullname());
        order.setPhone(request.getPhone());
        order.setEmail(request.getEmail());
        order.setFormat(request.getFormat());
        order.setPaper(request.getPaper());
        order.setQuantity(request.getQuantity());
        order.setPayment(request.getPayment() != null ? request.getPayment() : "Онлайн");
        order.setTotal(request.getTotal());
        order.setFiles(request.getFiles() != null ? request.getFiles() : List.of());
        order.setStatus("Принят");

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
    public Optional<OrderResponseDTO> updateOrderStatus(String orderNumber, String status) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(order -> {
                    order.setStatus(status);
                    return mapToResponse(orderRepository.save(order));
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
        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .fullname(order.getFullname())
                .phone(order.getPhone())
                .email(order.getEmail())
                .format(order.getFormat())
                .paper(order.getPaper())
                .quantity(order.getQuantity())
                .payment(order.getPayment())
                .total(order.getTotal())
                .files(order.getFiles())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}