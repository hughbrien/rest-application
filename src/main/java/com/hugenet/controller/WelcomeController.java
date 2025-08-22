package com.hugenet.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class WelcomeController {
    Logger logger = LoggerFactory.getLogger(WelcomeController.class);

    int sleepInterval =0;
    private static final String welcomeTemplate = "Welcome Message , %s!";
    //private static final String json_template = '{"hello":"World", "name":"value"}'
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/welcome")
    public Welcome welcome(@RequestParam(value = "name", defaultValue = "World") String name) {
        logger.info("Calling /welcome");
        return new Welcome(counter.incrementAndGet(), String.format(welcomeTemplate, name));
    }

    @GetMapping("/applicationx")
    public String applicationx(@RequestParam(value = "name", defaultValue = "World") String name) {
        logger.info("Calling /applicationx");
        int interval  = (int) (Math.random() * (5000 - 1000)) + 1000;
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String intervalValue  = String.valueOf(interval);
        counter.incrementAndGet();
        return String.format(welcomeTemplate, "The synthetic delay is " + intervalValue);
    }



    public int getSleepInterval() {
        return sleepInterval;
    }

    public void setSleepInterval(int sleepInterval) {
        this.sleepInterval = sleepInterval;
    }


}