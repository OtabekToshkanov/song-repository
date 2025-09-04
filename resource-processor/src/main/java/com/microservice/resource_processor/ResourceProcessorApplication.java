package com.microservice.resource_processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ResourceProcessorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResourceProcessorApplication.class, args);
	}

}
