package com.gushu.gushumingmu;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
@EnableCaching
public class GushumingmuApplication {

    public static void main(String[] args) {
        SpringApplication.run(GushumingmuApplication.class, args);
    }

}
