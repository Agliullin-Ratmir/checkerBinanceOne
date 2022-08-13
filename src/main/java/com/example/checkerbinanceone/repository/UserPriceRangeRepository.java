package com.example.checkerbinanceone.repository;

import com.example.checkerbinanceone.entity.UserPriceRange;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UserPriceRangeRepository extends JpaRepository<UserPriceRange, Long> {

    List<UserPriceRange> getUserPriceRangesByUserChatId(String userChatId);
    int countUserPriceRangesByUserChatId(String userChatId);
}
