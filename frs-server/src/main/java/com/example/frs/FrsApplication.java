package com.example.frs;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.frs.mapper")
public class FrsApplication {
    public static void main(String[] args) {
        SpringApplication.run(FrsApplication.class, args);
    }
}