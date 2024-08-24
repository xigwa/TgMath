package org.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    private static String id = "7296799246";
    private static String token = "AAFGmvnGFMFbPdMh-vbXN75Sti-wmI_Z2k4";
    public static void main(String[] args) {
        // Создаем объект TelegramBotsApi
        TelegramBotsApi botsApi = null;
        try {
            botsApi = new TelegramBotsApi(DefaultBotSession.class);
            // Регистрируем нашего бота
            botsApi.registerBot(new Bot());
            System.out.println("Бот успешно запущен!");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
