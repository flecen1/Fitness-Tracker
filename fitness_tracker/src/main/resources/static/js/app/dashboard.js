document.addEventListener('DOMContentLoaded', function() {
    // Инициализация карточек
    initCards();
    
    // Добавление интерактивности к уведомлениям
    setupNotifications();
    
    // Настройка дополнительной функциональности
    setupExtraFunctionality();
});

/**
 * Инициализация карточек панели управления
 */
function initCards() {
    const cards = document.querySelectorAll('.card');
    
    cards.forEach(card => {
        // Добавляем эффект при наведении
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
            this.style.boxShadow = '0 8px 16px rgba(0, 0, 0, 0.1)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = '';
            this.style.boxShadow = '';
        });
    });
}

/**
 * Настройка уведомлений
 */
function setupNotifications() {
    // Здесь может быть код для обработки уведомлений
    const notifications = document.querySelectorAll('.notification');
    
    notifications.forEach(notification => {
        const closeBtn = notification.querySelector('.close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', function() {
                notification.classList.add('fade-out');
                setTimeout(() => {
                    notification.style.display = 'none';
                }, 300);
            });
        }
    });
}

/**
 * Настройка дополнительной функциональности
 */
function setupExtraFunctionality() {
    // Автоматическое обновление данных в реальном времени
    setInterval(function() {
        // Код для обновления данных (например, через AJAX)
        // Это могло бы обновлять статистику в реальном времени
        
        // Для демонстрационных целей просто обновляем время последней проверки
        const lastUpdateElement = document.querySelector('.last-update');
        if (lastUpdateElement) {
            const now = new Date();
            lastUpdateElement.textContent = `Последнее обновление: ${now.getHours()}:${now.getMinutes().toString().padStart(2, '0')}`;
        }
    }, 60000); // Обновление каждую минуту
} 