// admin.js - работа с API вместо localStorage

// Защита страницы: Проверяем роль перед показом содержимого
if (localStorage.getItem('userRole') !== 'ADMIN') {
    alert('Доступ запрещен!');
    window.location.href = '/login.html';
}

// Словари для человекочитаемого вывода в админке
const serviceNames = {
    lamination: "Ламинация",
    folding: "Фальцовка",
    creasing: "Биговка",
    gluing: "Склейка",
    urgent: "Срочный заказ"
};

const paperNames = {
    coated: "Мелованная",
    matte: "Матовая",
    glossy: "Глянцевая",
    cardboard: "Картон",
    design: "Дизайнерская",
    sticky: "Самоклеящаяся"
};

async function renderOrders() {
    try {
        const orders = await getAllOrders();
        const tableBody = document.getElementById("ordersTableBody");
        tableBody.innerHTML = "";

        if (!orders || orders.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="11">Заказов нет</td></tr>`;
            return;
        }

        orders.forEach(order => {
            // Формируем список услуг для раскрывающейся панели
            const displayServices = order.services && order.services.length > 0
                ? order.services.map(key => serviceNames[key] || key).join(", ")
                : "Нет доп. услуг";
            const displayPaper = paperNames[order.paper] || order.paper;
            const mainRow = document.createElement("tr");
            mainRow.innerHTML = `
                <td>
                    <button class="toggle-details-btn" onclick="toggleOrderDetails('${order.orderNumber}', '${order.id}')" id="btn-${order.orderNumber}">
                        ▼ Подробнее
                    </button>
                </td>
                <td>${order.orderNumber}</td>
                <td>${order.fullname || ''}</td>
                <td>${order.phone || ''}</td>
                <td>${order.format || ''}</td>
                <td>${order.paper || ''}</td>
                <td>${order.quantity || ''}</td>
                <td><strong>${order.total || 0} ₽</strong></td>
                <td>${order.status || 'Принят'}</td>
                <td>
                    <select class="status-select" onchange="updateOrderStatus('${order.orderNumber}', this.value)">
                        <option value="Принят" ${order.status === "Принят" ? "selected" : ""}>Принят</option>
                        <option value="В печати" ${order.status === "В печати" ? "selected" : ""}>В печати</option>
                        <option value="Готов" ${order.status === "Готов" ? "selected" : ""}>Готов</option>
                        <option value="Выдан" ${order.status === "Выдан" ? "selected" : ""}>Выдан</option>
                    </select>
                </td>
                <td><button class="delete-btn" onclick="deleteOrder('${order.orderNumber}')">Удалить</button></td>
            `;

            // Создаем скрытую строку с деталями (Google-style)
                        const detailsRow = document.createElement("tr");
                        detailsRow.className = "details-row";
                        detailsRow.innerHTML = `
                <td colspan="11" class="details-cell">
                    <div class="details-content" id="details-${order.orderNumber}">
                        <div class="inner-details-grid">
                            <div>
                                <p><strong>Выбранные доп. услуги:</strong> <span style="color: #2980b9;">${displayServices}</span></p>
                                
                                <p><strong>Загруженные файлы макетов:</strong></p>
                                <div class="order-files-container" id="files-container-${order.id}" style="margin-top: 5px; display: flex; flex-direction: column; gap: 5px;">
                                    <span style="color: #888; font-style: italic;">Нажмите "Подробнее" для загрузки файлов...</span>
                                </div>
                            </div>
                            <div>
                                <p><strong>Способ оплаты:</strong> ${order.payment || "Не указан"}</p>
                                <p><strong>Email для связи:</strong> ${order.email || "—"}</p>
                            </div>
                        </div>
                    </div>
                </td>
            `;

            tableBody.appendChild(mainRow);
            tableBody.appendChild(detailsRow);
        });
    } catch (error) {
        console.error('Ошибка загрузки заказов:', error);
        document.getElementById("ordersTableBody").innerHTML = `<tr><td colspan="11">Ошибка загрузки данных</td></tr>`;
    }
}

// Функция анимации раскрытия строки
// Функция анимации раскрытия строки и загрузки файлов из БД
async function toggleOrderDetails(orderNumber, orderId) {
    const contentDiv = document.getElementById(`details-${orderNumber}`);
    const btn = document.getElementById(`btn-${orderNumber}`);
    const filesContainer = document.getElementById(`files-container-${orderId}`);

    if (!contentDiv || !btn) return;

    if (contentDiv.classList.contains("open")) {
        contentDiv.classList.remove("open");
        btn.innerText = "▼ Подробнее";
    } else {
        // Закрываем другие открытые панели
        document.querySelectorAll(".details-content.open").forEach(el => {
            el.classList.remove("open");
            const currentId = el.id.replace("details-", "");
            const currentBtn = document.getElementById(`btn-${currentId}`);
            if (currentBtn) currentBtn.innerText = "▼ Подробнее";
        });

        contentDiv.classList.add("open");
        btn.innerText = "▲ Свернуть";

        // ЛОГИКА ШАГА 3: Запрос файлов с бэкенда при раскрытии
        if (filesContainer) {
            filesContainer.innerHTML = `<span style="color: #3498db;">🔄 Загрузка файлов из базы данных...</span>`;

            try {
                const response = await fetch(`/api/orders/${orderId}/files`);
                if (!response.ok) {
                    throw new Error(`Ошибка сервера: ${response.status}`);
                }

                const files = await response.json();
                filesContainer.innerHTML = ""; // Очищаем статус загрузки

                if (!files || files.length === 0) {
                    filesContainer.innerHTML = `<span style="color: #e74c3c;">⚠️ Клиент не прикрепил файлы</span>`;
                    return;
                }

                // Отрисовываем каждый файл как отдельную ссылку-кнопку
                files.forEach(file => {
                    const downloadLink = document.createElement("a");
                    // Ссылка ведет на контроллер скачивания по уникальному fileId
                    downloadLink.href = `/api/orders/files/download/${file.id}`;
                    downloadLink.className = "file-download-link";
                    downloadLink.innerText = `💾 Скачать: ${file.fileName}`;
                    // Атрибут download подсказывает браузеру сразу качать файл, а не открывать в новой вкладке
                    downloadLink.setAttribute("download", file.fileName);

                    filesContainer.appendChild(downloadLink);
                });

            } catch (error) {
                console.error("Ошибка при получении файлов заказа:", error);
                filesContainer.innerHTML = `<span style="color: #c0392b;">❌ Не удалось загрузить файлы</span>`;
            }
        }
    }
}

async function updateOrderStatus(orderNumber, status) {
    try {
        await updateOrderStatusOnServer(orderNumber, status); // ← добавить OnServer
        await renderOrders();
    } catch (error) {
        alert('Ошибка: ' + error.message);
    }
}

async function deleteOrder(orderNumber) {
    if (!confirm("Удалить заказ?")) return;
    try {
        await deleteOrderOnServer(orderNumber); // ← добавить OnServer
        await renderOrders();
    } catch (error) {
        alert('Ошибка: ' + error.message);
    }
}

document.addEventListener("DOMContentLoaded", () => {
    renderOrders();
});

document.addEventListener("DOMContentLoaded", () => {
    loadPricesTable();
});

// Загрузка цен с сервера и отрисовка таблицы
async function loadPricesTable() {
    try {
        const response = await fetch('/api/admin/prices');
        const prices = await response.json();

        const tbody = document.querySelector('#pricesTable tbody');
        tbody.innerHTML = ''; // Очищаем

        prices.forEach(item => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td style="padding: 10px; border-bottom: 1px solid #eee;">${item.itemName}</td>
                <td style="padding: 10px; border-bottom: 1px solid #eee;">
                    <input type="number" id="input-${item.itemKey}" value="${item.price}" style="width: 80px; padding: 5px; border: 1px solid #ccc; border-radius: 4px;">
                </td>
                <td style="padding: 10px; border-bottom: 1px solid #eee;">
                    <button onclick="savePrice('${item.itemKey}')" class="submit-btn" style="padding: 5px 12px; font-size: 13px; background-color: #2ecc71;">Сохранить</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (err) {
        console.error('Не удалось загрузить таблицу цен:', err);
    }
}

// Отправка измененной цены на сервер
async function savePrice(key) {
    const inputField = document.getElementById(`input-${key}`);
    const newPrice = inputField.value;

    if (!newPrice || newPrice < 0) {
        alert('Цена не может быть пустой или отрицательной!');
        return;
    }

    try {
        const response = await fetch(`/api/admin/prices/${key}?newPrice=${newPrice}`, {
            method: 'PUT'
        });

        if (response.ok) {
            alert('Цена успешно обновлена и применяется для новых заказов!');
            loadPricesTable(); // Перерисовываем
        } else {
            alert('Ошибка при сохранении цены');
        }
    } catch (err) {
        alert('Ошибка связи с сервером: ' + err.message);
    }
}

// Выход из панели
function logout() {
    localStorage.removeItem('userRole');
    window.location.href = '/login.html';
}