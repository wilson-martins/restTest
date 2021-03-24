package com.bench.resttest.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Slf4j
@SpringBootApplication
@ComponentScan({
        "com.bench.resttest.app",
        "com.bench.resttest.dto",
        "com.bench.resttest.provider",
        "com.bench.resttest.service",
})
@EnableAsync
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class, args);
        log.info("Application started");

        ctx.getBean("")

    }
}