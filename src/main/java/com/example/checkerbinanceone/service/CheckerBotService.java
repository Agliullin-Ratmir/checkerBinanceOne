package com.example.checkerbinanceone.service;

import com.example.checkerbinanceone.dto.FlowState;
import com.example.checkerbinanceone.dto.UserPriceRangeDto;
import com.example.checkerbinanceone.entity.PricePair;
import com.example.checkerbinanceone.entity.StateLog;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@AllArgsConstructor
public class CheckerBotService extends TelegramLongPollingBot {

    private static final String ADD_NEW_PRICE_RANGE_TITLE = "Add new price range";
    private static final String SHOW_MY_PRICE_RANGES_TITLE = "Show my price ranges";

    private final StateLogService stateLogService;
    private final UserPriceRangeService userPriceRangeService;
    @Override
    public String getBotUsername() {
        return "CheckerOneTestBot";
    }

    @Override
    public String getBotToken() {
        return "5340708177:AAHrs3_3nJwcOGgu8Mgz07JI1N40nF0G9CA";
    }

    @Override
    public void onUpdateReceived(Update update) {
        String command = update.getMessage().getText();
        String chatId = update.getMessage().getChatId().toString();
        StateLog currentStateLog = stateLogService.getStateOfUserByChatId(chatId);
        if (command.equals("/start")) {
            String message = "Your chat id:" + chatId;
            SendMessage response = createStartMessage(chatId);
            response.setText(message);
            sendResponse(response);
        } else if (ADD_NEW_PRICE_RANGE_TITLE.equals(command)) {
            addNewPriceRange(chatId, update, currentStateLog.getFlowState());
        } else if (SHOW_MY_PRICE_RANGES_TITLE.equals(command)) {
            showPriceRanges(chatId);
        } else if (FlowState.ADD_TICKET_TITLE.equals(currentStateLog.getFlowState())) {
            stateLogService.addStateLogNewTitle(currentStateLog, command);
            addLowerPrice(null, chatId, update);
        } else if (FlowState.ADD_LOWER_PRICE.equals(currentStateLog.getFlowState())) {
            stateLogService.addStateLogLowerPrice(currentStateLog, Double.parseDouble(command));
            addHigherPrice(null, chatId, update);
        } else if (FlowState.ADD_HIGHER_PRICE.equals(currentStateLog.getFlowState())) {
            System.out.println("ADD_HIGHER_PRICE");
            UserPriceRangeDto dto = new UserPriceRangeDto(chatId);
            dto.setTicketTitle(currentStateLog.getTicketTitle());
            dto.setLowerPrice(currentStateLog.getLowerPrice());
            dto.setHigherPrice(Double.parseDouble(command));
            userPriceRangeService.saveNewUserPriceRange(dto);
            stateLogService.removeStateLog(currentStateLog);
            // add to price range, ticket, userPriceRange
        }
    }

    private void addNewPriceRange(String chatId, Update update, FlowState currentState) {
        UserPriceRangeDto dto = new UserPriceRangeDto(chatId);
        if (FlowState.ADD_TICKET_TITLE.equals(currentState)) {
            addTitleMessage(dto, chatId, update);
        } else if (FlowState.ADD_LOWER_PRICE.equals(currentState)) {
            addLowerPrice(dto, chatId, update);
        } else if (FlowState.ADD_HIGHER_PRICE.equals(currentState)) {
            addHigherPrice(dto, chatId, update);
        }
    }

    private void addTitleMessage(UserPriceRangeDto dto, String chatId, Update update) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Back");
        backButton.setCallbackData("Back");

        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("Cancel");
        cancelButton.setCallbackData("Cancel");
        inlineKeyboardButtons.addAll(Arrays.asList(cancelButton, backButton));

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(inlineKeyboardButtons));
        SendMessage message = new SendMessage();
        message.setText("Please, type title of coin, like 'ethbbtc'");
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);

        sendResponse(message);
    }

    private void addLowerPrice(UserPriceRangeDto dto, String chatId, Update update) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Back");
        backButton.setCallbackData("Back");

        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("Cancel");
        cancelButton.setCallbackData("Cancel");
        inlineKeyboardButtons.addAll(Arrays.asList(cancelButton, backButton));

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(inlineKeyboardButtons));
        SendMessage message = new SendMessage();
        message.setText("Please, add lower price, for example: 123.45");
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);

        sendResponse(message);
    }

    private void addHigherPrice(UserPriceRangeDto dto, String chatId, Update update) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("Back");
        backButton.setCallbackData("Back");

        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("Cancel");
        cancelButton.setCallbackData("Cancel");
        inlineKeyboardButtons.addAll(Arrays.asList(cancelButton, backButton));

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(inlineKeyboardButtons));
        SendMessage message = new SendMessage();
        message.setText("Please, add higher price, for example: 123.45");
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);

        sendResponse(message);
    }

    private void showPriceRanges(String chatId) {

    }

    private void sendResponse(SendMessage response) {
        try {
            execute(response);
        }
        catch (TelegramApiException e){
            e.printStackTrace();
        }
    }

    private SendMessage createStartMessage(String chatId) {
        ReplyKeyboardMarkup replyKey = ReplyKeyboardMarkup.builder()
                .build();
        KeyboardButton newPriceRangeButton = new KeyboardButton();
        newPriceRangeButton.setText(ADD_NEW_PRICE_RANGE_TITLE);
        KeyboardButton showPriceRangesButton = new KeyboardButton();
        showPriceRangesButton.setText(SHOW_MY_PRICE_RANGES_TITLE);
        KeyboardRow firstMenuRow = new KeyboardRow();
        firstMenuRow.add(newPriceRangeButton);
        KeyboardRow secondMenuRow = new KeyboardRow();
        secondMenuRow.add(showPriceRangesButton);

        replyKey.setKeyboard(List.of(firstMenuRow, secondMenuRow));
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        replyKey.setResizeKeyboard(true);
        message.setReplyMarkup(replyKey);
        return message;
    }
}
