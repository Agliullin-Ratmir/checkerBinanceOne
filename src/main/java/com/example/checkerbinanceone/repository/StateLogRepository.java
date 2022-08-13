package com.example.checkerbinanceone.repository;

import com.example.checkerbinanceone.entity.StateLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateLogRepository extends JpaRepository<StateLog, Long> {

    Optional<StateLog> getStateLogByUserChatId(String userChatId);
    int deleteAllByUserChatId(String userChatId);
}
