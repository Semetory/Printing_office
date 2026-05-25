const statusInput = document.getElementById("statusInput");
const checkStatusBtn = document.getElementById("checkStatusBtn");
const statusResult = document.getElementById("statusResult");

checkStatusBtn.addEventListener("click", checkOrderStatus);

async function checkOrderStatus() {

    const orderNumber = statusInput.value.trim();

    if (!orderNumber) {
        alert("Введите номер заказа");
        return;
    }

    const order = await getOrderByNumber(orderNumber);

    // Если найден
    if (order) {
        showOrder(order);
    } else {
        statusResult.innerHTML = `
            <div class="file-item file-error">
                Заказ не найден
            </div>
        `;
    }
}

function showOrder(order) {

    statusResult.innerHTML = `
        <div class="order-form">

            <h3>Информация о заказе</h3>

            <p><strong>Номер:</strong> ${order.orderNumber}</p>
            <p><strong>ФИО:</strong> ${order.fullname}</p>
            <p><strong>Телефон:</strong> ${order.phone}</p>

            <p><strong>Формат:</strong> ${order.format}</p>
            <p><strong>Бумага:</strong> ${order.paper}</p>
            <p><strong>Количество:</strong> ${order.quantity}</p>

            <p><strong>Стоимость:</strong> ${order.total} ₽</p>
            <p><strong>Статус:</strong> ${order.status}</p>
            <p><strong>Дата:</strong> ${order.createdAt}</p>

            <h4>Файлы:</h4>

            <ul>
				${order.files?.length
					? order.files.map(fileName => `<li>${fileName}</li>`).join("")
					: "<li>Файлы не загружены</li>"
				}
            </ul>

        </div>
    `;
}

statusInput.addEventListener("keypress", function (e) {
    if (e.key === "Enter") { checkOrderStatus(); }
});