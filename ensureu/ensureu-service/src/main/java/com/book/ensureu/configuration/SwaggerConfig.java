/*
package com.book.ensureu.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {//extends WebMvcConfigurationSupport {

	*/
/**
	 * @return regex for specific path shown as api documentation.
	 *//*

	@Bean
	public Docket getApi() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.book.ensureu")).paths(PathSelectors.any()).build().apiInfo(getApiMetaData());
	}

	private ApiInfo getApiMetaData() {
		return new ApiInfoBuilder().title("Ensureu API")
				.description("Ensureu API for Exams paper,Practice Paper and AI").version("1.0")
				.license("Ensureu License 1.0").licenseUrl("https://www.ensureu.com/licenses")
				.contact(new Contact("Dharmendra Singh", "https://www.ensureu.com", "dharmendra.singh@ensureu.com"))
				.build();

	}
}
*/
