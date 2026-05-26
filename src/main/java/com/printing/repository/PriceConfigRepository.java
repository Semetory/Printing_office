package com.printing.repository;

import com.printing.model.PriceConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PriceConfigRepository extends JpaRepository<PriceConfig, String> {
    Optional<PriceConfig> findByItemKey(String itemKey);
}