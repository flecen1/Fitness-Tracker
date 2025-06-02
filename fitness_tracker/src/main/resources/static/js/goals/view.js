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
    
    // Добавление подтверждения перед удалением цели
    const deleteForm = document.querySelector('form[action*="/goals/"][action*="/delete"]');
    if (deleteForm) {
        deleteForm.addEventListener('submit', function(e) {
            if (!confirm('Вы уверены, что хотите удалить эту цель?')) {
                e.preventDefault();
            }
        });
    }
    
    // Автоматическое закрытие алертов через 5 секунд
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s';
            setTimeout(() => {
                alert.style.display = 'none';
            }, 500);
        }, 5000);
    });
    
    // Анимация прогресс-бара
    const progressBar = document.querySelector('.progress-bar');
    if (progressBar) {
        const width = progressBar.style.width || progressBar.getAttribute('data-width') || '0%';
        progressBar.style.width = '0';
        setTimeout(() => {
            progressBar.style.width = width;
        }, 300);
    }
    
    // Добавление анимации для статистических блоков
    const goalStats = document.querySelectorAll('.goal-stat');
    goalStats.forEach((stat, index) => {
        stat.style.opacity = '0';
        stat.style.transform = 'translateY(20px)';
        setTimeout(() => {
            stat.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            stat.style.opacity = '1';
            stat.style.transform = 'translateY(0)';
        }, 300 + (index * 100)); // Создаем эффект последовательного появления
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
    
    // Добавляем анимацию для блоков с информацией
    const goalInfo = document.querySelector('.goal-info');
    if (goalInfo) {
        setTimeout(() => {
            goalInfo.style.opacity = '1';
            goalInfo.style.transform = 'translateX(0)';
        }, 200);
    }
}); 