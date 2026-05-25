// api.js

const API_BASE_URL = '/api';

// Отправить заказ на сервер
async function sendOrderToServer(orderData) {
    const response = await fetch(`${API_BASE_URL}/orders`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orderData)
    });
    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Ошибка создания заказа');
    }
    return await response.json();
}

// Получить заказ по номеру
async function getOrderByNumber(orderNumber) {
    const response = await fetch(`${API_BASE_URL}/orders/${orderNumber}`);
    if (response.status === 404) return null;
    if (!response.ok) throw new Error('Заказ не найден');
    return await response.json();
}

// Получить все заказы
async function getAllOrders() {
    const response = await fetch(`${API_BASE_URL}/orders`);
    if (!response.ok) throw new Error('Ошибка получения заказов');
    return await response.json();
}

// Обновить статус заказа (переименовано, чтобы не конфликтовать)
async function updateOrderStatusOnServer(orderNumber, status) {
    const response = await fetch(
        `${API_BASE_URL}/orders/${orderNumber}/status?status=${encodeURIComponent(status)}`,
        { method: 'PATCH' }
    );
    if (!response.ok) throw new Error('Ошибка обновления статуса');
    return await response.json();
}

// Удалить заказ
async function deleteOrderOnServer(orderNumber) {
    const response = await fetch(`${API_BASE_URL}/orders/${orderNumber}`, {
        method: 'DELETE'
    });
    if (!response.ok) throw new Error('Ошибка удаления заказа');
    return true;
}

window.sendOrderToServer = sendOrderToServer;
window.getOrderByNumber = getOrderByNumber;
window.getAllOrders = getAllOrders;
window.updateOrderStatusOnServer = updateOrderStatusOnServer;
window.deleteOrderOnServer = deleteOrderOnServer;