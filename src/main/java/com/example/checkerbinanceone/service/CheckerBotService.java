package com.example.checkerbinanceone.service;

import com.example.checkerbinanceone.dto.FlowState;
import com.example.checkerbinanceone.dto.UserPriceRangeDto;
import com.example.checkerbinanceone.entity.PricePair;
import com.example.checkerbinanceone.entity.StateLog;
import com.example.checkerbinanceone.entity.Ticket;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
    private static final String MORE_BUTTON_TITLE = "More";

    private static final String SHOW_MY_PRICE_RANGES_TITLE = "Show my price ranges";
    private static final String TEMPLATE_RANGES_OF_USER =
            "Ticket: %s, lower price: %s, higher price: %s";
    private static final String LOWER_PRICE_TITLE = "lower price";
    private static final String HIGHER_PRICE_TITLE = "higher price";

    private static final String LOWER_PRICE_CHANGE_TEMPLATE = "%s_lowerPrice";
    private static final String HIGHER_PRICE_CHANGE_TEMPLATE = "%s_higherPrice";
    private static final String TEMPLATE_INLINE_EDIT = "%s_EDIT";
    private static final String TEMPLATE_INLINE_REMOVE = "%s_REMOVE";

    private final StateLogService stateLogService;
    private final UserPriceRangeService userPriceRangeService;
    private final RestService restService;
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
        String chatId;
        if (update.getCallbackQuery() != null) {
            chatId = update.getCallbackQuery().getFrom().getId().toString();
            handleInlineButton(chatId, update.getCallbackQuery());
            return;
        }
        chatId = update.getMessage().getChatId().toString();
        String command = update.getMessage().getText();
        StateLog currentStateLog = stateLogService.getStateOfUserByChatId(chatId);
        boolean currentStateExists = currentStateLog != null;
        if (command.equals("/start")) {
            String message = "Your chat id:" + chatId;
            SendMessage response = createStartMessage(chatId);
            response.setText(message);
            sendResponse(response);
        } else if (ADD_NEW_PRICE_RANGE_TITLE.equals(command)) {
            addNewPriceRange(chatId, update);
        } else if (MORE_BUTTON_TITLE.equals(command)) {
            // more info
        } else if (SHOW_MY_PRICE_RANGES_TITLE.equals(command)) {
            showPriceRanges(chatId);
        } else if (currentStateExists && FlowState.ADD_TICKET_TITLE.equals(currentStateLog.getFlowState())) {
            if (!restService.checkTitleIsAvailable(command)) { // check
                stateLogService.removeStateLogByChatId(chatId);
                sendTextResponse(chatId, "This ticket does not exist");
                return;
            }
            stateLogService.addStateLogNewTitle(currentStateLog, command);
            addLowerPrice(null, chatId, update);
        } else if (currentStateExists && FlowState.ADD_LOWER_PRICE.equals(currentStateLog.getFlowState())) {
            stateLogService.addStateLogLowerPrice(currentStateLog, Double.parseDouble(command));
            addHigherPrice(null, chatId, update);
        } else if (currentStateExists && FlowState.ADD_HIGHER_PRICE.equals(currentStateLog.getFlowState())) {
            UserPriceRangeDto dto = new UserPriceRangeDto(chatId);
            dto.setTicketTitle(currentStateLog.getTicketTitle());
            dto.setLowerPrice(currentStateLog.getLowerPrice());
            dto.setHigherPrice(Double.parseDouble(command));
            userPriceRangeService.saveNewUserPriceRange(dto);
            stateLogService.removeStateLog(currentStateLog);
            sendTextResponse(chatId, "Done");
            // hibernate listener, check coin title(map<title, list<user, prices>>), maybe we can use cash hibernate
            // and use cash with listener
        } else if (currentStateExists && FlowState.UPDATE_LOWER_PRICE.equals(currentStateLog.getFlowState())) {
            double lowerPrice = Double.parseDouble(command);
            userPriceRangeService.updateLowerPrice(currentStateLog.getUpdatingTicketId(), lowerPrice);
            stateLogService.removeStateLog(currentStateLog);
            sendTextResponse(chatId, "Done");
        } else if (currentStateExists && FlowState.UPDATE_HIGHER_PRICE.equals(currentStateLog.getFlowState())) {
            double lowerPrice = Double.parseDouble(command);
            userPriceRangeService.updateHigherPrice(currentStateLog.getUpdatingTicketId(), lowerPrice);
            stateLogService.removeStateLog(currentStateLog);
            sendTextResponse(chatId, "Done");
        }
    }

    private void handleInlineButton(String chatId, CallbackQuery callbackQuery) {
        String inlineCommand = callbackQuery.getData();
        long ticketId = 0L;
        if (inlineCommand.contains("_")) {
            ticketId = Long.parseLong(inlineCommand.split("_")[0]);
        }
        if (inlineCommand.endsWith("REMOVE")) {
            userPriceRangeService.removeUserPriceRange(ticketId);
            sendTextResponse(chatId, "Done");
        } else if ((inlineCommand.endsWith("EDIT"))) {
            addEditMenu(chatId, ticketId);
        } else if ((inlineCommand.endsWith("lowerPrice"))) {
            changeLowerPrice(ticketId, chatId);
        } else if ((inlineCommand.endsWith("higherPrice"))) {
            changeHigherPrice(ticketId, chatId);
        } else if ((inlineCommand.equals("Cancel"))) {
            stateLogService.removeStateLogByChatId(chatId);
            sendTextResponse(chatId, "Done");
        } else if ((inlineCommand.equals("Back"))) {
            getBackToFlow(chatId);
        }
    }

    private void getBackToFlow(String chatId) {
        StateLog stateLog = stateLogService.getStateOfUserByChatId(chatId);
        FlowState currentState;
        if (stateLog == null) {
            return;
        }
        currentState = stateLog.getFlowState();
        if (currentState.equals(FlowState.ADD_TICKET_TITLE)) {
            stateLogService.removeStateLog(stateLog);
            sendTextResponse(chatId, "Done");
        } else if (currentState.equals(FlowState.ADD_LOWER_PRICE)) {
            stateLog.setFlowState(FlowState.ADD_TICKET_TITLE);
            stateLogService.saveStateLog(stateLog);
            addTitleMessage(null, chatId, null);
        } else if (currentState.equals(FlowState.ADD_HIGHER_PRICE)) {
            stateLog.setFlowState(FlowState.ADD_LOWER_PRICE);
            stateLogService.saveStateLog(stateLog);
            addLowerPrice(null, chatId, null);
        }
    }

    private void changeLowerPrice(long id, String chatId) {
        addCancelInlineButton(chatId, "Please, type new lower price, for example: 123.45");
        stateLogService.saveUpdatingState(chatId, id, FlowState.UPDATE_LOWER_PRICE);
    }

    private void changeHigherPrice(long id, String chatId) {
        addCancelInlineButton(chatId, "Please, type new higher price, for example: 456.78");
        stateLogService.saveUpdatingState(chatId, id, FlowState.UPDATE_HIGHER_PRICE);
    }

    private void addCancelInlineButton(String chatId, String text) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("Cancel");
        cancelButton.setCallbackData(("Cancel"));

        inlineKeyboardButtons.add(cancelButton);

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(inlineKeyboardButtons));
        SendMessage message = new SendMessage();
        message.setText(text);
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);

        sendResponse(message);
    }

    private void addNewPriceRange(String chatId, Update update) {
        UserPriceRangeDto dto = new UserPriceRangeDto(chatId);
        StateLog currentStateLog = stateLogService.getStateOfUserByChatId(chatId);
        if (!userPriceRangeService.isPossibleToAdd(chatId)) {
            sendTextResponse(chatId,
                    "You can't add new range. You are out of limit");
            return;
        }
        if (currentStateLog == null) {
            stateLogService.addStateLogNewTitleState(chatId);
            addTitleMessage(dto, chatId, update);
        } else if (FlowState.ADD_LOWER_PRICE.equals(currentStateLog.getFlowState())) {
            addLowerPrice(dto, chatId, update);
        } else if (FlowState.ADD_HIGHER_PRICE.equals(currentStateLog.getFlowState())) {
            addHigherPrice(dto, chatId, update);
        }
    }

    private void addEditMenu(String chatId, long id) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton lowerPriceButton = new InlineKeyboardButton();
        lowerPriceButton.setText(LOWER_PRICE_TITLE);
        lowerPriceButton.setCallbackData(String.format(LOWER_PRICE_CHANGE_TEMPLATE, id));

        InlineKeyboardButton higherPriceButton = new InlineKeyboardButton();
        higherPriceButton.setText(HIGHER_PRICE_TITLE);
        higherPriceButton.setCallbackData(String.format(HIGHER_PRICE_CHANGE_TEMPLATE, id));
        inlineKeyboardButtons.addAll(Arrays.asList(lowerPriceButton, higherPriceButton));

        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(inlineKeyboardButtons));
        SendMessage message = new SendMessage();
        message.setText("Please, choose, what do you want to change");
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);

        sendResponse(message);
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
        message.setText("Please, type title of coin, like 'BNBBTC'");
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
        message.setText("Please, add higher price, for example: 456.78");
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);

        sendResponse(message);
    }

    private void showPriceRanges(String chatId) {
        userPriceRangeService.getUserPriceRangeTickets(chatId)
                .forEach(item -> createMessageTicketOfUser(chatId, item));
    }

    private void createMessageTicketOfUser(String chatId, Ticket ticket) {
        String id = String.valueOf(ticket.getTicketId());
        PricePair pricePair = ticket.getPricePair();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
        InlineKeyboardButton editButton = new InlineKeyboardButton();
        editButton.setText("Edit");
        editButton.setCallbackData(String.format(TEMPLATE_INLINE_EDIT, id));

        InlineKeyboardButton removeButton = new InlineKeyboardButton();
        removeButton.setText("Remove");
        removeButton.setCallbackData(String.format(TEMPLATE_INLINE_REMOVE, id));
        inlineKeyboardButtons.addAll(Arrays.asList(removeButton, editButton));
// finish here
        inlineKeyboardMarkup.setKeyboard(Collections.singletonList(inlineKeyboardButtons));
        SendMessage message = new SendMessage();
        message.setText(String.format(TEMPLATE_RANGES_OF_USER,
                ticket.getTicketTitle().toUpperCase(),
                pricePair.getLowerPrice(), pricePair.getHigherPrice()));
        message.setReplyMarkup(inlineKeyboardMarkup);
        message.setChatId(chatId);

        sendResponse(message);
    }

    private void sendTextResponse(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(chatId);
        sendResponse(sendMessage);
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
        KeyboardButton moreButton = new KeyboardButton();
        moreButton.setText(MORE_BUTTON_TITLE);

        KeyboardRow firstMenuRow = new KeyboardRow();
        firstMenuRow.add(newPriceRangeButton);
        KeyboardRow secondMenuRow = new KeyboardRow();
        secondMenuRow.add(showPriceRangesButton);
        KeyboardRow thirdMenuRow = new KeyboardRow();
        thirdMenuRow.add(moreButton);

        replyKey.setKeyboard(List.of(firstMenuRow, secondMenuRow, thirdMenuRow));
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        replyKey.setResizeKeyboard(true);
        message.setReplyMarkup(replyKey);
        return message;
    }
}
