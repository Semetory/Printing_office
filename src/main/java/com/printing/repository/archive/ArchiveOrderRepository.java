package com.printing.repository.archive;

import com.printing.model.archive.ArchiveOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArchiveOrderRepository extends JpaRepository<ArchiveOrder, Long> {
    // Здесь при необходимости можно будет описывать методы поиска по архивной БД
    // Например: Optional<ArchiveOrder> findByOrderNumber(String orderNumber);
}