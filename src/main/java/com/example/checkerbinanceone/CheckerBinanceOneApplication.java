package com.example.checkerbinanceone;

import com.example.checkerbinanceone.service.SocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CheckerBinanceOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckerBinanceOneApplication.class, args);
    }
}
