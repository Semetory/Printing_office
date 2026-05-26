// status.js

document.addEventListener("DOMContentLoaded", () => {
    const checkStatusBtn = document.getElementById("checkStatusBtn");
    const statusInput = document.getElementById("statusInput");
    const statusResult = document.getElementById("statusResult");

    if (!checkStatusBtn || !statusInput || !statusResult) {
        console.error("Не найдены элементы интерфейса на странице status.html");
        return;
    }

    // Функция проверки статуса
    async function checkOrderStatus() {
        const orderNumber = statusInput.value.trim();

        if (!orderNumber) {
            statusResult.innerHTML = `<p style="color: #e74c3c;">Пожалуйста, введите номер заказа.</p>`;
            return;
        }

        statusResult.innerHTML = `<p style="color: #3498db;">Поиск заказа...</p>`;

        if (typeof window.getOrderByNumber !== "function" && typeof getOrderByNumber !== "function") {
            statusResult.innerHTML = `<p style="color: #e74c3c;">Ошибка: API модуль (api.js) не подключен или поврежден.</p>`;
            return;
        }

        try {
            const order = await getOrderByNumber(orderNumber);

            // ИСПРАВЛЕНИЕ: Если заказ действительно НЕ найден в БД
            if (!order) {
                statusResult.innerHTML = `<p style="color: #e74c3c;">Заказ с номером <strong>${orderNumber}</strong> не найден.</p>`;
                return;
            }

            // ЛОГИКА ДЛЯ НАЙДЕННОГО ЗАКАЗА:
            // Переводим латинские ключи в читаемый вид для клиента
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

            const displayPaper = paperNames[order.paper] || order.paper;

            // Собираем доп. услуги
            const displayServices = order.services && order.services.length > 0
                ? order.services.map(key => serviceNames[key] || key).join(", ")
                : "Не выбраны";

            // Выводим обновленную, красивую карточку со всеми параметрами заказа
            statusResult.innerHTML = `
                <div style="padding: 20px; background: #f4f9f4; border-radius: 12px; border: 1px solid #c3e6cb; margin-top: 15px; text-align: left;">
                    <h3 style="color: #2c3e50; margin-top: 0;">Информация о заказе</h3>
                    <p><strong>Номер:</strong> ${orderNumber}</p>
                    <p><strong>Клиент:</strong> ${order.fullname || 'Не указано'}</p>
                    <p><strong>Параметры:</strong> Формат ${order.format}, бумага: ${displayPaper}</p>
                    <p><strong>Доп. услуги:</strong> <span style="color: #2980b9; font-weight: 500;">${displayServices}</span></p>
                    <p><strong>Тираж:</strong> ${order.quantity} шт.</p>
                    <p><strong>Сумма:</strong> <span style="font-weight: bold; color: #2ecc71; font-size: 18px;">${order.total} ₽</span></p>
                    <p><strong>Дата создания:</strong> ${order.createdAt ? new Date(order.createdAt).toLocaleString() : '—'}</p>
                    <hr style="border: 0; border-top: 1px solid #c3e6cb; margin: 15px 0;">
                    <p style="font-size: 18px; margin-bottom: 0;">
                        <strong>Статус:</strong> 
                        <span style="padding: 5px 12px; background: #2ecc71; color: white; border-radius: 20px; font-weight: bold; font-size: 14px; display: inline-block; margin-left: 5px;">
                            ${order.status || 'Обрабатывается'}
                        </span>
                    </p>
                </div>
            `;

        } catch (error) {
            console.error(error);
            statusResult.innerHTML = `<p style="color: #e74c3c;">Ошибка при обращении к серверу: ${error.message}</p>`;
        }
    }

    checkStatusBtn.addEventListener("click", checkOrderStatus);

    statusInput.addEventListener("keypress", (e) => {
        if (e.key === "Enter") {
            checkOrderStatus();
        }
    });
});