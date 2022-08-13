package com.example.checkerbinanceone.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestService {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();
    private static final String URL_TEMPLATE = "https://api.binance.com/api/v3/exchangeInfo?symbol=%s";
    private static final String INVALID_MESSAGE = "Invalid symbol.";

    public boolean checkTitleIsAvailable(String title) {
        String url = String.format(URL_TEMPLATE, title.toUpperCase());
        try {
            ResponseEntity<String> response = REST_TEMPLATE.getForEntity(url, String.class);
            return response.getStatusCodeValue() == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
