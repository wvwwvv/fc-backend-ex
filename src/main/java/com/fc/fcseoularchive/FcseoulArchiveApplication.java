package com.fc.fcseoularchive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class FcseoulArchiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(FcseoulArchiveApplication.class, args);
    }

}
