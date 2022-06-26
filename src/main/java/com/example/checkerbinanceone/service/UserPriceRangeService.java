package com.example.checkerbinanceone.service;

import com.example.checkerbinanceone.dto.UserPriceRangeDto;
import com.example.checkerbinanceone.entity.PricePair;
import com.example.checkerbinanceone.entity.Ticket;
import com.example.checkerbinanceone.entity.UserPriceRange;
import com.example.checkerbinanceone.repository.UserPriceRangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserPriceRangeService {

    private final UserPriceRangeRepository userPriceRangeRepository;

    public Map<String, PricePair> getChatIdsPriceInRange(double price, List<UserPriceRange> userPriceRanges) {
        Map<String, PricePair> map = new HashMap<>(0);
        for (UserPriceRange userPriceRange : userPriceRanges) {
//            userPriceRange.getPricePairs().stream()
//                    .forEach(item -> {
//                  if (isPriceInPair(price, item)) {
//                      map.put(userPriceRange.getUserChatId(), item);
//                  }
//                });
            if (isPriceInPair(price, userPriceRange.getPricePair())) {
                map.put(userPriceRange.getUserChatId(), userPriceRange.getPricePair());
            }

        }
        return map;
    }

    public void saveNewUserPriceRange(UserPriceRangeDto dto) {
        PricePair pricePair = new PricePair();
        pricePair.setLowerPrice(dto.getLowerPrice());
        pricePair.setHigherPrice(dto.getHigherPrice());

        Ticket ticket = new Ticket();
        ticket.setTicketTitle(dto.getTicketTitle());

        UserPriceRange userPriceRange = new UserPriceRange();
        userPriceRange.setUserChatId(dto.getUserChatId());

//        ticket.setUserPriceRanges(Set.of(userPriceRange));
        userPriceRange.setPricePair(pricePair);
        pricePair.setUserPriceRange(userPriceRange);
        //userPriceRange.getTickets().add(ticket);
        userPriceRangeRepository.save(userPriceRange);
    }

    private boolean isPriceInPair(double price, PricePair pricePair) {
        if (price >= pricePair.getLowerPrice() && price <= pricePair.getHigherPrice()) {
            return true;
        }
        return false;
    }
}
