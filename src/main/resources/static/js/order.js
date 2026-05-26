// order.js

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
            const value = this.dataset.value;  // Русский текст (для интерфейса)
            const key = this.dataset.key;      // Латинский ключ (для бэкенда)

            if (selectedText) selectedText.textContent = value;
            if (hiddenInput) {
                hiddenInput.value = key; // Записываем латинский ключ ("glossy", "matte" и т.д.)
                hiddenInput.dispatchEvent(new Event('change'));
            }

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
            if (hiddenInput) {
                hiddenInput.value = button.dataset.value;
                hiddenInput.dispatchEvent(new Event('change'));
            }
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


// ВСЯ ИНИЦИАЛИЗАЦИЯ И ОБРАБОТКА СТРАНИЦЫ ТЕПЕРЬ ТУТ:
document.addEventListener("DOMContentLoaded", function() {
    const uploadArea = document.getElementById("uploadArea");
    const fileInput = document.getElementById("fileInput");
    const fileList = document.getElementById("fileList");

    // 1. Получение формата из URL
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

    // 2. Инициализация кастомных компонентов формы
    initSlider("paymentSlider", "payment");
    initPaperDropdown();

    // 3. Принудительно считаем цену ПЕРВЫЙ раз после подстановки формата
    if (typeof window.calculatePrice === "function") {
        window.calculatePrice();
    }

    // 4. Обработка загрузки файлов (Drag & Drop и клик)
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
            handleFiles(e.dataTransfer.files, fileList);
        });

        fileInput.addEventListener("change", () => {
            handleFiles(fileInput.files, fileList);
        });
    }

    // 5. Отправка формы (безопасно внутри DOMContentLoaded)
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
                    setTimeout(() => { firstError.style.backgroundColor = ''; }, 1000);
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

            const formatInput = document.getElementById("format");
            const paperInput = document.getElementById("paper");

            const format = formatInput ? formatInput.value.trim() : "A4";
            const paper = paperInput ? paperInput.value.trim() : "coated";
            const payment = document.getElementById("payment")?.value || "Онлайн";
            const total = window.calculatePrice ? window.calculatePrice() : 0;

            const selectedServices = [];
            // Ищем ВСЕ отмеченные чекбоксы, у которых есть атрибут data-key, по всей форме заказа
            const checkboxes = document.querySelectorAll('#orderForm input[type="checkbox"]:checked');
            checkboxes.forEach(cb => {
                if (cb.dataset.key) {
                    selectedServices.push(cb.dataset.key); // Теперь гарантированно соберет и lamination, и gluing
                }
            });

            // Вычисляем чистую стоимость для отправки на сервер без текстовых символов "₽"
            let finalTotal = 0;
            const totalElement = document.getElementById('totalPrice');
            if (totalElement) {
                // Если это input — берем value, если обычный тег — берем textContent или innerText
                const totalString = totalElement.value !== undefined ? totalElement.value : totalElement.textContent;
                finalTotal = parseInt(totalString.replace(/\D/g, '')) || 0;
            }

            const order = {
                fullname: fullname,
                phone: fullPhone,
                email: email,
                format: format,
                paper: paper,
                quantity: parseInt(document.getElementById("quantity").value) || 1,
                payment: payment,
                //total: finalTotal, // Отправляем серверу чистое числовое значение
                files: [...uploadedFiles],
                services: selectedServices,
                status: "Принят"
            };

            if (typeof sendOrderToServer === 'undefined') {
                console.error('sendOrderToServer не определён! Подключите api.js');
                alert('Ошибка: API не подключён. Проверьте подключение файла api.js');
                return;
            }

            try {
                // Находим наш скрытый инпут, куда браузер сохраняет бинарники файлов
                const fileInputEl = document.getElementById("fileInput");
                const physicalFile = fileInputEl && fileInputEl.files.length > 0 ? fileInputEl.files[0] : null;

                // Передаем объект заказа И сам файл в обновленную функцию из api.js
                const savedOrder = await sendOrderToServer(order, physicalFile);
                showOrderNumberModal(savedOrder.orderNumber);
            } catch (error) {
                alert('Ошибка при создании заказа: ' + error.message);
                console.error(error);
            }
        });
    }
});

function handleFiles(files, fileListElement) {
    if (fileListElement) fileListElement.innerHTML = "";
    uploadedFiles = [];

    // Если файлы были перетащены мышкой (Drag & Drop), принудительно синхронизируем их с инпутом
    const fileInputEl = document.getElementById("fileInput");
    if (fileInputEl && fileInputEl.files !== files) {
        fileInputEl.files = files;
    }

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
        if (fileListElement) fileListElement.appendChild(div);
    });
}

// Модальное окно
// Модальное окно и кастомный Toast-нотификатор
function showOrderNumberModal(orderNumber) {
    // 1. Находим элементы, которые уже добавлены внизу страницы order.html
    const modal = document.getElementById('successModal');
    const modalOrderNumber = document.getElementById('modalOrderNumber');
    const btnCopyNumber = document.getElementById('btnCopyNumber');
    const btnCloseModal = document.getElementById('btnCloseModal');

    if (!modal || !modalOrderNumber) {
        // Резервный вариант на случай, если HTML не обновился
        alert('Заказ создан! Номер: ' + orderNumber);
        return;
    }

    // 2. Подставляем сгенерированный номер заказа и отображаем окно (через flex для центрирования)
    modalOrderNumber.innerText = orderNumber;
    modal.style.display = 'flex';

    // 3. Обработка клика по кнопке «Копировать номер» (Заменяем старый alert на красивый Toast)
    btnCopyNumber.onclick = function() {
        navigator.clipboard.writeText(orderNumber).then(() => {
            const toast = document.getElementById('customToast');
            if (toast) {
                toast.style.display = 'block';
                // Мягко прячем уведомление через 2.5 секунды
                setTimeout(() => {
                    toast.style.display = 'none';
                }, 2500);
            }
        }).catch(err => {
            console.error('Не удалось скопировать номер: ', err);
        });
    };

    // 4. Обработка кнопки «Закрыть» (Очищаем форму и сбрасываем калькулятор)
    btnCloseModal.onclick = function() {
        modal.style.display = 'none';

        const form = document.getElementById('orderForm');
        if (form) form.reset();

        const totalPrice = document.getElementById('totalPrice');
        if (totalPrice) totalPrice.value = '0 ₽';

        uploadedFiles = [];
        const fileList = document.getElementById('fileList');
        if (fileList) fileList.innerHTML = '';

        // Сбрасываем выбранный тип бумаги на дефолтный
        const selectedText = document.getElementById("paperSelected");
        const hiddenInput = document.getElementById("paper");
        if (selectedText) selectedText.textContent = "Мелованная";
        if (hiddenInput) hiddenInput.value = "coated";

        // Пересчитываем цену (вернет к базовой)
        if (typeof window.calculatePrice === "function") {
            window.calculatePrice();
        }
    };
}