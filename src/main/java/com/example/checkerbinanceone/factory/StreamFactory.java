package com.example.checkerbinanceone.factory;

import com.example.checkerbinanceone.dto.StreamDto;
import org.json.JSONObject;

public class StreamFactory {

    public static StreamDto createStreamDto(String source) {
        JSONObject obj = new JSONObject(source).getJSONObject("data");
        double price = obj.getDouble("p");
        String ticket = obj.getString("s");
        return new StreamDto(ticket, price);
    }
}
