package com.example.ss_les2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SsLes2Application {

	public static void main(String[] args) {
		SpringApplication.run(SsLes2Application.class, args);
	}

}

/*
добавимо таблиці

	authorities
			# id, name
			'1', 'read'



	users_authorities
				insert into users_authorities values (1, 1);
				
		# user_id, authority_id
			1			1

 */