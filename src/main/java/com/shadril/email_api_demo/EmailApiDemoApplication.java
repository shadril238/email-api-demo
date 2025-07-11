package com.shadril.email_api_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
@EnableAspectJAutoProxy
public class EmailApiDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmailApiDemoApplication.class, args);
	}

}
