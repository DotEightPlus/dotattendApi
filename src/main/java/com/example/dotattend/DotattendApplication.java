package com.example.dotattend;

import com.example.dotattend.LocalService.Repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
public class DotattendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DotattendApplication.class, args);
	}

}
