package com.example.checkerbinanceone.dto;

import lombok.Data;

@Data
public class UserPriceRangeDto {

    public UserPriceRangeDto(String userChatId) {
        this.userChatId = userChatId;
    }
    private String userChatId;
    private String ticketTitle;
    private double lowerPrice;
    private double higherPrice;
}
