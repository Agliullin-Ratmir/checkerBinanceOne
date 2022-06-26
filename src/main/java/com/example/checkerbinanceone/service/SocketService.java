package com.example.checkerbinanceone.service;

import com.binance.connector.client.enums.DefaultUrls;
import com.binance.connector.client.impl.SpotClientImpl;
import com.binance.connector.client.impl.WebsocketClientImpl;
import com.example.checkerbinanceone.dto.StreamDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.checkerbinanceone.factory.StreamFactory.createStreamDto;

public class SocketService {

    public static void connect() {

        WebsocketClientImpl client = new WebsocketClientImpl();

//Combining Streams
        ArrayList<String> streams = new ArrayList<>();
        streams.add("btcbusd@trade");
        streams.add("ethbusd@trade");



        int streamID2 = client.combineStreams(streams, ((event) -> {
            checkTicket(event);
        }));

        SpotClientImpl spotClient = new SpotClientImpl("vcih0XpquVTtlt8uuw12jzQWr0auQr87ynxnDPTKJhQXHMahXkeSE8sIy9AMeNkp",
                "ww0Y1rRN3M4JDT7iMi0wvq1MAdNtDv0HbzFU0BkAYDZEpYgzHaztLLNODTw6DO5O",
                DefaultUrls.TESTNET_URL);
        JSONObject obj = new JSONObject(spotClient.createUserData().createListenKey());
        String listenKey = obj.getString("listenKey");

//Closing a single stream
        client.closeConnection(streamID2); //closes aggTradeStream-btcusdt

//Closing all streams
        client.closeAllConnections();
    }

    private static boolean isPriceInRange(double price, double lower, double higher) {
        if (price <= higher && price >= lower) {
            return true;
        }
        return false;
    }

    private static void checkTicket(String source) {
        StreamDto dto = createStreamDto(source);
        if (isPriceInRange(dto.getPrice(), 1000d, 40000d)) {
            System.out.println("Ticket in th range:" + dto);
        }
    }
}
