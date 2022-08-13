package com.example.checkerbinanceone.listener;

import com.example.checkerbinanceone.entity.UserPriceRange;
import com.example.checkerbinanceone.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.transaction.Transactional;

//@Component
public class UserPriceRangeListener {

//    private static CacheService cacheService;;
//
//    @Autowired
//    public void setCacheService(CacheService cacheService) {
//        this.cacheService = cacheService;
//    }

//    @PostPersist
//    @PostRemove
//    @Transactional
    public void checkChanges(UserPriceRange userPriceRange) {
        userPriceRange.getTickets()
                        .forEach(item -> System.out.println("Title: " + item.getTicketTitle()));
    //    cacheService.rebootCache();
    }
}
