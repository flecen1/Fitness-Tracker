document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const inputs = document.querySelectorAll('input');
    const loginButton = document.querySelector('button[type="submit"]');
    
    // Добавляем эффекты для уведомлений
    const alerts = document.querySelectorAll('.alert');
    
    alerts.forEach(alert => {
        if (alert.textContent.trim() !== '') {
            setTimeout(() => {
                alert.classList.add('show');
            }, 100);
            
            // Если это успешный выход - скрываем через 5 секунд
            if (alert.style.backgroundColor === 'rgb(46, 204, 113)') {
                setTimeout(() => {
                    alert.style.opacity = '0';
                    setTimeout(() => {
                        alert.style.display = 'none';
                    }, 300);
                }, 5000);
            }
        } else {
            alert.style.display = 'none';
        }
    });
    
    // Добавляем эффект фокуса для полей ввода
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
        });
        
        input.addEventListener('blur', function() {
            if (!this.value) {
                this.parentElement.classList.remove('focused');
            }
        });
    });
    
    // Анимация загрузки при отправке формы
    form.addEventListener('submit', function(e) {
        if (form.checkValidity()) {
            const originalText = loginButton.textContent;
            
            loginButton.disabled = true;
            loginButton.innerHTML = '<span class="loading">Вход...</span>';
            
            // Сохраняем оригинальный текст для восстановления в случае ошибки сети
            // (это для безопасности, т.к. форма будет отправлена и страница перезагружена)
            sessionStorage.setItem('loginButtonText', originalText);
        }
    });
    
    // Восстанавливаем текст кнопки, если произошла ошибка сети и страница не перезагрузилась
    window.addEventListener('load', function() {
        const savedText = sessionStorage.getItem('loginButtonText');
        if (savedText && loginButton) {
            loginButton.disabled = false;
            loginButton.textContent = savedText;
            sessionStorage.removeItem('loginButtonText');
        }
    });
    
    // Простая валидация формы
    form.addEventListener('submit', function(e) {
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        if (!username || !password) {
            e.preventDefault();
            
            const errorDiv = document.createElement('div');
            errorDiv.className = 'alert';
            errorDiv.textContent = 'Пожалуйста, заполните все поля';
            
            // Проверяем, есть ли уже сообщение об ошибке
            const existingError = document.querySelector('.alert');
            if (existingError) {
                existingError.textContent = 'Пожалуйста, заполните все поля';
                existingError.style.display = 'block';
            } else {
                form.insertBefore(errorDiv, form.firstChild);
            }
            
            setTimeout(() => {
                if (existingError) {
                    existingError.classList.add('show');
                } else {
                    errorDiv.classList.add('show');
                }
            }, 10);
        }
    });
}); 