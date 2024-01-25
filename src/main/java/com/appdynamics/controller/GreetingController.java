package com.appdynamics.controller;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
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

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name)  {

        logger.info("Calling the Remote Service ");
        String results = "";
        try {
            URL url = new URL("https://www.hughbrien.com/");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            results = String.valueOf(responseCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        runClient();
        long testValue = createDelay() * 100;
        logger.info("Setting Cart Value");
        setCartValue(testValue);
        return new Greeting(counter.incrementAndGet(), String.format(template, name), results, testValue);
    }

    @GetMapping("/application")
    public String application(@RequestParam(value = "name", defaultValue = "World") String name) {
        long counter_value = counter.incrementAndGet();
        String myTemplate = "Hello, %s!";

        return String.format(template, counter);
    }

    public  void runClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("https://www.hughbrien.com"))
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