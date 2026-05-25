// script.js

// 1. Цены объявляем в глобальной видимости сразу
const prices = {
    format: {
        A0: 80, A1: 60, A2: 40, A3: 20, A4: 10, A5: 8, A6: 5
    },
    paper: {
        "Мелованная": 1, "Матовая": 1.2, "Глянцевая": 1.5,
        "Картон": 2, "Дизайнерская": 2.5, "Самоклеящаяся": 1.8
    },
    lamination: 15,
    urgentMultiplier: 1.5,
    folding: 5,
    creasing: 7,
    gluing: 10
};

// Переменные для элементов формы (объявляем тут, значения присвоим при загрузке)
let formatSelect, paperSelect, quantityInput, laminationCheckbox;
let urgentCheckbox, totalPriceElement, foldingCheckbox, creasingCheckbox, gluingCheckbox;

// 2. Функция расчёта цены (доступна везде)
function calculatePrice() {
    // Если DOM еще не загрузился, пытаемся найти элементы повторно
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

    // Проверяем существование критически важных полей
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

    let basePrice = prices.format[format] || 0;
    basePrice *= prices.paper[paper] || 1;
    let total = basePrice * quantity;

    if (lamination) total += prices.lamination * quantity;
    if (folding) total += prices.folding * quantity;
    if (creasing) total += prices.creasing * quantity;
    if (gluing) total += prices.gluing * quantity;
    if (urgent) total *= prices.urgentMultiplier;

    total = Math.round(total);
    totalPriceElement.value = total + " ₽";
    return total;
}

// 3. Функция загрузки цены из localStorage
function loadPricePerSheet() {
    const savedPrice = localStorage.getItem("pricePerSheet");
    if (savedPrice && !isNaN(parseFloat(savedPrice))) {
        prices.format.A4 = parseFloat(savedPrice);
        const ratio = parseFloat(savedPrice) / 10;
        prices.format.A0 = Math.round(80 * ratio);
        prices.format.A1 = Math.round(60 * ratio);
        prices.format.A2 = Math.round(40 * ratio);
        prices.format.A3 = Math.round(20 * ratio);
        prices.format.A5 = Math.round(8 * ratio);
        prices.format.A6 = Math.round(5 * ratio);
    }
}

// 4. Ждем полной загрузки HTML страницы, чтобы привязать события
document.addEventListener("DOMContentLoaded", function() {
    // Находим элементы на странице
    formatSelect = document.getElementById("format");
    paperSelect = document.getElementById("paper");
    quantityInput = document.getElementById("quantity");
    totalPriceElement = document.getElementById("totalPrice");
    laminationCheckbox = document.getElementById("lamination");
    urgentCheckbox = document.getElementById("urgent");
    foldingCheckbox = document.getElementById("folding");
    creasingCheckbox = document.getElementById("creasing");
    gluingCheckbox = document.getElementById("gluing");

    // Добавляем обработчики событий
    if (quantityInput) quantityInput.addEventListener("input", calculatePrice);
    if (laminationCheckbox) laminationCheckbox.addEventListener("change", calculatePrice);
    if (urgentCheckbox) urgentCheckbox.addEventListener("change", calculatePrice);
    if (foldingCheckbox) foldingCheckbox.addEventListener("change", calculatePrice);
    if (creasingCheckbox) creasingCheckbox.addEventListener("change", calculatePrice);
    if (gluingCheckbox) gluingCheckbox.addEventListener("change", calculatePrice);

    // Слушаем изменения на скрытых или текстовых полях
    if (paperSelect) paperSelect.addEventListener("change", calculatePrice);
    if (formatSelect) formatSelect.addEventListener("change", calculatePrice);

    // Загружаем цены бэкапа и делаем первичный расчет
    loadPricePerSheet();
    calculatePrice();
});

// Экспортируем функцию в объект window для order.js
window.calculatePrice = calculatePrice;