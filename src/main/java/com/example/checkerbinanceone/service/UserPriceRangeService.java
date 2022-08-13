package com.example.checkerbinanceone.service;

import com.example.checkerbinanceone.dto.UserPriceRangeDto;
import com.example.checkerbinanceone.entity.PricePair;
import com.example.checkerbinanceone.entity.Ticket;
import com.example.checkerbinanceone.entity.UserPriceRange;
import com.example.checkerbinanceone.repository.PricePairRepository;
import com.example.checkerbinanceone.repository.TicketRepository;
import com.example.checkerbinanceone.repository.UserPriceRangeRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserPriceRangeService {

    private static final String TEMPLATE_RANGES_OF_USER =
            "Ticket: %s, lower price: %s, higher price: %s";

//    @Autowired
  //  private final SessionFactory sessionFactory;
    private final CacheService cacheService;
    private final UserPriceRangeRepository userPriceRangeRepository;
    private final PricePairRepository pricePairRepository;
    private final TicketRepository ticketRepository;

    public Map<String, PricePair> getChatIdsPriceInRange(double price, List<UserPriceRange> userPriceRanges) {
        Map<String, PricePair> map = new HashMap<>(0);
        for (UserPriceRange userPriceRange : userPriceRanges) {
            userPriceRange.getTickets().stream()
                    .forEach(item -> {
                  if (isPriceInPair(price, item.getPricePair())) {
                      map.put(userPriceRange.getUserChatId(), item.getPricePair());
                  }
                });
//            if (isPriceInPair(price, userPriceRange.getPricePair())) {
//                map.put(userPriceRange.getUserChatId(), userPriceRange.getPricePair());
//            }

        }
        return map;
    }

    @Transactional
    public void updateLowerPrice(long id, double lowerPrice) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        ticket.getPricePair().setLowerPrice(lowerPrice);
        ticketRepository.save(ticket);
        cacheService.rebootCache();
    }

    @Transactional
    public void updateHigherPrice(long id, double higherPrice) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        ticket.getPricePair().setHigherPrice(higherPrice);
        ticketRepository.save(ticket);
        cacheService.rebootCache();
    }

    @Transactional
    public void removeUserPriceRange(long id) {
        userPriceRangeRepository.deleteById(id);
        userPriceRangeRepository.flush();
        cacheService.rebootCache();
    }

    @Transactional
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Transactional
    public Set<Ticket> getUserPriceRangeTickets(String chatId) {
        Set<Ticket> tickets = new HashSet<>();
        List<UserPriceRange> userPriceRangeList = userPriceRangeRepository.getUserPriceRangesByUserChatId(chatId);
        for (UserPriceRange userPriceRange : userPriceRangeList) {
            tickets.addAll(userPriceRange.getTickets());
        }
        return tickets;
    }

    public boolean isPossibleToAdd(String chatId) {
        int count = userPriceRangeRepository.countUserPriceRangesByUserChatId(chatId);
        return count < 3;
    }

    @Transactional
    public void saveNewUserPriceRange(UserPriceRangeDto dto) {
        PricePair pricePair = new PricePair();
        pricePair.setLowerPrice(dto.getLowerPrice());
        pricePair.setHigherPrice(dto.getHigherPrice());

        Ticket ticket = new Ticket();
        ticket.setTicketTitle(dto.getTicketTitle());
        ticket.setPricePair(pricePair);
        pricePair.setTicket(ticket);

        UserPriceRange userPriceRange = new UserPriceRange();
        userPriceRange.setUserChatId(dto.getUserChatId());

        ticket.setUserPriceRanges(Set.of(userPriceRange));
        userPriceRange.getTickets().add(ticket);
        userPriceRangeRepository.saveAndFlush(userPriceRange);
        cacheService.rebootCache();
    }

    private boolean isPriceInPair(double price, PricePair pricePair) {
        if (price >= pricePair.getLowerPrice() && price <= pricePair.getHigherPrice()) {
            return true;
        }
        return false;
    }
}
