package com.printing.controller;

import com.printing.model.PriceConfig;
import com.printing.repository.PriceConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Публичный REST-контроллер для предоставления актуальной тарифной сетки.
 * <p>
 * Используется интерактивным калькулятором на фронтенде для асинхронного получения
 * цен на форматы, бумагу и услуги и последующего динамического расчета стоимости на стороне клиента.
 * </p>
 *
 * @author Дмитрий
 * @version 1.0
 */
@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @Autowired
    private PriceConfigRepository priceConfigRepository;

    /**
     * Извлекает из базы данных абсолютно все позиции прайс-листа типографии.
     *
     * @return список объектов {@link PriceConfig}, содержащих системные ключи и цены
     */
    @GetMapping
    public List<PriceConfig> getAllPrices() {
        return priceConfigRepository.findAll();
    }
}