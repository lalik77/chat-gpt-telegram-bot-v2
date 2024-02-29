package com.mami.chatgptbot.controller;

import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class TelegramBot extends TelegramLongPollingBot {

    private static final String HELP_TEXT = """
            This bot is designed to showcase Spring capabilities! You can interact with it using the following commands:
            /start: Displays a welcome message.
            /chatgpt: Initiates a prompt conversation with the bot.
            /help: Displays this message.
            """;
    private static final String PROMPT_MESSAGE = "Please type your prompt:";

    private RestTemplate restTemplate;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final OpenAiService service;

    public TelegramBot(RestTemplate restTemplate, OpenAiService service) {
        this.restTemplate = restTemplate;
        this.service = service;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        // Check if the update has a message
        if (update.hasMessage() && update.getMessage().hasText()) {

            // Handle  commands

            switch (messageText) {

                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/chatgpt":
                    chatGptCommandReceived(chatId);
                    break;
                case "/help":
                    sendTextMessage(chatId, HELP_TEXT);
                    break;
                default:
                    processPromptResponse(chatId, messageText);
            }
        }
    }

    private void startCommandReceived(long chatId, String firstName) {

        String answer = "Hi, " + firstName + ", nice to meet you!" +
                " I am an AI digital assistant created by the OpenAI team";
        sendTextMessage(chatId, answer);
    }

    private void chatGptCommandReceived(long chatId) {
        sendTextMessage(chatId, PROMPT_MESSAGE);
    }

    private void sendTextMessage(long chatId, String text) {
        try {
            execute(new SendMessage(String.format("%d", chatId), text));

        } catch (TelegramApiException e) {
            log.error("TelegramBot#sendTextMessage(): {}", e);
        }
    }

    private void processPromptResponse(long chatId, String prompt) {
        String apiUrl = "http://localhost:8080/bot/chat?prompt=" + prompt;
        String response = restTemplate.getForObject(apiUrl, String.class);
        sendTextMessage(chatId, response);
    }
}
