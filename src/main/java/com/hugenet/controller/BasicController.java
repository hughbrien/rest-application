package com.hugenet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/basic")
public class BasicController {

    private static final Logger logger = LoggerFactory.getLogger(BasicController.class);

    // Intentional error: Missing @GetMapping annotation
    public String missingMapping() {
        logger.error("This endpoint is missing a mapping annotation.");
        return "This won't be reachable!";
    }

    // Intentional error: Null pointer exception
    @GetMapping("/null-error")
    public String triggerNullPointer() {
        String value = null;
        logger.info("About to trigger a null pointer exception.");
        return value.toString(); // This will throw NullPointerException
    }

    // Intentional error: Arithmetic exception
    @GetMapping("/divide-by-zero")
    public String divideByZero() {
        int result = 10 / 0; // This will throw ArithmeticException
        logger.warn("Division by zero attempted.");
        return "Result: " + result;
    }

    // Intentional error: Bad path variable usage
    @GetMapping("/bad-path/{id}")
    public String badPath(@RequestParam("id") String id) { // Should be @PathVariable
        logger.debug("Incorrect annotation used for path variable.");
        return "Received ID: " + id;
    }

    // Intentional error: Unhandled exception
    @GetMapping("/unhandled")
    public String unhandledException() {
        logger.error("Throwing an unhandled exception.");
        throw new RuntimeException("This is an unhandled exception!");
    }
}
