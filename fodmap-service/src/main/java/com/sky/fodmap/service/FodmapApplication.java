package com.sky.fodmap.service;

import io.prometheus.client.Counter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class FodmapApplication {
	public static void main(String[] args) {
		SpringApplication.run(FodmapApplication.class,args);
	}
}
