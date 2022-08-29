package com.example.zhaicount;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.zhaicount.dao")
public class ZhaicountApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZhaicountApplication.class, args);
    }

}
