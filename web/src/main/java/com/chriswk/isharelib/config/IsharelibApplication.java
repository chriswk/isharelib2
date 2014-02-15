package com.chriswk.isharelib.config;

import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;


@EnableAutoConfiguration
@ComponentScan
public class IsharelibApplication {
    public static void main(String[] args) {
        SpringApplication.run(IsharelibApplication.class, args);
    }
}
