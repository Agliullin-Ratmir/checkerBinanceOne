package com.example.checkerbinanceone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class StreamDto {

    private String ticket;
    private double price;
}
