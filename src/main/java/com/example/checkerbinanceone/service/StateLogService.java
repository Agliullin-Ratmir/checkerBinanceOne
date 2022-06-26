package com.example.checkerbinanceone.service;


import com.example.checkerbinanceone.dto.FlowState;
import com.example.checkerbinanceone.entity.StateLog;
import com.example.checkerbinanceone.repository.StateLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StateLogService {

    private final StateLogRepository stateLogRepository;

    public StateLog getStateOfUserByChatId(String chatId) {
        Optional<StateLog> stateLog = stateLogRepository.getStateLogByUserChatId(chatId);
        if (stateLog.isEmpty()) {
            StateLog newStateLog = addStateLogNewTitleState(chatId);
            return newStateLog;
        }
        return stateLog.get();
    }

    @Transactional
    public StateLog addStateLogNewTitleState(String chatId) {
        StateLog stateLog = new StateLog();
        stateLog.setFlowState(FlowState.ADD_TICKET_TITLE);
        stateLog.setUserChatId(chatId);
        return stateLogRepository.save(stateLog);
    }

    @Transactional
    public void addStateLogNewTitle(StateLog stateLog, String title) {
        stateLog.setTicketTitle(title);
        stateLog.setFlowState(FlowState.ADD_LOWER_PRICE);
        stateLogRepository.save(stateLog);
    }

    @Transactional
    public void addStateLogLowerPrice(StateLog stateLog, double lowerPrice) {
        stateLog.setLowerPrice(lowerPrice);
        stateLog.setFlowState(FlowState.ADD_HIGHER_PRICE);
        stateLogRepository.save(stateLog);
    }

    @Transactional
    public void removeStateLog(StateLog stateLog) {
        stateLogRepository.delete(stateLog);
    }
}
