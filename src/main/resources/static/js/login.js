document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const usernameInput = document.getElementById('username').value;
    const passwordInput = document.getElementById('password').value;
    const errorBox = document.getElementById('errorBox');

    errorBox.style.display = 'none';

    try {
        const response = await fetch('/api/admin/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: usernameInput, password: passwordInput })
        });

        if (!response.ok) {
            const text = await response.text();
            throw new Error(text || 'Неверные данные');
        }

        const data = await response.json();

        // Сохраняем сессию в локальное хранилище для проверки доступа
        localStorage.setItem('userRole', data.role);

        // Перенаправляем на целевую страницу
        window.location.href = data.redirectUrl;

    } catch (err) {
        errorBox.textContent = err.message;
        errorBox.style.display = 'block';
    }
});