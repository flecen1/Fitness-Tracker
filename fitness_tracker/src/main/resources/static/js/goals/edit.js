document.addEventListener('DOMContentLoaded', function() {
    // Настраиваем выпадающее меню пользователя
    const userDropdownBtn = document.getElementById('userDropdownBtn');
    const userDropdown = document.getElementById('userDropdown');
    
    if (userDropdownBtn) {
        userDropdownBtn.addEventListener('click', function() {
            userDropdown.classList.toggle('show');
        });
        
        // Закрываем меню при клике вне его
        window.addEventListener('click', function(e) {
            if (!userDropdown.contains(e.target)) {
                userDropdown.classList.remove('show');
            }
        });
    }
    
    // Обработка выхода из системы
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Перенаправляем на стандартный URL выхода Spring Security
            window.location.href = '/logout';
        });
    }
    
    // Если цель отмечена как выполненная, показываем дату выполнения
    const completedCheckbox = document.getElementById('completed');
    if (completedCheckbox) {
        // Анимация для чекбокса
        completedCheckbox.addEventListener('change', function() {
            const label = completedCheckbox.nextElementSibling;
            if (this.checked) {
                label.style.transition = 'color 0.3s ease';
                label.style.color = '#388E3C';
                label.style.fontWeight = '500';
                
                // Добавляем пульсацию для подтверждения действия
                const pulseAnimation = document.createElement('style');
                pulseAnimation.textContent = `
                    @keyframes pulse {
                        0% { transform: scale(1); }
                        50% { transform: scale(1.05); }
                        100% { transform: scale(1); }
                    }
                `;
                document.head.appendChild(pulseAnimation);
                
                label.style.animation = 'pulse 0.6s ease-out';
                setTimeout(() => {
                    label.style.animation = '';
                }, 600);
                
                // Логика для отображения даты выполнения, если нужно
            } else {
                label.style.color = '';
                label.style.fontWeight = '';
                // Логика для скрытия даты выполнения, если нужно
            }
        });
    }
    
    // Устанавливаем минимальную дату для поля targetDate как сегодня
    const targetDateField = document.getElementById('targetDate');
    if (targetDateField) {
        const today = new Date();
        const yyyy = today.getFullYear();
        let mm = today.getMonth() + 1;
        let dd = today.getDate();
        
        if (mm < 10) mm = '0' + mm;
        if (dd < 10) dd = '0' + dd;
        
        const todayStr = yyyy + '-' + mm + '-' + dd;
        // Не устанавливаем минимальную дату при редактировании, чтобы не блокировать существующие цели
        // targetDateField.min = todayStr;
    }
    
    // Анимация для элементов формы
    const formGroups = document.querySelectorAll('.form-group');
    formGroups.forEach((group, index) => {
        group.style.opacity = '0';
        group.style.transform = 'translateX(-10px)';
        setTimeout(() => {
            group.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            group.style.opacity = '1';
            group.style.transform = 'translateX(0)';
        }, 100 + (index * 50)); // Создаем эффект последовательного появления
    });
    
    // Анимация для кнопок
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('mousedown', function() {
            this.style.transform = 'scale(0.95)';
        });
        
        button.addEventListener('mouseup', function() {
            this.style.transform = 'scale(1)';
        });
        
        button.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1)';
        });
    });
    
    // Стилизация полей при фокусировке
    const formControls = document.querySelectorAll('.form-control');
    formControls.forEach(control => {
        control.addEventListener('focus', function() {
            const formGroup = this.closest('.form-group');
            if (formGroup) {
                const label = formGroup.querySelector('.form-label');
                if (label) {
                    label.style.color = '#4CAF50';
                }
            }
        });
        
        control.addEventListener('blur', function() {
            const formGroup = this.closest('.form-group');
            if (formGroup) {
                const label = formGroup.querySelector('.form-label');
                if (label) {
                    label.style.color = '';
                }
            }
        });
    });
    
    // Создаем эффект пульсации для заголовка формы
    const formTitle = document.querySelector('.form-title');
    if (formTitle) {
        setTimeout(() => {
            formTitle.style.transition = 'transform 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275)';
            formTitle.style.transform = 'scale(1.03)';
            setTimeout(() => {
                formTitle.style.transform = 'scale(1)';
            }, 300);
        }, 400);
    }
}); 