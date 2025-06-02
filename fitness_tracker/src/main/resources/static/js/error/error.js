document.addEventListener('DOMContentLoaded', function() {
    // Основные элементы
    const errorText = document.querySelector('.error');
    const button = document.querySelector('.btn-home');
    const errorMessage = document.querySelector('.error-message');
    const errorContainer = document.querySelector('.error-container');
    const app = document.querySelector('.app');
    const img = document.querySelector('.img img');
    const premiumContainer = document.querySelector('.premium-error-container');
    
    // Добавляем класс dots к спану с точками
    const trainText = document.querySelector('.okak');
    if (trainText) {
        // Исправляем формат "Продолжайте тренировки" и точек в одну строку
        const textContent = trainText.textContent.trim();
        if (textContent.includes('Продолжайте тренировки')) {
            trainText.innerHTML = 'Продолжайте тренировки<span class="dots"></span>';
        }
    }
    
    // Добавляем атрибут data-text к элементу ошибки для эффекта свечения
    if (errorText) {
        errorText.setAttribute('data-text', errorText.textContent);
    }
    
    // Удаляем класс animated с кнопки, чтобы избежать пульсации
    if (button) {
        button.classList.remove('animated');
    }
    
    // Создаем декоративные элементы
    createDecorations();
    
    // Добавляем неоновую линию
    addNeonLine();
    
    // Добавляем цифровые линии
    addCircuitLines();
    
    // Создаем эффект постепенного появления для страницы
    document.body.style.opacity = '0';
    setTimeout(() => {
        document.body.style.opacity = '1';
        document.body.style.transition = 'opacity 0.5s ease';
    }, 100);
    
    function addNeonLine() {
        // Добавляем неоновую линию
        const neonLine = document.createElement('div');
        neonLine.className = 'neon-line';
        app.appendChild(neonLine);
    }
    
    function addCircuitLines() {
        // Добавляем линии как в электронных схемах
        for (let i = 1; i <= 3; i++) {
            const line = document.createElement('div');
            line.className = `circuit-line circuit-line-${i}`;
            app.appendChild(line);
        }
    }
    
    // Функция для создания декоративных элементов
    function createDecorations() {
        // Создаем фоновые декоративные элементы
        for (let i = 1; i <= 6; i++) {
            const decoration = document.createElement('div');
            decoration.className = `decoration decoration-${i}`;
            app.appendChild(decoration);
        }
        
        // Создаем фоновые волны
        const wave = document.createElement('div');
        wave.className = 'wave';
        app.appendChild(wave);
        
        const wave2 = document.createElement('div');
        wave2.className = 'wave-2';
        app.appendChild(wave2);
        
        // Добавляем кибер-сетку
        const cyberGrid = document.createElement('div');
        cyberGrid.className = 'cyber-grid';
        app.appendChild(cyberGrid);
    }
    
    // Функция для создания минимального количества фоновых частиц
    function createParticles() {
        // Создаем только 6 плавающих частиц
        for (let i = 0; i < 6; i++) {
            const particle = document.createElement('div');
            particle.className = 'particle';
            
            // Случайный размер
            const size = Math.random() * 8 + 2;
            // Случайная позиция
            const posX = Math.random() * 100;
            const posY = Math.random() * 100;
            // Случайное время анимации
            const duration = Math.random() * 10 + 5;
            // Случайная прозрачность (меньше заметная)
            const opacity = Math.random() * 0.1 + 0.05;
            // Зеленые оттенки
            const hue = 120 + Math.random() * 20;
            
            // Применяем стили
            Object.assign(particle.style, {
                position: 'absolute',
                width: size + 'px',
                height: size + 'px',
                background: `hsla(${hue}, 70%, 50%, ${opacity})`,
                borderRadius: '50%',
                top: posY + '%',
                left: posX + '%',
                opacity: opacity,
                zIndex: '-1',
                animation: `float ${duration}s infinite ease-in-out ${Math.random() * 3}s`,
                boxShadow: `0 0 ${Math.round(size/2)}px hsla(${hue}, 70%, 50%, 0.2)`
            });
            
            app.appendChild(particle);
        }
    }
    
    // Создаем плавающие частицы
    createParticles();
    
    // Флаг для отслеживания анимации
    let isAnimating = false;
    
    // Улучшенная анимация для кота при клике
    if (img) {
        img.addEventListener('click', function() {
            if (isAnimating) return; // Предотвращаем повторное срабатывание во время анимации
            isAnimating = true;
            
            // Создаем контейнер для текста
            const okakContainer = document.createElement('div');
            okakContainer.className = 'okak-message';
            okakContainer.textContent = 'окак'; // Только один раз "окак"
            
            // Добавляем стили для контейнера
            Object.assign(okakContainer.style, {
                position: 'absolute',
                top: '-70px', 
                left: '50%',
                transform: 'translateX(-50%)',
                color: 'var(--primary-dark)',
                fontWeight: 'bold',
                fontSize: '28px',
                padding: '15px 30px',
                borderRadius: '25px',
                backgroundColor: 'rgba(255,255,255,0.97)',
                boxShadow: '0 8px 20px rgba(76, 175, 80, 0.4)',
                zIndex: '10',
                opacity: '0',
                pointerEvents: 'none'
            });
            
            // Добавляем элемент в контейнер изображения
            document.querySelector('.img').appendChild(okakContainer);
            
            // Создаем и добавляем стили для анимации
            const style = document.createElement('style');
            style.textContent = `
                @keyframes okakPopup {
                    0% { 
                        opacity: 0;
                        transform: translateX(-50%) translateY(10px);
                    }
                    20% { 
                        opacity: 1;
                        transform: translateX(-50%) translateY(0);
                    }
                    80% { 
                        opacity: 1;
                        transform: translateX(-50%) translateY(0);
                    }
                    100% { 
                        opacity: 0;
                        transform: translateX(-50%) translateY(-10px);
                    }
                }
                
                @keyframes catAnimation {
                    0% { transform: translateY(0) scale(1); }
                    15% { transform: translateY(3px) scale(0.98); }
                    30% { transform: translateY(-5px) scale(1.05); }
                    45% { transform: translateY(-2px) scale(1.02); }
                    60% { transform: translateY(0) scale(1); }
                    100% { transform: translateY(0) scale(1); }
                }
            `;
            document.head.appendChild(style);
            
            // Применяем анимацию к тексту
            okakContainer.style.animation = 'okakPopup 2s forwards ease-in-out';
            
            // Применяем плавную анимацию к коту
            this.style.animation = 'catAnimation 1.5s ease-in-out';
            
            // Добавляем временное изображение с эффектом свечения вокруг кота
            const glowEffect = document.createElement('div');
            glowEffect.style.cssText = `
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                border-radius: 50%;
                background: radial-gradient(circle, rgba(76, 175, 80, 0.3) 30%, transparent 70%);
                opacity: 0;
                z-index: 4;
                animation: fadeInOut 1.5s ease-in-out forwards;
            `;
            
            // Добавляем стиль для анимации свечения
            style.textContent += `
                @keyframes fadeInOut {
                    0% { opacity: 0; }
                    30% { opacity: 0.8; }
                    70% { opacity: 0.8; }
                    100% { opacity: 0; }
                }
            `;
            
            // Добавляем свечение к контейнеру изображения
            const imgContainer = document.querySelector('.img');
            imgContainer.appendChild(glowEffect);
            
            // Очистка
            setTimeout(() => {
                okakContainer.remove();
                glowEffect.remove();
                style.remove();
                isAnimating = false;
            }, 2000);
        });
    }
}); 