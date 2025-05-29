package com.example.fitness_tracker;

import com.example.fitness_tracker.model.Goal;
import com.example.fitness_tracker.model.User;
import com.example.fitness_tracker.service.GoalService;
import com.example.fitness_tracker.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@SpringBootApplication
public class FitnessTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitnessTrackerApplication.class, args);
	}
	
	@Bean
	public ErrorAttributes errorAttributes() {
		return new DefaultErrorAttributes() {
			@Override
			public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
				Map<String, Object> errorAttributes = super.getErrorAttributes(
					webRequest, 
					ErrorAttributeOptions.defaults()
						.including(ErrorAttributeOptions.Include.MESSAGE)
						.including(ErrorAttributeOptions.Include.BINDING_ERRORS)
				);
				return errorAttributes;
			}
		};
	}
	
	@Bean
	public CommandLineRunner initData(UserService userService, GoalService goalService) {
		return args -> {
			// Проверяем, есть ли уже цели в базе
			if (goalService.getAllGoals().isEmpty()) {
				// Получаем тестового пользователя (предполагается, что он уже существует)
				Optional<User> testUser = userService.findByUsername("user@example.com");
				
				if (testUser.isPresent()) {
					User user = testUser.get();
					
					// Создаем тестовые цели
					Goal goal1 = Goal.builder()
							.name("Сбросить 5 кг")
							.type("WEIGHT_LOSS")
							.description("Сбросить 5 кг к лету")
							.targetValue(5)
							.currentValue(2)
							.unit("кг")
							.targetDate(LocalDate.now().plusMonths(3))
							.completed(false)
							.createdAt(LocalDateTime.now())
							.user(user)
							.build();
					goalService.createGoal(goal1);
					
					Goal goal2 = Goal.builder()
							.name("Пробежать полумарафон")
							.type("ENDURANCE")
							.description("Подготовиться и пробежать полумарафон 21 км")
							.targetValue(21)
							.currentValue(15)
							.unit("км")
							.targetDate(LocalDate.now().plusMonths(6))
							.completed(false)
							.createdAt(LocalDateTime.now())
							.user(user)
							.build();
					goalService.createGoal(goal2);
					
					Goal goal3 = Goal.builder()
							.name("Подтягиваться 15 раз")
							.type("STRENGTH")
							.description("Увеличить количество подтягиваний до 15 раз")
							.targetValue(15)
							.currentValue(10)
							.unit("раз")
							.targetDate(LocalDate.now().plusMonths(2))
							.completed(false)
							.createdAt(LocalDateTime.now())
							.user(user)
							.build();
					goalService.createGoal(goal3);
					
					Goal goal4 = Goal.builder()
							.name("Выпивать 2 литра воды ежедневно")
							.type("WATER_INTAKE")
							.description("Пить больше воды для лучшего самочувствия")
							.targetValue(2)
							.currentValue(2)
							.unit("л")
							.targetDate(LocalDate.now().plusDays(30))
							.completed(true)
							.completedAt(LocalDateTime.now())
							.createdAt(LocalDateTime.now().minusDays(15))
							.user(user)
							.build();
					goalService.createGoal(goal4);
				}
			}
		};
	}
}
