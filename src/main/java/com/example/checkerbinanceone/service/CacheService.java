package com.example.checkerbinanceone.service;

import com.example.checkerbinanceone.dto.UserPriceRangeDto;
import com.example.checkerbinanceone.entity.Ticket;
import com.example.checkerbinanceone.entity.UserPriceRange;
import com.example.checkerbinanceone.repository.TicketRepository;
import com.example.checkerbinanceone.repository.UserPriceRangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CacheService {

    private final UserPriceRangeRepository userPriceRangeRepository;
    private final TicketRepository ticketRepository;
    private Map<String, List<UserPriceRangeDto>> localCache = new HashMap<>();
    private ExecutorService executorService;

// nft, add sockets

    public Map<String, List<UserPriceRangeDto>> getLocalCache() {
        return localCache;
    }
    public void notifyUsers() {
        executorService =
                Executors.newFixedThreadPool(localCache.size());
        for (var entry : localCache.entrySet()) {
            executorService.execute(() -> {
                entry.getValue().forEach(item ->
                        System.out.println("title: " + entry.getKey()
                        + "chatId: " + item.getUserChatId()));
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Transactional
    public void rebootCache() {
        localCache.clear();
        List<UserPriceRangeDto> list;
        List<Ticket> tickets = ticketRepository.findAll();
        for (Ticket ticket : tickets) {
            if (localCache.containsKey(ticket.getTicketTitle())) {
                list = localCache.get(ticket.getTicketTitle());
            } else {
                list = new ArrayList<>(5);
            }
            for (UserPriceRange userPriceRange : ticket.getUserPriceRanges()) {
                UserPriceRangeDto dto = mapToDto(ticket, userPriceRange.getUserChatId());
                list.add(dto);
            }
            localCache.put(ticket.getTicketTitle(), list);
        }
        System.out.println("Cache size: " + localCache.size());
     //   notifyUsers();
    }

    private UserPriceRangeDto mapToDto(Ticket ticket, String chatId) {
        UserPriceRangeDto dto = new UserPriceRangeDto(chatId);
        dto.setLowerPrice(ticket.getPricePair().getLowerPrice());
        dto.setHigherPrice(ticket.getPricePair().getHigherPrice());
        return dto;
    }
}
