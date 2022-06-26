package com.example.checkerbinanceone;

import com.example.checkerbinanceone.service.SocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CheckerBinanceOneApplication {

    public static void main(String[] args) {
        SpringApplication.run(CheckerBinanceOneApplication.class, args);
        SocketService.connect();
    }

}
