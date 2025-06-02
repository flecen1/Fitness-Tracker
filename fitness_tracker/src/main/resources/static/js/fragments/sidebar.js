document.addEventListener('DOMContentLoaded', function() {
    // Основные элементы
    const sidebar = document.querySelector('.sidebar');
    const toggleButton = document.querySelector('.sidebar-toggle');
    const miniLogo = document.querySelector('.mini-logo');
    const navLinks = document.querySelectorAll('.sidebar-nav a');
    const app = document.querySelector('body');
    
    // Сразу применяем состояние из хранилища при загрузке DOM
    function applySidebarState() {
        const isSidebarCollapsed = localStorage.getItem('sidebarCollapsed') === 'true';
        
        // Устанавливаем состояние без анимаций
        if (sidebar) {
            if (isSidebarCollapsed) {
                sidebar.classList.add('collapsed');
                if (app) app.classList.add('sidebar-collapsed');
            } else {
                sidebar.classList.remove('collapsed');
                if (app) app.classList.remove('sidebar-collapsed');
            }
        }
    }
    
    // Применяем состояние сразу при загрузке
    applySidebarState();
    
    // Функция для переключения состояния сайдбара
    function toggleSidebar() {
        if (!sidebar) return;
        
        sidebar.classList.toggle('collapsed');
        app.classList.toggle('sidebar-collapsed');
        localStorage.setItem('sidebarCollapsed', sidebar.classList.contains('collapsed'));
    }
    
    // Обработчик нажатия на кнопку скрытия/показа меню
    if (toggleButton) {
        toggleButton.addEventListener('click', function(event) {
            toggleSidebar();
            event.stopPropagation();
        });
    }
    
    // Обработчик клика по мини-логотипу
    if (miniLogo) {
        miniLogo.addEventListener('click', function(event) {
            if (sidebar && sidebar.classList.contains('collapsed')) {
                toggleSidebar();
            }
            event.preventDefault();
            event.stopPropagation();
        });
    }
    
    // Отключаем разворачивание при клике на пустую область сайдбара
    // (разворачивание происходит только при клике на мини-логотип)
    
    // Запрещаем любому клику по ссылкам разворачивать сайдбар
    navLinks.forEach(link => {
        link.addEventListener('click', function(event) {
            // Если сайдбар свёрнут, просто переходим по ссылке
            if (sidebar.classList.contains('collapsed')) {
                event.stopPropagation();
            }
        });
    });
    
    // Создаем декоративные элементы
    createDecorations();
    
    // Добавляем подсветку к активному пункту меню
    highlightActiveItem();
    
    // На мобильных устройствах добавляем кнопку для открытия меню
    setupMobileMenu();
    
    // Функция для создания декоративных элементов
    function createDecorations() {
        // Создаем фоновые декоративные элементы
        for (let i = 1; i <= 3; i++) {
            const decoration = document.createElement('div');
            decoration.className = `sidebar-decoration decoration-${i}`;
            
            // Добавляем случайные стили для декораций
            const size = Math.random() * 50 + 30; // От 30px до 80px
            const posX = Math.random() * 80 + 10; // От 10% до 90% по горизонтали
            const posY = Math.random() * 80 + 10; // От 10% до 90% по вертикали
            const opacity = Math.random() * 0.07 + 0.02; // Низкая прозрачность
            
            Object.assign(decoration.style, {
                position: 'absolute',
                width: size + 'px',
                height: size + 'px',
                borderRadius: '50%',
                backgroundColor: 'var(--primary)',
                opacity: opacity,
                top: posY + '%',
                left: posX + '%',
                filter: 'blur(15px)',
                zIndex: '-1'
            });
            
            // Добавляем в сайдбар
            if (sidebar) {
                sidebar.appendChild(decoration);
            }
        }
        
        // Добавляем линию подчеркивания для активного элемента
        const activeLine = document.createElement('div');
        activeLine.className = 'active-nav-line';
        
        Object.assign(activeLine.style, {
            position: 'absolute',
            width: '0',
            height: '3px',
            backgroundColor: 'var(--primary)',
            transition: 'all 0.3s ease',
            bottom: '-1px',
            left: '0',
            opacity: '0'
        });
        
        if (sidebar) {
            sidebar.appendChild(activeLine);
        }
    }
    
    // Функция для подсветки активного пункта меню
    function highlightActiveItem() {
        // Находим активный элемент по URL
        const currentPath = window.location.pathname;
        
        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            const linkPath = href.split('?')[0]; // Убираем параметры из URL
            
            // Если путь содержится в текущем URL
            if (currentPath.includes(linkPath) && linkPath !== '/') {
                link.classList.add('active');
                
                // Добавляем эффект при наведении для активного элемента
                link.addEventListener('mouseenter', function() {
                    this.style.transform = 'translateY(-2px)';
                    this.style.boxShadow = '0 4px 8px rgba(46, 139, 87, 0.15)';
                });
                
                link.addEventListener('mouseleave', function() {
                    this.style.transform = 'translateY(0)';
                    this.style.boxShadow = 'none';
                });
            }
        });
    }
    
    // Функция для настройки мобильной версии меню
    function setupMobileMenu() {
        // Проверяем ширину экрана
        const isMobile = window.innerWidth <= 768;
        
        if (isMobile) {
            // Создаем кнопку открытия меню для мобильных устройств
            const mobileToggle = document.createElement('button');
            mobileToggle.className = 'mobile-sidebar-toggle';
            mobileToggle.innerHTML = '<i class="fas fa-bars"></i>';
            
            Object.assign(mobileToggle.style, {
                position: 'fixed',
                top: '15px',
                left: '15px',
                zIndex: '1001',
                background: 'var(--primary)',
                color: 'white',
                border: 'none',
                borderRadius: '50%',
                width: '45px',
                height: '45px',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                fontSize: '1.2rem',
                boxShadow: '0 2px 10px rgba(46, 139, 87, 0.3)',
                cursor: 'pointer'
            });
            
            // Добавляем кнопку на страницу
            document.body.appendChild(mobileToggle);
            
            // Добавляем обработчик нажатия
            mobileToggle.addEventListener('click', function() {
                sidebar.classList.toggle('mobile-open');
            });
            
            // Закрываем меню при клике на ссылку в мобильной версии
            navLinks.forEach(link => {
                link.addEventListener('click', function() {
                    if (window.innerWidth <= 768) {
                        sidebar.classList.remove('mobile-open');
                    }
                });
            });
            
            // Закрываем меню при клике вне его в мобильной версии
            document.addEventListener('click', function(event) {
                if (!sidebar.contains(event.target) && 
                    !mobileToggle.contains(event.target) && 
                    window.innerWidth <= 768 && 
                    sidebar.classList.contains('mobile-open')) {
                    sidebar.classList.remove('mobile-open');
                }
            });
        }
    }
    
    // Добавляем немного анимации при загрузке
    window.addEventListener('load', function() {
        // Добавляем последовательное появление для пунктов меню
        navLinks.forEach((link, index) => {
            link.style.opacity = '0';
            link.style.transform = 'translateX(-20px)';
            
            setTimeout(() => {
                link.style.opacity = '1';
                link.style.transform = 'translateX(0)';
                link.style.transition = 'all 0.3s ease';
            }, 100 + (index * 60));
        });
    });
    
    // Обновляем состояние сайдбара при изменении размера окна
    window.addEventListener('resize', function() {
        setupMobileMenu();
    });
}); 