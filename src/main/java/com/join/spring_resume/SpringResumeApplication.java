package com.join.spring_resume;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SpringResumeApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringResumeApplication.class, args);
	}

}
