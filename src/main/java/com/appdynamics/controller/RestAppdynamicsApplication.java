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

	public static String REMOTE_TEST_HOST = "localhost";
	public static String REMOTE_DEFAULT_PORT = "8083";

	private static final Logger LOG = LoggerFactory.getLogger(RestAppdynamicsApplication.class);

	public static void main(String[] args)
	{
		// -D REMOTE_TEST_HOST=propertyValue

		REMOTE_TEST_HOST = System.getProperty("REMOTE_TEST_HOST", "localhost");
		LOG.info("# # # # # # # # REMOTE_TEST_HOST :: " + REMOTE_TEST_HOST + " # # # # # # # # " );


		REMOTE_DEFAULT_PORT = System.getProperty("REMOTE_DEFAULT_PORT", REMOTE_DEFAULT_PORT);
		LOG.info("# # # # # # # # REMOTE_DEFAULT_PORT :: " + REMOTE_DEFAULT_PORT + " # # # # # # # # " );


		LOG.info("# # # # # # # # Starting RestAppdynamicsApplication # # # # # # # # ");
		SpringApplication.run(RestAppdynamicsApplication.class, args);
	}

}
