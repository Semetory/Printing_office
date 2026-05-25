// ============================================
// Файл: js/validation.js
// Валидация формы заказа и маска телефона
// ============================================

// Регулярные выражения для валидации
const validators = {
    // ФИО: от 2 до 50 символов, только буквы, дефис, пробел
    fullname: /^[А-Яа-яЁёA-Za-z\s\-]{2,50}$/,
    
    // Email: стандартный формат
    email: /^[^\s@]+@([^\s@.,]+\.)+[^\s@.,]{2,}$/,
    
    // Количество: целое число от 1 до 10000
    quantity: /^[1-9]\d{0,4}$/
};

// Сообщения об ошибках на русском
const errorMessages = {
    fullname: {
        required: 'Введите ФИО или имя',
        pattern: 'Используйте только буквы, пробелы и дефис (2-50 символов)'
    },
    phone: {
        required: 'Введите номер телефона',
        pattern: 'Введите 10 цифр номера (формат: XXX-XXX-XX-XX)'
    },
    email: {
        required: 'Введите email',
        pattern: 'Введите корректный email (например: name@domain.ru)'
    },
    quantity: {
        required: 'Введите количество экземпляров',
        pattern: 'Введите число от 1 до 10 000',
        min: 'Минимальное количество: 1',
        max: 'Максимальное количество: 10 000'
    }
};

// ========== ФУНКЦИИ ВАЛИДАЦИИ ==========

// Валидация ФИО
function validateFullname(value) {
    const trimmed = value.trim();
    
    if (!trimmed) {
        return { valid: false, message: errorMessages.fullname.required };
    }
    
    if (!validators.fullname.test(trimmed)) {
        return { valid: false, message: errorMessages.fullname.pattern };
    }
    
    return { valid: true, message: '' };
}

// Валидация телефона (ожидаем 10 цифр)
function validatePhone(value) {
    const trimmed = value.trim();
    
    if (!trimmed) {
        return { valid: false, message: errorMessages.phone.required };
    }
    
    // Убираем все не-цифры
    const digits = trimmed.replace(/\D/g, '');
    
    // Проверяем что ровно 10 цифр
    if (digits.length !== 10) {
        return { valid: false, message: errorMessages.phone.pattern };
    }
    
    return { valid: true, message: '', cleanPhone: digits };
}

// Валидация email
function validateEmail(value) {
    const trimmed = value.trim();
    
    if (!trimmed) {
        return { valid: false, message: errorMessages.email.required };
    }
    
    if (!validators.email.test(trimmed)) {
        return { valid: false, message: errorMessages.email.pattern };
    }
    
    return { valid: true, message: '' };
}

// Валидация количества
function validateQuantity(value) {
    const trimmed = value.trim();
    
    if (!trimmed) {
        return { valid: false, message: errorMessages.quantity.required };
    }
    
    if (!validators.quantity.test(trimmed)) {
        return { valid: false, message: errorMessages.quantity.pattern };
    }
    
    const num = parseInt(trimmed);
    if (num < 1) {
        return { valid: false, message: errorMessages.quantity.min };
    }
    
    if (num > 10000) {
        return { valid: false, message: errorMessages.quantity.max };
    }
    
    return { valid: true, message: '', value: num };
}

// Валидация всей формы
function validateForm() {
    let isValid = true;
    
    // Валидация ФИО
    const fullnameInput = document.getElementById('fullname');
    if (fullnameInput) {
        const result = validateFullname(fullnameInput.value);
        if (!result.valid) {
            showError(fullnameInput, result.message);
            isValid = false;
        } else {
            clearError(fullnameInput);
        }
    }
    
    // Валидация телефона
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        const result = validatePhone(phoneInput.value);
        if (!result.valid) {
            showError(phoneInput, result.message);
            isValid = false;
        } else {
            clearError(phoneInput);
        }
    }
    
    // Валидация email
    const emailInput = document.getElementById('email');
    if (emailInput) {
        const result = validateEmail(emailInput.value);
        if (!result.valid) {
            showError(emailInput, result.message);
            isValid = false;
        } else {
            clearError(emailInput);
        }
    }
    
    // Валидация количества
    const quantityInput = document.getElementById('quantity');
    if (quantityInput) {
        const result = validateQuantity(quantityInput.value);
        if (!result.valid) {
            showError(quantityInput, result.message);
            isValid = false;
        } else {
            clearError(quantityInput);
        }
    }
    
    return isValid;
}

// ========== ФУНКЦИИ ДЛЯ ОТОБРАЖЕНИЯ ОШИБОК ==========

// Показать ошибку под полем
function showError(input, message) {
    const formGroup = input.closest('.form-group');
    if (!formGroup) return;
    
    // Удаляем старую ошибку
    const oldError = formGroup.querySelector('.error-message');
    if (oldError) oldError.remove();
    
    // Добавляем класс ошибки
    formGroup.classList.add('error');
    
    // Добавляем сообщение об ошибке
    const errorDiv = document.createElement('div');
    errorDiv.className = 'error-message';
    errorDiv.textContent = message;
    formGroup.appendChild(errorDiv);
}

// Очистить ошибку
function clearError(input) {
    const formGroup = input.closest('.form-group');
    if (!formGroup) return;
    
    formGroup.classList.remove('error');
    
    const error = formGroup.querySelector('.error-message');
    if (error) error.remove();
}

// Отметить успешный ввод
function markSuccess(input) {
    const formGroup = input.closest('.form-group');
    if (!formGroup) return;
    
    formGroup.classList.remove('error');
    const error = formGroup.querySelector('.error-message');
    if (error) error.remove();
}

// ========== МАСКА ТЕЛЕФОНА ==========

// Маска для телефона: формат XXX-XXX-XX-XX
function maskPhone(input) {
    if (!input) return;
    
    // Удаляем все не-цифры
    let value = input.value.replace(/\D/g, '');
    
    // Ограничиваем длину до 10 цифр (3+3+2+2)
    if (value.length > 10) value = value.slice(0, 10);
    
    // Форматируем как XXX-XXX-XX-XX
    let formatted = '';
    if (value.length > 0) {
        formatted = value.slice(0, 3);
        if (value.length >= 4) {
            formatted += '-' + value.slice(3, 6);
        }
        if (value.length >= 7) {
            formatted += '-' + value.slice(6, 8);
        }
        if (value.length >= 9) {
            formatted += '-' + value.slice(8, 10);
        }
    }
    
    input.value = formatted;
}

// Получить чистый номер (только 10 цифр)
function getCleanPhoneNumber(phoneInput) {
    if (!phoneInput) return '';
    const digits = phoneInput.value.replace(/\D/g, '');
    return digits.slice(-10);
}

// ========== НАСТРОЙКА ВАЛИДАЦИИ В РЕАЛЬНОМ ВРЕМЕНИ ==========

// Настройка валидации для всех полей
function setupRealTimeValidation() {
    // Валидация ФИО при вводе
    const fullnameInput = document.getElementById('fullname');
    if (fullnameInput) {
        fullnameInput.addEventListener('input', function() {
            const result = validateFullname(this.value);
            if (result.valid) {
                clearError(this);
                this.style.borderColor = '#27ae60';
            } else {
                showError(this, result.message);
                this.style.borderColor = '#e74c3c';
            }
        });
    }
    
    // Валидация email при вводе
    const emailInput = document.getElementById('email');
    if (emailInput) {
        emailInput.addEventListener('input', function() {
            const result = validateEmail(this.value);
            if (result.valid) {
                clearError(this);
                this.style.borderColor = '#27ae60';
            } else {
                showError(this, result.message);
                this.style.borderColor = '#e74c3c';
            }
        });
    }
    
    // Валидация телефона при вводе с маской
    const phoneInput = document.getElementById('phone');
    if (phoneInput) {
        phoneInput.addEventListener('input', function() {
            // Применяем маску
            maskPhone(this);
            
            const result = validatePhone(this.value);
            if (result.valid) {
                clearError(this);
                this.style.borderColor = '#27ae60';
            } else if (this.value.replace(/\D/g, '').length > 0) {
                showError(this, result.message);
                this.style.borderColor = '#e74c3c';
            } else {
                clearError(this);
            }
        });
        
        // Обработка вставки из буфера обмена
        phoneInput.addEventListener('paste', function(e) {
            e.preventDefault();
            const pastedText = (e.clipboardData || window.clipboardData).getData('text');
            const digits = pastedText.replace(/\D/g, '');
            if (digits.length > 0) {
                const cleanDigits = digits.slice(-10);
                this.value = cleanDigits;
                maskPhone(this);
            }
            this.dispatchEvent(new Event('input'));
        });
    }
    
    // Валидация количества при вводе
    const quantityInput = document.getElementById('quantity');
    if (quantityInput) {
        quantityInput.addEventListener('input', function() {
            const result = validateQuantity(this.value);
            if (result.valid) {
                clearError(this);
                this.style.borderColor = '#27ae60';
            } else if (this.value) {
                showError(this, result.message);
                this.style.borderColor = '#e74c3c';
            } else {
                clearError(this);
            }
        });
    }
}

// ========== ЭКСПОРТ ФУНКЦИЙ ==========

window.validation = {
    // Валидация
    validateFullname,
    validatePhone,
    validateEmail,
    validateQuantity,
    validateForm,
    
    // Отображение ошибок
    showError,
    clearError,
    markSuccess,
    
    // Маска телефона
    maskPhone,
    getCleanPhoneNumber,
    
    // Настройка
    setupRealTimeValidation
};

// Автоматически настраиваем валидацию при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('phone')) {
        setupRealTimeValidation();
    }
});