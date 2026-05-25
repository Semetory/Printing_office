// script.js - исправленная версия

const prices = {
    format: {
        A0: 80,
        A1: 60,
        A2: 40,
        A3: 20,
        A4: 10,
        A5: 8,
        A6: 5
    },
    paper: {
        "Мелованная": 1,
        "Матовая": 1.2,
        "Глянцевая": 1.5,
        "Картон": 2,
        "Дизайнерская": 2.5,
        "Самоклеящаяся": 1.8
    },
    lamination: 15,
    urgentMultiplier: 1.5,
    folding: 5,
    creasing: 7,
    gluing: 10
};

// Получаем элементы с проверкой на существование
const formatSelect = document.getElementById("format");
const paperSelect = document.getElementById("paper");
const quantityInput = document.getElementById("quantity");
const laminationCheckbox = document.getElementById("lamination");
const urgentCheckbox = document.getElementById("urgent");
const totalPriceElement = document.getElementById("totalPrice");
const foldingCheckbox = document.getElementById("folding");
const creasingCheckbox = document.getElementById("creasing");
const gluingCheckbox = document.getElementById("gluing");

// Функция расчёта цены
function calculatePrice() {
    // Проверяем, что все необходимые элементы существуют
    if (!formatSelect || !paperSelect || !quantityInput || !totalPriceElement) {
        console.warn('Не все элементы формы найдены для расчёта цены');
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

// Функция загрузки цены из localStorage
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
        calculatePrice();
    }
}

// Добавляем обработчики ТОЛЬКО если элементы существуют
if (quantityInput) quantityInput.addEventListener("input", calculatePrice);
if (laminationCheckbox) laminationCheckbox.addEventListener("change", calculatePrice);
if (urgentCheckbox) urgentCheckbox.addEventListener("change", calculatePrice);
if (paperSelect) paperSelect.addEventListener("change", calculatePrice);
if (formatSelect) formatSelect.addEventListener("change", calculatePrice);
if (foldingCheckbox) foldingCheckbox.addEventListener("change", calculatePrice);
if (creasingCheckbox) creasingCheckbox.addEventListener("change", calculatePrice);
if (gluingCheckbox) gluingCheckbox.addEventListener("change", calculatePrice);

// Загружаем цену и рассчитываем
loadPricePerSheet();
calculatePrice();

// Экспортируем функцию для использования в других файлах
window.calculatePrice = calculatePrice;