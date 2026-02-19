package com.book.ensureu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * EnsureU - Assessment Platform for Competitive Exams
 * A product of GrayscaleLabs AI Pvt Ltd.
 *
 * Main Spring Boot Application entry point.
 *
 * @author GrayscaleLabs AI Pvt Ltd
 * @since 1.0.0
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.book.ensureu","com.ensureu.commons.gcloud"})
@EnableMongoRepositories(basePackages={"com.book.ensureu.repository"})
@EntityScan({"com.book.ensureu.model","com.book.ensureu.admin.model"})
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
