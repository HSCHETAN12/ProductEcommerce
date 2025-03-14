package com.Virima.ProductEcommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProductEcommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductEcommerceApplication.class, args);
	}

}
