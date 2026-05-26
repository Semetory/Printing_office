// // script.js

// 1. Цены по умолчанию (Резервные/Бэкап). Если сервер вернет ошибку, калькулятор будет жить.
const prices = {
    format: {
        A0: 150, A1: 90, A2: 45, A3: 20, A4: 10, A5: 7, A6: 4
    },
    paper: {
        coated: 4,      // Мелованная
        matte: 7,       // Матовая
        glossy: 5,      // Глянцевая
        cardboard: 15,  // Картон
        design: 25,     // Дизайнерская
        sticky: 12      // Самоклеящаяся
    },
    lamination: 15,
    folding: 3,
    creasing: 4,
    gluing: 8,
    urgent: 200          // ИЗМЕНЕНИЕ: Базовая фикс. цена в бэкапе (например, 200 ₽ вместо 20)
};

// Переменные для элементов формы
let formatSelect, paperSelect, quantityInput, laminationCheckbox;
let urgentCheckbox, totalPriceElement, foldingCheckbox, creasingCheckbox, gluingCheckbox;

// ФУНКЦИЯ ДЛЯ ЗАГРУЗКИ АКТУАЛЬНЫХ ЦЕН ИЗ БД БЭКЕНДА
async function fetchPricesFromServer() {
    try {
        const response = await fetch('/api/prices');
        if (!response.ok) throw new Error('Ошибка сети при получении прайс-листа');

        const dbPrices = await response.json(); // Получаем массив объектов PriceConfig

        // Проходим циклом по массиву из БД и обновляем наш глобальный объект prices
        dbPrices.forEach(item => {
            const key = item.itemKey; // Например: "A1", "glossy", "urgent"
            const priceValue = item.price; // Например: 90, 5, 200

            // Распределяем по категориям, проверяя ключи форматов и бумаги
            if (['A0', 'A1', 'A2', 'A3', 'A4', 'A5', 'A6'].includes(key)) {
                prices.format[key] = priceValue;
            } else if (['coated', 'matte', 'glossy', 'cardboard', 'design', 'sticky'].includes(key)) {
                prices.paper[key] = priceValue;
            } else {
                // Все остальные ключи — это доп. услуги (lamination, urgent, folding...)
                prices[key] = priceValue;
            }
        });

        console.log("Калькулятор: Цены из базы данных успешно синхронизированы!", prices);
        // Сразу после успешного обновления цен — пересчитываем стоимость на экране
        calculatePrice();

    } catch (error) {
        console.warn("Калькулятор: Не удалось загрузить цены с сервера. Используются дефолтные цены.", error);
    }
}

// 2. Функция расчёта цены (Вариант Б)
function calculatePrice() {
    if (!formatSelect) {
        formatSelect = document.getElementById("format");
        paperSelect = document.getElementById("paper");
        quantityInput = document.getElementById("quantity");
        totalPriceElement = document.getElementById("totalPrice");
        laminationCheckbox = document.getElementById("lamination");
        urgentCheckbox = document.getElementById("urgent");
        foldingCheckbox = document.getElementById("folding");
        creasingCheckbox = document.getElementById("creasing");
        gluingCheckbox = document.getElementById("gluing");
    }

    if (!formatSelect || !paperSelect || !quantityInput || !totalPriceElement) {
        console.warn('Калькулятор: Критические элементы формы еще не найдены в DOM');
        return 0;
    }

    const format = formatSelect.value;
    const paper = paperSelect.value;
    const quantity = parseInt(quantityInput.value) || 1;

    const lamination = laminationCheckbox?.checked || false;
    const urgent = urgentCheckbox?.checked || false;
    const folding = foldingCheckbox?.checked || false;
    const creasing = creasingCheckbox?.checked || false;
    const gluing = gluingCheckbox?.checked || false;

    // Базовые цены формата и бумаги
    let formatPrice = prices.format[format] || 0;
    let paperPrice = prices.paper[paper] || 0;

    // Считаем услуги, которые зависят ОТ ТИРАЖА (за штуку)
    let servicesPricePerPieceSum = 0;
    if (lamination) servicesPricePerPieceSum += prices.lamination;
    if (folding) servicesPricePerPieceSum += prices.folding;
    if (creasing) servicesPricePerPieceSum += prices.creasing;
    if (gluing) servicesPricePerPieceSum += prices.gluing;

    // Считаем фиксированные услуги ЗА ВЕСЬ ЗАКАЗ (Вариант Б)
    let fixedUrgentPrice = 0;
    if (urgent) {
        fixedUrgentPrice = prices.urgent; // Просто берем цену, не умножая на тираж
    }

    // ИТОГОВАЯ ФОРМУЛА ВАРИАНТА Б: (Формат + Бумага + Услуги_за_шт) * Тираж + Фиксированная_Срочность
    let total = ((formatPrice + paperPrice + servicesPricePerPieceSum) * quantity) + fixedUrgentPrice;

    total = Math.round(total);

    // Безопасный вывод стоимости в интерфейс
    if (totalPriceElement.value !== undefined) {
        totalPriceElement.value = total + " ₽";
    } else {
        totalPriceElement.textContent = total + " ₽";
    }

    return total;
}

// 4. Ждем загрузки HTML страницы
document.addEventListener("DOMContentLoaded", function() {
    formatSelect = document.getElementById("format");
    paperSelect = document.getElementById("paper");
    quantityInput = document.getElementById("quantity");
    totalPriceElement = document.getElementById("totalPrice");
    laminationCheckbox = document.getElementById("lamination");
    urgentCheckbox = document.getElementById("urgent");
    foldingCheckbox = document.getElementById("folding");
    creasingCheckbox = document.getElementById("creasing");
    gluingCheckbox = document.getElementById("gluing");

    if (quantityInput) quantityInput.addEventListener("input", calculatePrice);
    if (laminationCheckbox) laminationCheckbox.addEventListener("change", calculatePrice);
    if (urgentCheckbox) urgentCheckbox.addEventListener("change", calculatePrice);
    if (foldingCheckbox) foldingCheckbox.addEventListener("change", calculatePrice);
    if (creasingCheckbox) creasingCheckbox.addEventListener("change", calculatePrice);
    if (gluingCheckbox) gluingCheckbox.addEventListener("change", calculatePrice);

    if (paperSelect) paperSelect.addEventListener("change", calculatePrice);
    if (formatSelect) formatSelect.addEventListener("change", calculatePrice);

    // Запускаем асинхронное скачивание цен с бэкенда при открытии страницы
    fetchPricesFromServer();
});

window.calculatePrice = calculatePrice;