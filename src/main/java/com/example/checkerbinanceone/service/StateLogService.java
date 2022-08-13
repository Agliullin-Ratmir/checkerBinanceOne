package com.example.checkerbinanceone.service;


import com.example.checkerbinanceone.dto.FlowState;
import com.example.checkerbinanceone.entity.StateLog;
import com.example.checkerbinanceone.entity.Ticket;
import com.example.checkerbinanceone.repository.StateLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class StateLogService {

    private final StateLogRepository stateLogRepository;

    public StateLog getStateOfUserByChatId(String chatId) {
        Optional<StateLog> stateLog = stateLogRepository.getStateLogByUserChatId(chatId);
        if (stateLog.isEmpty()) {
            return null;
        }
        return stateLog.get();
    }

    @Transactional
    public StateLog saveUpdatingState(String chatId, long id, FlowState state) {
        Optional<StateLog> stateLogOptional = stateLogRepository.getStateLogByUserChatId(chatId);
        StateLog stateLog;
        if (stateLogOptional.isEmpty()) {
            stateLog = new StateLog();
            stateLog.setUserChatId(chatId);
        } else {
            stateLog = stateLogOptional.get();
        }
        stateLog.setFlowState(state);
        stateLog.setUpdatingTicketId(id);
        return stateLogRepository.save(stateLog);
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

    @Transactional
    public void removeStateLogByChatId(String chatId) {
        stateLogRepository.deleteAllByUserChatId(chatId);
    }

    @Transactional
    public void saveStateLog(StateLog stateLog) {
        stateLogRepository.save(stateLog);
    }
}
