package com.hugenet.controller;

import java.net.*;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


@RestController
public class GreetingController {
    Logger logger = LoggerFactory.getLogger(GreetingController.class);

    long cartValue = 0;
    private static final String template = "Hello, %s!";
    //private static final String json_template = '{"hello":"World", "name":"value"}'
    private final AtomicLong counter = new AtomicLong();
    
    // Tracking for application method slow downs
    private int currentHour = -1;
    private int slowDownCount = 0;
    private static final int MAX_SLOW_DOWNS_PER_HOUR = 10;

    @GetMapping("/machine")
    public String machine(@RequestParam(value = "name", defaultValue = "World") String name) {
        logger.info("Calling the /machine ");
        String hostname = "localhost";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return hostname;

    }

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "machine") String name)  {
        logger.info("Calling the /machine ");

        // Check if we're in the slow period (first 5 minutes of each hour)
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        int minutes = now.getMinute();
        if (minutes < 5) {
            logger.info("Entering slow period - first 5 minutes of the hour");
            try {
                // Add extra 2-5 second delay during slow period
                int slowDelay = (int) (Math.random() * 3000) + 2000; // 2-5 seconds
                Thread.sleep(slowDelay);
                logger.info("Applied slow period delay of {} ms", slowDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("Slow period delay interrupted", e);
            }
        }

        logger.info("Calling the Remote Service ");
        String results = "";
        results = executeRemoteService("https://www.hughbrien.com");
        results = executeRemoteService("https://www.hughbrien.com/index.html");
        results = executeRemoteService("https://www.steelstratus.com/en");
        // results = executeRemoteService("http://10.0.0.43:8083/welcome");
        // results = executeRemoteService("http://10.0.0.43:8083/applicationxd");
        // results = executeRemoteService("http://10.0.0.43:8083/applicationxd");

        logger.info("Run Request to SteelStatus and other web application");

        long testValue = createDelay() * 100;
        logger.info("Setting Cart Value");

        setCartValue(testValue);
        name = name + " Called Other Services";
        return new Greeting(counter.incrementAndGet(), String.format(template, name), results, testValue);
    }

    private static String executeRemoteService(String url_string) {
        String results;
        try {
            URL url = new URL(url_string);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            results = String.valueOf(responseCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    @GetMapping("/application")
    public String application(@RequestParam(value = "name", defaultValue = "World") String name) {
        logger.info("Calling the /application ");
        
        // Check current hour and reset counter if needed
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        int hour = now.getHour();
        
        if (currentHour != hour) {
            currentHour = hour;
            slowDownCount = 0;
            logger.info("New hour detected: {}:00. Reset slow down counter.", hour);
        }
        
        // Apply random slow down if we haven't reached the limit for this hour
        if (slowDownCount < MAX_SLOW_DOWNS_PER_HOUR) {
            // Random chance to trigger slow down (about 20% chance per request)
            double randomChance = Math.random();
            if (randomChance < 0.2) {
                slowDownCount++;
                logger.info("Triggering slow down #{} for hour {}", slowDownCount, hour);
                
                try {
                    // Random delay between 10-15 seconds (10000-15000 ms)
                    int slowDelay = (int) (Math.random() * 5000) + 10000;
                    Thread.sleep(slowDelay);
                    logger.info("Applied random slow delay of {} ms", slowDelay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Random slow delay interrupted", e);
                }
            }
        }
        
        long counter_value = counter.incrementAndGet();
        String results;
        results = executeRemoteService("https://www.hughbrien.com");
        results = executeRemoteService("https://www.hughbrien.com/index.html");
        results = executeRemoteService("https://www.steelstratus.com/en");
        
        return String.format(template, counter_value);
    }


    @GetMapping("/check_message")
    public String check_message(@RequestParam(value = "name", defaultValue = "World") String name) {
        logger.info("Calling the /check_message ");
        long counter_value = counter.incrementAndGet();
        String myTemplate = "Hello, %s!";
        logger.info("Throwing an exception");
        try {
            throw new Exception("Demo");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public  void runClient(String service_url) {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(service_url))
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            logger.info("Status code: " + response.statusCode());
            logger.info("Headers: " + response.headers().allValues("content-type"));
            //logger.info("Body: " + response.body().substring(0,10));
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
            logger.error((e.getStackTrace().toString()));

        }
    }

    public int createDelay(){
        return createDelay(5000, 1000);
    }

    public int createDelay(long end, long start){
        int interval  = (int) (Math.random() * (end - start)) + 1000;
        try {
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return  interval;
    }

    public long getCartValue() {
        return cartValue;
    }

    public void setCartValue(long cartValue) {
        this.cartValue = cartValue;
    }
}
