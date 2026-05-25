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

        // Проверяем, доступна ли функция из api.js
        if (typeof window.getOrderByNumber !== "function" && typeof getOrderByNumber !== "function") {
            statusResult.innerHTML = `<p style="color: #e74c3c;">Ошибка: API модуль (api.js) не подключен или поврежден.</p>`;
            return;
        }

        try {
            // Вызываем функцию из api.js
            const order = await getOrderByNumber(orderNumber);

            if (!order) {
                statusResult.innerHTML = `
                    <div style="padding: 15px; background: #fdf2f2; border-radius: 8px; border: 1px solid #f8b4b4; margin-top: 15px;">
                        <p style="color: #e74c3c; font-weight: bold; margin: 0;">Заказ №${orderNumber} не найден</p>
                        <p style="color: #666; font-size: 14px; margin: 5px 0 0 0;">Проверьте правильность ввода номера.</p>
                    </div>
                `;
                return;
            }

            // Если заказ найден, выводим красивую карточку со статусом
            statusResult.innerHTML = `
                <div style="padding: 20px; background: #f4f9f4; border-radius: 12px; border: 1px solid #c3e6cb; margin-top: 15px; text-align: left;">
                    <h3 style="color: #2c3e50; margin-top: 0;">Информация о заказе</h3>
                    <p><strong>Номер:</strong> ${orderNumber}</p>
                    <p><strong>Клиент:</strong> ${order.fullname || 'Не указано'}</p>
                    <p><strong>Услуга:</strong> Печать (${order.format}, бумага: ${order.paper})</p>
                    <p><strong>Тираж:</strong> ${order.quantity} шт.</p>
                    <p><strong>Сумма:</strong> <span style="font-weight: bold; color: #2ecc71;">${order.total} ₽</span></p>
                    <p><strong>Дата создания:</strong> ${order.createdAt || '—'}</p>
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

    // Вешаем событие на клик по кнопке
    checkStatusBtn.addEventListener("click", checkOrderStatus);

    // Дополнительно: проверка по нажатию Enter в текстовом поле
    statusInput.addEventListener("keypress", (e) => {
        if (e.key === "Enter") {
            checkOrderStatus();
        }
    });
});