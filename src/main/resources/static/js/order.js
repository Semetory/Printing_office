// order.js - исправленная версия

let uploadedFiles = [];

function initPaperDropdown() {
    const dropdown = document.getElementById("paperDropdown");
    const toggle = document.getElementById("paperToggle");
    const menu = document.getElementById("paperMenu");
    const selectedText = document.getElementById("paperSelected");
    const hiddenInput = document.getElementById("paper");
    const items = menu?.querySelectorAll(".dropdown-item");

    if (!dropdown || !toggle || !menu) return;

    toggle.addEventListener("click", function () {
        dropdown.classList.toggle("open");
    });

    items?.forEach(item => {
        item.addEventListener("click", function () {
            const value = this.dataset.value;

            if (selectedText) selectedText.textContent = value;
            if (hiddenInput) hiddenInput.value = value;

            if (typeof window.calculatePrice === "function") {
                window.calculatePrice();
            }

            items.forEach(i => i.classList.remove("active"));
            this.classList.add("active");
            dropdown.classList.remove("open");
        });
    });

    document.addEventListener("click", function (e) {
        if (dropdown && !dropdown.contains(e.target)) {
            dropdown.classList.remove("open");
        }
    });
}

function initSlider(sliderId, hiddenInputId) {
    const slider = document.getElementById(sliderId);
    const options = slider?.querySelectorAll(".slider-option");
    const highlight = slider?.querySelector(".slider-highlight");
    const hiddenInput = document.getElementById(hiddenInputId);

    if (!slider || !highlight) return;

    function moveHighlight(activeButton) {
        highlight.style.width = activeButton.offsetWidth + "px";
        highlight.style.left = activeButton.offsetLeft + "px";
    }

    options?.forEach(button => {
        button.addEventListener("click", function () {
            options.forEach(btn => btn.classList.remove("active"));
            button.classList.add("active");
            if (hiddenInput) hiddenInput.value = button.dataset.value;
            moveHighlight(button);
        });
    });

    const activeButton = slider.querySelector(".slider-option.active");
    if (activeButton) {
        setTimeout(() => moveHighlight(activeButton), 50);
    }
}

const allowedTypes = [
    "application/pdf",
    "image/jpeg",
    "image/png",
    "image/tiff"
];

const maxFileSize = 50 * 1024 * 1024;

const uploadArea = document.getElementById("uploadArea");
const fileInput = document.getElementById("fileInput");
const fileList = document.getElementById("fileList");

// Получение формата из URL
const params = new URLSearchParams(window.location.search);
const validFormats = ['A0', 'A1', 'A2', 'A3', 'A4', 'A5', 'A6'];
let selectedFormat = params.get("format");
if (!validFormats.includes(selectedFormat)) {
    selectedFormat = "A4";
}
const formatInput = document.getElementById("format");
if (formatInput) {
    formatInput.value = selectedFormat;
} else {
    console.error('Элемент #format не найден на странице!');
}

// Инициализация
initSlider("paymentSlider", "payment");
initPaperDropdown();

// Обработка загрузки файлов
if (uploadArea && fileInput) {
    uploadArea.addEventListener("click", () => fileInput.click());

    uploadArea.addEventListener("dragover", e => {
        e.preventDefault();
        uploadArea.classList.add("drag");
    });

    uploadArea.addEventListener("dragleave", () => {
        uploadArea.classList.remove("drag");
    });

    uploadArea.addEventListener("drop", e => {
        e.preventDefault();
        uploadArea.classList.remove("drag");
        handleFiles(e.dataTransfer.files);
    });

    fileInput.addEventListener("change", () => {
        handleFiles(fileInput.files);
    });
}

function handleFiles(files) {
    if (fileList) fileList.innerHTML = "";
    uploadedFiles = [];
    Array.from(files).forEach(file => {
        let error = "";
        if (!allowedTypes.includes(file.type)) {
            error = "Недопустимый формат";
        }
        if (file.size > maxFileSize) {
            error = "Файл больше 50 МБ";
        }
        const div = document.createElement("div");
        div.classList.add("file-item");
        if (error) {
            div.classList.add("file-error");
            div.textContent = `${file.name} — ${error}`;
        } else {
            uploadedFiles.push(file.name);
            div.textContent = `${file.name} ✅ загружен`;
        }
        if (fileList) fileList.appendChild(div);
    });
}

// Модальное окно
function showOrderNumberModal(orderNumber) {
    const modal = document.createElement('div');
    modal.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0,0,0,0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 10000;
    `;

    const modalContent = document.createElement('div');
    modalContent.style.cssText = `
        background: white;
        padding: 30px;
        border-radius: 16px;
        text-align: center;
        max-width: 400px;
        box-shadow: 0 10px 40px rgba(0,0,0,0.2);
    `;

    modalContent.innerHTML = `
        <h2 style="color: #2c3e50; margin-bottom: 20px;">✅ Заказ создан!</h2>
        <p style="font-size: 18px; margin-bottom: 10px;">Ваш номер заказа:</p>
        <p style="font-size: 32px; font-weight: bold; color: #3498db; margin: 15px 0; letter-spacing: 2px;">${orderNumber}</p>
        <p style="color: #666; margin-bottom: 20px;">Сохраните его для отслеживания статуса</p>
        <button id="copyOrderBtn" style="background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 8px; cursor: pointer; margin-right: 10px;">📋 Копировать</button>
        <button id="closeModalBtn" style="background: #95a5a6; color: white; border: none; padding: 10px 20px; border-radius: 8px; cursor: pointer;">Остаться на странице</button>
        <button id="goToStatusBtn" style="background: #2ecc71; color: white; border: none; padding: 10px 20px; border-radius: 8px; cursor: pointer; margin-top: 10px;">📊 Проверить статус</button>
    `;

    modal.appendChild(modalContent);
    document.body.appendChild(modal);

    document.getElementById('copyOrderBtn')?.addEventListener('click', () => {
        navigator.clipboard.writeText(orderNumber);
        alert('Номер заказа скопирован!');
    });

    document.getElementById('closeModalBtn')?.addEventListener('click', () => {
        modal.remove();
        const form = document.getElementById('orderForm');
        if (form) form.reset();
        const totalPrice = document.getElementById('totalPrice');
        if (totalPrice) totalPrice.value = '0 ₽';
        uploadedFiles = [];
        if (fileList) fileList.innerHTML = '';
    });

    document.getElementById('goToStatusBtn')?.addEventListener('click', () => {
        window.location.href = "status.html";
    });

    modal.addEventListener('click', (e) => {
        if (e.target === modal) modal.remove();
    });
}

// Отправка формы
const orderForm = document.getElementById("orderForm");
if (orderForm) {
    orderForm.addEventListener("submit", async function(e) {
        e.preventDefault();

        if (!window.validation?.validateForm()) {
            const firstError = document.querySelector('.form-group.error');
            if (firstError) {
                firstError.scrollIntoView({ behavior: 'smooth', block: 'center' });
                firstError.style.transition = 'all 0.3s';
                firstError.style.backgroundColor = '#fff5f5';
                setTimeout(() => {
                    firstError.style.backgroundColor = '';
                }, 1000);
            }
            return;
        }

        const countryCode = document.getElementById("countryCode")?.value || "+7";
        const phoneInput = document.getElementById("phone");

        if (!phoneInput) {
            alert('Поле телефона не найдено');
            return;
        }

        const phoneResult = window.validation?.validatePhone(phoneInput.value);
        if (!phoneResult?.valid) {
            window.validation?.showError(phoneInput, phoneResult?.message || 'Неверный номер');
            phoneInput.focus();
            return;
        }

        const fullPhone = countryCode + phoneInput.value.replace(/\D/g, '');
        const quantityInput = document.getElementById("quantity");
        const quantityResult = window.validation?.validateQuantity(quantityInput?.value || "");

        if (!quantityResult?.valid) {
            window.validation?.showError(quantityInput, quantityResult?.message || 'Неверное количество');
            quantityInput?.focus();
            return;
        }

        const fullname = document.getElementById("fullname")?.value.trim() || "";
        const email = document.getElementById("email")?.value.trim() || "";
        const format = document.getElementById("format")?.value || "A4";
        const paper = document.getElementById("paper")?.value || "Мелованная";
        const payment = document.getElementById("payment")?.value || "Онлайн";
        const total = window.calculatePrice ? window.calculatePrice() : 0;

        const order = {
            fullname: fullname,
            phone: fullPhone,
            email: email,
            format: format,
            paper: paper,
            quantity: quantityResult.value,
            payment: payment,
            total: total,
            files: [...uploadedFiles],
            status: "Принят",
            createdAt: new Date().toLocaleString()
        };

        if (typeof sendOrderToServer === 'undefined') {
            console.error('sendOrderToServer не определён! Подключите api.js');
            alert('Ошибка: API не подключён. Проверьте подключение файла api.js');
            return;
        }

        try {
            const savedOrder = await sendOrderToServer(order);
            showOrderNumberModal(savedOrder.orderNumber);
        } catch (error) {
            alert('Ошибка при создании заказа: ' + error.message);
            console.error(error);
        }
    });
}

function generateOrderNumber(phone) {
    const digits = phone.replace(/\D/g, "");
    const lastFour = digits.slice(-4) || "0000";

    const now = new Date();
    const timestamp = now.getFullYear().toString().slice(-2) +
        (now.getMonth() + 1).toString().padStart(2, '0') +
        now.getDate().toString().padStart(2, '0') +
        now.getHours().toString().padStart(2, '0') +
        now.getMinutes().toString().padStart(2, '0') +
        now.getSeconds().toString().padStart(2, '0');

    const random = Math.floor(Math.random() * 1000).toString().padStart(3, '0');

    return `${lastFour}${timestamp}${random}`;
}