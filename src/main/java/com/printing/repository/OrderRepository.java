package com.printing.repository;

import com.printing.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.printing.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderNumber(String orderNumber);

    // Этот метод Spring Data соберет автоматически по названию:
    List<Order> findByStatusAndStatusUpdatedAtBefore(String status, LocalDateTime threshold);
}