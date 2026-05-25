// admin.js - работа с API вместо localStorage

async function renderOrders() {
    try {
        const orders = await getAllOrders();
        const tableBody = document.getElementById("ordersTableBody");
        tableBody.innerHTML = "";

        if (!orders || orders.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="10">Заказов нет</td></tr>`;
            return;
        }

        orders.forEach(order => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${order.orderNumber}</td>
                <td>${order.fullname || ''}</td>
                <td>${order.phone || ''}</td>
                <td>${order.format || ''}</td>
                <td>${order.paper || ''}</td>
                <td>${order.quantity || ''}</td>
                <td>${order.total || 0} ₽</td>
                <td>${order.status || 'Принят'}</td>
                <td>
                    <select onchange="updateOrderStatus('${order.orderNumber}', this.value)">
                        <option value="Принят" ${order.status === "Принят" ? "selected" : ""}>Принят</option>
                        <option value="В печати" ${order.status === "В печати" ? "selected" : ""}>В печати</option>
                        <option value="Готов" ${order.status === "Готов" ? "selected" : ""}>Готов</option>
                        <option value="Выдан" ${order.status === "Выдан" ? "selected" : ""}>Выдан</option>
                    </select>
                </td>
                <td><button onclick="deleteOrder('${order.orderNumber}')">Удалить</button></td>
            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Ошибка загрузки заказов:', error);
        document.getElementById("ordersTableBody").innerHTML = `<tr><td colspan="10">Ошибка загрузки</td></tr>`;
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

document.getElementById("savePriceBtn")?.addEventListener("click", () => {
    const price = document.getElementById("pricePerSheet").value;
    localStorage.setItem("pricePerSheet", price);
    alert("Цена сохранена");
});

function loadCurrentPrice() {
    const price = localStorage.getItem("pricePerSheet");
    if (price) {
        document.getElementById("pricePerSheet").value = price;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    renderOrders();
    loadCurrentPrice();
});