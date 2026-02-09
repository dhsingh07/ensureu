package com.ensureu.commons;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

//@SpringBootApplication
public class NotificationApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}

	@Autowired
	ApplicationContext applicationContext;
	
	@Override
	public void run(String... args) throws Exception {
		Map<String,Object> beans = applicationContext.getBeansWithAnnotation(RestController.class);
		beans.forEach((k,v)->{
			System.out.println("key-->"+k);
			System.out.println("value obje-->"+v);
		});
	}
}
