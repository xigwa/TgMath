package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "Math32116523bot";
    }

    @Override
    public String getBotToken() {
        return "7296799246:AAFGmvnGFMFbPdMh-vbXN75Sti-wmI_Z2k4";
    }

    @Override
    public void onUpdateReceived(Update update) {
        long idFromUser = update.getMessage().getFrom().getId();
        String textUser = update.getMessage().getText();
        if (textUser.equals("/start")) {
            sendText(idFromUser, "Hello I'm a Tg Math bot. \n I can add, subtract, multiply, divide. (+,-,*,/) \n Example of what you can send: \"12 + 12\".");
        } else {
            processUserMessage(idFromUser, textUser);
        }
    }

    public void processUserMessage(long chatId, String message) {
        String[] cuted = parseInput(message);
        if (cuted == null) {
            sendText(chatId, "Invalid format. Use: number1 operator number2 (12 + 12)");
            return;
        }

        double num1 = Double.parseDouble(cuted[0]);
        String operator = cuted[1];
        double num2 = Double.parseDouble(cuted[2]);

        String result = calculate(num1, operator, num2);
        sendText(chatId, result);
    }

    public String[] parseInput(String input) {
        input = input.replaceAll("\\s+", "");

        String[] operators = {"+", "-", "*", "/"};
        for (String operator : operators)
            if (input.contains(operator)) {
                String[] tokens = input.split("\\" + operator, 2);
                if (tokens.length == 2)
                    return new String[] {tokens[0], operator, tokens[1]};
            }
        return null;
    }
    public String calculate(double num1, String operator, double num2) {
        double result;
        switch (operator) {
            case "+":
                result = num1 + num2;
                break;
            case "-":
                result = num1 - num2;
                break;
            case "*":
                result = num1 * num2;
                break;
            case "/":
                if (num2 == 0)
                    return "Cannot divide by zero.";
                result = num1 / num2;
                break;
            default:
                return "Invalid operator. Use +, -, *, /.";
        }
        return "Result: " + result;
    }

    public void sendText (Long who, String what){
        SendMessage send = SendMessage.builder().chatId(who.toString()).text(what).build();
        try {
            execute(send);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }
}