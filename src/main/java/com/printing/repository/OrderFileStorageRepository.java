package com.printing.repository;

import com.printing.model.OrderFileStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderFileStorageRepository extends JpaRepository<OrderFileStorage, Long> {
    // Найти все файлы, привязанные к конкретному заказу
    List<OrderFileStorage> findByOrderId(Long orderId);
}