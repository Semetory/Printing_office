// Защита страницы: Проверяем роль перед показом содержимого
if (localStorage.getItem('userRole') !== 'SYSADMIN') {
    alert('Доступ запрещен!');
    window.location.href = '/login.html';
}

const logBox = document.getElementById('logBox');

function printLog(message, isError = false) {
    logBox.style.color = isError ? '#e74c3c' : '#2c3e50';
    logBox.textContent = `[${new Date().toLocaleTimeString()}] ${message}`;
}

// 1. СКАЧАТЬ БД
document.getElementById('btnDownload').addEventListener('click', async () => {
    printLog('Подготовка дампа базы данных...');
    try {
        window.location.href = '/api/admin/database/download';
        printLog('Запрос на скачивание отправлен.');
    } catch (err) {
        printLog('Ошибка при скачивании: ' + err.message, true);
    }
});

// 2. ЗАГРУЗИТЬ БД
const fileInput = document.getElementById('dbFileInput');
document.getElementById('btnUploadTrigger').addEventListener('click', () => fileInput.click());

fileInput.addEventListener('change', async () => {
    if (!fileInput.files.length) return;

    const file = fileInput.files[0];
    if (!confirm(`Вы уверены, что хотите полностью заменить данные в БД файлом ${file.name}? текущие данные будут удалены!`)) {
        fileInput.value = '';
        return;
    }

    printLog('Загрузка файла бэкапа на сервер...');
    const formData = new FormData();
    formData.append('file', file);

    try {
        const response = await fetch('/api/admin/database/upload', {
            method: 'POST',
            body: formData
        });
        const text = await response.text();

        if (!response.ok) throw new Error(text);

        printLog(text);
    } catch (err) {
        printLog('Ошибка импорта: ' + err.message, true);
    } finally {
        fileInput.value = '';
    }
});

// 3. ОЧИСТИТЬ БД
document.getElementById('btnClear').addEventListener('click', async () => {
    if (!confirm('ВНИМАНИЕ! Это действие полностью удалит все заказы из базы данных без возможности восстановления! Продолжить?')) {
        return;
    }

    printLog('Выполнение очистки таблиц...');
    try {
        const response = await fetch('/api/admin/database/clear', {
            method: 'DELETE'
        });
        const text = await response.text();

        if (!response.ok) throw new Error(text);

        printLog(text);
    } catch (err) {
        printLog('Ошибка очистки: ' + err.message, true);
    }
});

// Выход из панели
function logout() {
    localStorage.removeItem('userRole');
    window.location.href = '/login.html';
}