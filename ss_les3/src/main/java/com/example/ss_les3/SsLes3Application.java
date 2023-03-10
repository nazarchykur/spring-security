package com.example.ss_les3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SsLes3Application {

	public static void main(String[] args) {
		SpringApplication.run(SsLes3Application.class, args);
	}

}

/*
 1) перше створюємо свій фільтр 
 		CustomAuthenticationFilter extends OncePerRequestFilter - відпрацює тільки один раз
 			це не підходить, бо CustomAuthenticationFilter extends Filter - відпрацьовуватиме кожного разу 
 			
 			
 2) далі працюємо з AuthenticationManager
    його роль = передати до провайдера, так як у нас може бути багато провайдерів,
    навіть якщо у нас є тільки один провайдер, всеодно не пропускати AuthenticationManager  
    
			@Component
			public class CustomAuthenticationManager implements AuthenticationManager	
			   
			   public Authentication authenticate(Authentication authentication)   <= перезаписати єдиний метод 
    
    CustomAuthenticationManager ми маємо додати до CustomAuthenticationFilter як депенденсі
    
    
 3) далі йдемо до провайдера   
 
		@Component
		public class CustomAuthenticationProvider  implements AuthenticationProvider 	
					
				@Override
				public Authentication authenticate(Authentication authentication)
				
		де у методі authenticate() перевіримо чи є ключ у хедері і чи він коректний
		
		
	4) далі, якщо все ОК, то назад по цій цепочці повертаємо уже аутентифікованого
	
	5) у SecurityContext зберігається уже аутентифікований
	
	6) і тільки потім передається на наступний фільтр, якщо він є
	
	... 		
	..) фільтр по Авторизації вже буде брати з SecurityContext аутентифікований акаунт (юзер ...)
 */