package com.printing.controller;

import com.printing.model.PriceConfig;
import com.printing.repository.PriceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/prices") // Этот URL фронтенд будет вызывать через fetch()
public class PriceController {

    @Autowired
    private PriceConfigRepository priceConfigRepository;

    @GetMapping
    public List<PriceConfig> getAllPrices() {
        // Запрашиваем абсолютно все строки с ценами из таблицы конфигурации цен
        return priceConfigRepository.findAll();
    }
}