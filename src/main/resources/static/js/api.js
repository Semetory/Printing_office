// api.js

const API_BASE_URL = '/api';

// Отправить заказ на сервер
// Отправить заказ на сервер (Обновленная версия с загрузкой бинарника файла)
async function sendOrderToServer(orderData, fileObject = null) {
    // 1. Отправляем текстовые данные заказа (JSON)
    const response = await fetch(`${API_BASE_URL}/orders`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(orderData)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.message || 'Ошибка создания заказа');
    }

    // Получаем созданный заказ из бэкенда (внутри будет нужный нам генерационный ID базы данных)
    const createdOrder = await response.json();

    // 2. Если заказ создался успешно и пользователь прикрепил файл — отправляем бинарник!
    if (fileObject && fileObject.size > 0) {
        console.log(`[API] Заказ создан с ID: ${createdOrder.id}. Начинаем загрузку файла...`);

        const formData = new FormData();
        formData.append("file", fileObject); // Поле называется "file", как требует @RequestParam("file") на бэкенде

        const uploadResponse = await fetch(`${API_BASE_URL}/orders/${createdOrder.id}/upload`, {
            method: 'POST',
            body: formData // Для FormData браузер сам выставит нужные Multipart заголовки
        });

        if (!uploadResponse.ok) {
            console.error('[API] Ошибка при загрузке бинарного файла на сервер');
            // Не ломаем процесс оформления заказа окончательно, просто предупреждаем в консоли
        } else {
            console.log('[API] Бинарный файл успешно сохранен в PostgreSQL (BYTEA)!');
        }
    }

    // Возвращаем объект созданного заказа исходному коду страницы
    return createdOrder;
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