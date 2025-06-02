document.addEventListener('DOMContentLoaded', function() {
    // Получаем username из URL параметров
    const urlParams = new URLSearchParams(window.location.search);
    const username = urlParams.get('username');
    
    const errorAlert = document.getElementById('error-alert');
    const successAlert = document.getElementById('success-alert');
    const codeInputs = document.querySelectorAll('.code-input input');
    const verifyButton = document.getElementById('verify-button');
    const resendLink = document.getElementById('resend-link');
    
    // Фокус на первом поле кода при загрузке
    setTimeout(() => {
        codeInputs[0].focus();
    }, 800); // Задержка для анимации появления формы
    
    // Функция для показа сообщения об ошибке с анимацией
    function showError(message) {
        errorAlert.textContent = message;
        errorAlert.style.display = 'block';
        errorAlert.style.opacity = '0';
        errorAlert.style.transform = 'translateY(-10px)';
        
        successAlert.style.display = 'none';
        
        setTimeout(() => {
            errorAlert.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            errorAlert.style.opacity = '1';
            errorAlert.style.transform = 'translateY(0)';
        }, 10);
    }
    
    // Функция для показа сообщения об успехе с анимацией
    function showSuccess(message) {
        successAlert.textContent = message || 'Верификация прошла успешно! Перенаправление...';
        successAlert.style.display = 'block';
        successAlert.style.opacity = '0';
        successAlert.style.transform = 'translateY(-10px)';
        
        errorAlert.style.display = 'none';
        
        setTimeout(() => {
            successAlert.style.transition = 'opacity 0.3s ease, transform 0.3s ease';
            successAlert.style.opacity = '1';
            successAlert.style.transform = 'translateY(0)';
            successAlert.classList.add('success-animation');
        }, 10);
    }
    
    // Проверяем username
    if (!username) {
        showError('Ошибка: имя пользователя не указано');
    }
    
    // Настраиваем эффекты для полей ввода кода
    codeInputs.forEach((input, index) => {
        // При изменении помечаем поле как заполненное
        input.addEventListener('input', function(e) {
            if (this.value) {
                this.classList.add('filled');
            } else {
                this.classList.remove('filled');
            }
            
            // Автоматический переход к следующему полю
            if (this.value && index < codeInputs.length - 1) {
                codeInputs[index + 1].focus();
            }
            
            // Проверка заполнения всех полей
            checkAllFieldsFilled();
        });
        
        // Обработка нажатий клавиш
        input.addEventListener('keydown', function(e) {
            // Перемещение назад по Backspace
            if (e.key === 'Backspace' && !this.value && index > 0) {
                codeInputs[index - 1].focus();
                codeInputs[index - 1].select();
                e.preventDefault();
            }
            
            // Предотвращаем ввод нецифровых значений
            if (!/^[0-9]$/.test(e.key) && 
                e.key !== 'Backspace' &&
                e.key !== 'Tab' &&
                e.key !== 'ArrowLeft' &&
                e.key !== 'ArrowRight') {
                e.preventDefault();
            }
        });
        
        // Анимации при фокусе
        input.addEventListener('focus', function() {
            this.style.transform = 'translateY(-2px)';
            this.style.boxShadow = '0 4px 12px rgba(46, 139, 87, 0.15)';
        });
        
        input.addEventListener('blur', function() {
            this.style.transform = '';
            this.style.boxShadow = '';
        });
    });
    
    // Вставка из буфера для быстрого копирования кода
    document.addEventListener('paste', function(e) {
        // Получаем текст из буфера обмена
        const clipboardData = e.clipboardData || window.clipboardData;
        const pastedData = clipboardData.getData('Text').trim();
        
        // Проверяем, что вставленный текст - 6 цифр
        if (/^\d{6}$/.test(pastedData)) {
            e.preventDefault();
            
            // Заполняем поля формы цифрами из скопированного кода
            for (let i = 0; i < Math.min(6, pastedData.length); i++) {
                codeInputs[i].value = pastedData[i];
                codeInputs[i].classList.add('filled');
            }
            
            // Фокус на последнем поле
            codeInputs[Math.min(5, pastedData.length) - 1].focus();
            
            // Проверяем заполнение всех полей
            checkAllFieldsFilled();
        }
    });
    
    // Функция для проверки заполнения всех полей
    function checkAllFieldsFilled() {
        const allFilled = Array.from(codeInputs).every(input => input.value.trim() !== '');
        
        if (allFilled) {
            verifyButton.classList.add('ready');
            
            // Для небольшой подсказки пользователю
            setTimeout(() => {
                verifyButton.classList.add('pulse');
                setTimeout(() => {
                    verifyButton.classList.remove('pulse');
                }, 1000);
            }, 300);
        } else {
            verifyButton.classList.remove('ready');
        }
    }
    
    // Обработка формы верификации
    document.getElementById('verification-form').addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Собираем код из всех полей
        let code = '';
        codeInputs.forEach(input => {
            code += input.value;
        });
        
        // Проверяем длину кода
        if (code.length !== 6) {
            showError('Пожалуйста, введите 6-значный код');
            return;
        }
        
        // Показываем анимацию загрузки
        const originalText = verifyButton.textContent;
        verifyButton.disabled = true;
        verifyButton.textContent = 'Проверка...';
        
        // Отправляем запрос на верификацию
        fetch('/api/verify-with-code', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                code: code
            }),
            credentials: 'include'
        })
        .then(response => {
            if (!response.ok) {
                console.error('Ошибка HTTP: ' + response.status);
                throw new Error('Ошибка сервера: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // Показываем сообщение об успехе
                verifyButton.textContent = 'Успешно!';
                verifyButton.classList.add('success');
                showSuccess();
                
                // Перенаправляем на страницу приложения через полную загрузку страницы
                setTimeout(() => {
                    window.location.href = '/app/dashboard';
                }, 2000);
            } else {
                // Восстанавливаем кнопку и показываем сообщение об ошибке
                verifyButton.disabled = false;
                verifyButton.textContent = originalText;
                showError(data.message || 'Ошибка верификации');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            verifyButton.disabled = false;
            verifyButton.textContent = originalText;
            showError('Ошибка при отправке запроса: ' + error.message);
        });
    });
    
    // Обработка повторной отправки кода
    resendLink.addEventListener('click', function(e) {
        e.preventDefault();
        
        // Получаем email 
        const email = localStorage.getItem('registration_email');
        
        if (!email) {
            showError('Не удалось определить email для отправки кода');
            return;
        }
        
        // Показываем состояние загрузки
        const originalText = resendLink.textContent;
        resendLink.textContent = 'Отправка...';
        resendLink.style.opacity = '0.7';
        
        // Отправляем запрос на повторную отправку кода
        fetch('/api/resend-verification', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify({
                username: username,
                email: email
            })
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Ошибка сервера: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            // Восстанавливаем состояние ссылки
            resendLink.textContent = originalText;
            resendLink.style.opacity = '1';
            
            if (data.success) {
                // Показываем сообщение об успешной отправке
                showSuccess('Код успешно отправлен на ваш email');
                
                // Скрываем сообщение через 3 секунды
                setTimeout(() => {
                    successAlert.style.opacity = '0';
                    setTimeout(() => {
                        successAlert.style.display = 'none';
                    }, 300);
                }, 3000);
            } else {
                // Показываем сообщение об ошибке
                showError(data.message || 'Ошибка при отправке кода');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            resendLink.textContent = originalText;
            resendLink.style.opacity = '1';
            showError('Ошибка при отправке запроса: ' + error.message);
        });
    });
}); 