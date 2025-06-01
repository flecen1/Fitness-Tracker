document.addEventListener('DOMContentLoaded', function() {
    // Анимация для появления элементов при загрузке страницы
    const welcomeBox = document.querySelector('.welcome-box');
    const features = document.querySelectorAll('.feature');
    
    if (welcomeBox) {
        welcomeBox.style.opacity = '0';
        welcomeBox.style.transform = 'translateY(20px)';
        
        setTimeout(() => {
            welcomeBox.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
            welcomeBox.style.opacity = '1';
            welcomeBox.style.transform = 'translateY(0)';
        }, 100);
    }
    
    if (features.length) {
        features.forEach((feature, index) => {
            feature.style.opacity = '0';
            feature.style.transform = 'translateY(20px)';
            
            setTimeout(() => {
                feature.style.transition = 'opacity 0.8s ease, transform 0.8s ease';
                feature.style.opacity = '1';
                feature.style.transform = 'translateY(0)';
            }, 300 + (index * 150));
        });
    }
}); 