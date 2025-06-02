document.addEventListener('DOMContentLoaded', function() {
    // При клике на кнопку фильтрации, проверяем наличие значений в селекторах
    document.querySelector('.filter-controls form').addEventListener('submit', function(e) {
        const typeSelect = document.getElementById('type');
        const statusSelect = document.getElementById('completed');
        
        // Если оба селектора пустые, предотвращаем отправку формы
        if (typeSelect.value === '' && statusSelect.value === '') {
            e.preventDefault();
            window.location.href = '/app/goals';
        }
    });
    
    // Функция для создания кастомного селекта
    function createCustomSelect(select) {
        // Скрываем оригинальный селект
        select.style.display = 'none';
        
        // Создаем кастомный селект
        const customSelect = document.createElement('div');
        customSelect.className = 'custom-select';
        customSelect.style.position = 'relative';
        customSelect.style.display = 'inline-block';
        customSelect.style.minWidth = '150px';
        
        // Создаем элемент, показывающий выбранное значение
        const selectedOption = document.createElement('div');
        selectedOption.className = 'custom-select-selected';
        selectedOption.textContent = select.options[select.selectedIndex]?.textContent || 'Выберите значение';
        selectedOption.style.padding = '8px 12px';
        selectedOption.style.border = '1px solid #4CAF50';
        selectedOption.style.borderRadius = '6px';
        selectedOption.style.backgroundColor = 'white';
        selectedOption.style.cursor = 'pointer';
        selectedOption.style.position = 'relative';
        selectedOption.style.paddingRight = '30px';
        
        // Добавляем стрелку
        const arrow = document.createElement('div');
        arrow.innerHTML = '▼';
        arrow.style.position = 'absolute';
        arrow.style.right = '10px';
        arrow.style.top = '50%';
        arrow.style.transform = 'translateY(-50%)';
        arrow.style.color = '#4CAF50';
        arrow.style.fontSize = '0.8em';
        selectedOption.appendChild(arrow);
        
        // Создаем выпадающий список опций
        const optionsList = document.createElement('div');
        optionsList.className = 'custom-select-options';
        optionsList.style.display = 'none';
        optionsList.style.position = 'absolute';
        optionsList.style.top = '100%';
        optionsList.style.left = '0';
        optionsList.style.right = '0';
        optionsList.style.backgroundColor = 'white';
        optionsList.style.border = '1px solid #4CAF50';
        optionsList.style.borderRadius = '6px';
        optionsList.style.marginTop = '5px';
        optionsList.style.maxHeight = '200px';
        optionsList.style.overflowY = 'auto';
        optionsList.style.zIndex = '100';
        optionsList.style.boxShadow = '0 4px 8px rgba(0,0,0,0.1)';
        
        // Добавляем опции
        for (let i = 0; i < select.options.length; i++) {
            const option = document.createElement('div');
            option.className = 'custom-select-option';
            option.textContent = select.options[i].textContent;
            option.dataset.value = select.options[i].value;
            option.style.padding = '8px 12px';
            option.style.cursor = 'pointer';
            option.style.borderBottom = i < select.options.length - 1 ? '1px solid #eee' : 'none';
            
            // Выделяем выбранную опцию
            if (i === select.selectedIndex) {
                option.style.backgroundColor = '#4CAF50';
                option.style.color = 'white';
            }
            
            // Добавляем эффект при наведении
            option.addEventListener('mouseover', function() {
                if (i !== select.selectedIndex) {
                    option.style.backgroundColor = '#8BC34A';
                    option.style.color = 'white';
                }
            });
            
            option.addEventListener('mouseout', function() {
                if (i !== select.selectedIndex) {
                    option.style.backgroundColor = '';
                    option.style.color = '';
                }
            });
            
            // Обрабатываем клик по опции
            option.addEventListener('click', function() {
                select.selectedIndex = i;
                selectedOption.textContent = this.textContent;
                selectedOption.appendChild(arrow); // Возвращаем стрелку обратно
                
                // Обновляем стили опций
                const allOptions = optionsList.querySelectorAll('.custom-select-option');
                allOptions.forEach((opt, index) => {
                    if (index === i) {
                        opt.style.backgroundColor = '#4CAF50';
                        opt.style.color = 'white';
                    } else {
                        opt.style.backgroundColor = '';
                        opt.style.color = '';
                    }
                });
                
                // Скрываем список
                optionsList.style.display = 'none';
                
                // Вызываем событие change на оригинальном селекте
                const event = new Event('change');
                select.dispatchEvent(event);
            });
            
            optionsList.appendChild(option);
        }
        
        // Обрабатываем клик по выбранному элементу
        selectedOption.addEventListener('click', function(e) {
            e.stopPropagation();
            
            // Закрываем другие открытые селекты
            const allLists = document.querySelectorAll('.custom-select-options');
            allLists.forEach(list => {
                if (list !== optionsList) {
                    list.style.display = 'none';
                }
            });
            
            // Переключаем видимость списка
            optionsList.style.display = optionsList.style.display === 'none' ? 'block' : 'none';
            arrow.innerHTML = optionsList.style.display === 'none' ? '▼' : '▲';
        });
        
        // Клик вне селекта закрывает список
        document.addEventListener('click', function() {
            optionsList.style.display = 'none';
            arrow.innerHTML = '▼';
        });
        
        // Предотвращаем закрытие при клике на сам список
        optionsList.addEventListener('click', function(e) {
            e.stopPropagation();
        });
        
        // Собираем и добавляем всё в DOM
        customSelect.appendChild(selectedOption);
        customSelect.appendChild(optionsList);
        select.parentNode.insertBefore(customSelect, select.nextSibling);
        
        return customSelect;
    }
    
    // Создаем кастомные селекты (раскомментируйте, если хотите использовать)
    const selects = document.querySelectorAll('.filter-select');
    selects.forEach(select => {
        createCustomSelect(select);
    });
    
    // Применение зеленых стилей к селекторам
    selects.forEach(select => {
        select.style.borderColor = '#4CAF50';
        select.style.color = '#388E3C';
        
        // Добавляем обработчик события для изменения цвета выбранного элемента
        select.addEventListener('change', function() {
            const selectedOption = this.options[this.selectedIndex];
            if (selectedOption) {
                Array.from(this.options).forEach(opt => {
                    opt.style.backgroundColor = '';
                    opt.style.color = '';
                });
                selectedOption.style.backgroundColor = '#4CAF50';
                selectedOption.style.color = 'white';
            }
        });
        
        // Принудительно применяем стили к уже выбранному элементу
        const selectedOption = select.options[select.selectedIndex];
        if (selectedOption) {
            selectedOption.style.backgroundColor = '#4CAF50';
            selectedOption.style.color = 'white';
        }
        
        // Стилизация выпадающих опций с помощью CSS
        const style = document.createElement('style');
        style.textContent = `
            .filter-select option:checked {
                background-color: #4CAF50 !important;
                color: white !important;
                -webkit-appearance: none;
                box-shadow: 0 0 10px 100px #4CAF50 inset !important;
            }
            .filter-select option:hover {
                background-color: #8BC34A !important;
            }
            select:-internal-list-box option:checked {
                color: white !important;
                background-color: #4CAF50 !important;
            }
        `;
        document.head.appendChild(style);
    });
    
    // Переопределяем стили для выбранного элемента в списке ENDURANCE
    const options = document.querySelectorAll('.filter-select option');
    options.forEach(option => {
        if (option.selected) {
            option.style.backgroundColor = '#4CAF50';
            option.style.color = 'white';
        }
        // Добавляем обработчик события для изменения стиля при наведении
        option.addEventListener('mouseover', function() {
            this.style.backgroundColor = '#8BC34A';
            this.style.color = 'white';
        });
        option.addEventListener('mouseout', function() {
            if (!this.selected) {
                this.style.backgroundColor = '';
                this.style.color = '';
            }
        });
    });
    
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
    
    // Добавление подтверждения перед удалением цели
    const deleteForms = document.querySelectorAll('form[action*="/goals/"]');
    deleteForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            if (!confirm('Вы уверены, что хотите удалить эту цель?')) {
                e.preventDefault();
            }
        });
    });
    
    // Анимация карточек целей при загрузке страницы
    const goalCards = document.querySelectorAll('.goal-card');
    goalCards.forEach((card, index) => {
        card.style.opacity = '0';
        card.style.transform = 'translateY(20px)';
        setTimeout(() => {
            card.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
            card.style.opacity = '1';
            card.style.transform = 'translateY(0)';
        }, 100 + (index * 50)); // Создаем эффект появления карточек одна за другой
    });
    
    // Анимация прогресс-баров при загрузке страницы
    const progressBars = document.querySelectorAll('.progress-bar');
    progressBars.forEach((bar) => {
        const width = bar.style.width;
        bar.style.width = '0';
        setTimeout(() => {
            bar.style.width = width;
        }, 300);
    });
    
    // Добавляем обработчик клика для кнопки добавления цели
    const addGoalBtn = document.querySelector('.add-goal-btn');
    if (addGoalBtn) {
        addGoalBtn.addEventListener('mousedown', function() {
            this.style.transform = 'scale(0.95) translateY(-1px)';
        });
        addGoalBtn.addEventListener('mouseup', function() {
            this.style.transform = 'scale(1) translateY(-2px)';
        });
        addGoalBtn.addEventListener('mouseleave', function() {
            this.style.transform = 'scale(1) translateY(-2px)';
        });
    }
}); 