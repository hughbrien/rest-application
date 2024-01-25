package com.appdynamics.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@SpringBootApplication
public class RestAppdynamicsApplication {

	private static final Logger LOG = LoggerFactory.getLogger(RestAppdynamicsApplication.class);

	public static void main(String[] args)
	{
		LOG.info("# # # # # # # # Starting RestAppdynamicsApplication # # # # # # # # ");
		SpringApplication.run(RestAppdynamicsApplication.class, args);
	}

}
