document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('register-form');
    const errorAlert = document.getElementById('error-alert');
    const inputs = document.querySelectorAll('input');
    
    // Добавляем эффект фокуса для инпутов
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
        });
        
        input.addEventListener('blur', function() {
            this.parentElement.classList.remove('focused');
        });
    });
    
    form.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Сбрасываем ошибки
        errorAlert.style.display = 'none';
        document.querySelectorAll('.validation-error').forEach(el => el.textContent = '');
        
        // Собираем данные формы
        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const passwordConfirm = document.getElementById('password-confirm').value;
        
        // Валидация
        let isValid = true;
        
        if (!username) {
            showError('username-error', 'Введите имя пользователя');
            isValid = false;
        }
        
        if (!email) {
            showError('email-error', 'Введите email');
            isValid = false;
        } else if (!isValidEmail(email)) {
            showError('email-error', 'Введите корректный email');
            isValid = false;
        }
        
        if (!password) {
            showError('password-error', 'Введите пароль');
            isValid = false;
        } else if (password.length < 6) {
            showError('password-error', 'Пароль должен содержать минимум 6 символов');
            isValid = false;
        }
        
        if (password !== passwordConfirm) {
            showError('password-confirm-error', 'Пароли не совпадают');
            isValid = false;
        }
        
        if (!isValid) return;
        
        // Показываем анимацию загрузки
        const button = form.querySelector('button');
        const originalText = button.textContent;
        button.disabled = true;
        button.textContent = 'Регистрация...';
        
        // Отправляем запрос на регистрацию
        fetch('/api/users/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                email: email,
                password: password
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Добавляем анимацию успеха
                button.textContent = 'Успешно!';
                button.classList.add('success');
                
                // Сохраняем email для страницы верификации
                localStorage.setItem('registration_email', email);
                
                // Перенаправляем на страницу верификации после небольшой задержки
                setTimeout(() => {
                    window.location.href = '/verification?username=' + encodeURIComponent(username);
                }, 1000);
            } else {
                // Восстанавливаем кнопку
                button.disabled = false;
                button.textContent = originalText;
                
                // Показываем сообщение об ошибке
                showAlert(data.message || 'Произошла ошибка при регистрации');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            
            // Восстанавливаем кнопку
            button.disabled = false;
            button.textContent = originalText;
            
            showAlert('Произошла ошибка при отправке запроса');
        });
    });
    
    function showError(elementId, message) {
        const element = document.getElementById(elementId);
        element.textContent = message;
        element.style.opacity = '0';
        
        setTimeout(() => {
            element.style.transition = 'opacity 0.3s ease';
            element.style.opacity = '1';
        }, 10);
    }
    
    function showAlert(message) {
        errorAlert.textContent = message;
        errorAlert.style.display = 'block';
        errorAlert.style.opacity = '0';
        errorAlert.style.transform = 'translateY(-20px)';
        
        setTimeout(() => {
            errorAlert.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            errorAlert.style.opacity = '1';
            errorAlert.style.transform = 'translateY(0)';
        }, 10);
        
        // Прокручиваем к сообщению об ошибке
        errorAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
    
    function isValidEmail(email) {
        const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        return regex.test(email);
    }
}); 