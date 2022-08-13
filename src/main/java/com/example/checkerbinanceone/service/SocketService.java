package com.example.checkerbinanceone.service;

import com.binance.connector.client.enums.DefaultUrls;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebsocketClientImpl;
import com.example.checkerbinanceone.dto.StreamDto;
import com.example.checkerbinanceone.dto.UserPriceRangeDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.checkerbinanceone.factory.StreamFactory.createStreamDto;

@Service
public class SocketService {

    @Autowired
    private CacheService cacheService;

    @PostConstruct
    public void connect() {

        WebsocketClientImpl client = new WebsocketClientImpl();
        cacheService.rebootCache();
        Map<String, List<UserPriceRangeDto>> cache = cacheService.getLocalCache();
        for (Map.Entry<String, List<UserPriceRangeDto>> entry : cache.entrySet()) {
            client.aggTradeStream(entry.getKey(), ((event) -> {
                checkTicket(event, entry.getValue());
            }));
        }
    }

    private boolean isPriceInRange(double price, double lower, double higher) {
        if (price <= higher && price >= lower) {
            return true;
        }
        return false;
    }

    private void checkTicket(String source, List<UserPriceRangeDto> list) {
        StreamDto dto = createStreamDto(source);
        list.forEach(item -> {
//            if (isPriceInRange(dto.getPrice(), item.getLowerPrice(),
//                    item.getHigherPrice())) {
                System.out.println("Ticket in th range:" + dto.getTicket()
                + " chatId: " + item.getUserChatId() + " price: " + dto.getPrice());
        //    }
        });
    }
}
