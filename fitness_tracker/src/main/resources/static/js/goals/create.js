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
        targetDateField.min = todayStr;
    }
    
    // Добавляем класс .has-error к form-group если есть ошибки
    const errorMessages = document.querySelectorAll('.form-error');
    errorMessages.forEach(error => {
        if (error.textContent.trim() !== '') {
            error.closest('.form-group').classList.add('has-error');
            
            // Добавляем анимацию встряхивания для полей с ошибками
            const formGroup = error.closest('.form-group');
            formGroup.style.animation = 'shake 0.5s ease-in-out';
            setTimeout(() => {
                formGroup.style.animation = '';
            }, 500);
        }
    });
    
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
    
    // Создаем интерактивные типы целей
    const goalTypeBadges = document.querySelectorAll('.goal-type-badge');
    const goalTypeInput = document.getElementById('type');
    
    if (goalTypeBadges.length && goalTypeInput) {
        // Устанавливаем начальный выбор
        const currentType = goalTypeInput.value;
        goalTypeBadges.forEach(badge => {
            if (badge.getAttribute('data-type') === currentType) {
                badge.classList.add('selected');
            }
            
            // Анимация при наведении
            badge.addEventListener('mouseover', function() {
                if (!this.classList.contains('selected')) {
                    this.style.transform = 'translateY(-3px)';
                    this.style.boxShadow = '0 4px 8px rgba(0,0,0,0.1)';
                }
            });
            
            badge.addEventListener('mouseout', function() {
                if (!this.classList.contains('selected')) {
                    this.style.transform = '';
                    this.style.boxShadow = '';
                }
            });
            
            // Обработка клика
            badge.addEventListener('click', function() {
                const type = this.getAttribute('data-type');
                goalTypeInput.value = type;
                
                // Убираем выделение со всех бейджей
                goalTypeBadges.forEach(b => {
                    b.classList.remove('selected');
                    b.style.transform = '';
                    b.style.boxShadow = '';
                });
                
                // Выделяем кликнутый бейдж
                this.classList.add('selected');
                
                // Эффект пульсации при выборе
                this.style.animation = 'pulse 0.5s ease-out';
                setTimeout(() => {
                    this.style.animation = '';
                }, 500);
            });
        });
    }
    
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
    
    // Анимация для ссылки "Назад"
    const backLink = document.querySelector('.back-link');
    if (backLink) {
        backLink.style.opacity = '0';
        backLink.style.transform = 'translateX(-10px)';
        setTimeout(() => {
            backLink.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            backLink.style.opacity = '1';
            backLink.style.transform = 'translateX(0)';
        }, 300);
    }
}); 