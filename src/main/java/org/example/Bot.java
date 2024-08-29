package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private String selectedLanguage = "en";

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
        if (update.hasMessage() && update.getMessage().hasText()) {
            long idFromUser = update.getMessage().getFrom().getId();
            String textUser = update.getMessage().getText();
            if (textUser.equals("/start")) {
                sendLanguageSelectionButton(idFromUser, "Choose language.");
            } else {
                processUserMessage(idFromUser, textUser);
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            switch (callbackData) {
                case "ukrainian":
                    selectedLanguage = "uk";
                    sendTextWithHelpButton(chatId, "Привіт! Я бот, який може додавати, віднімати, множити та ділити числа. (+,-,*,/)\nПриклад повідомлення: \"12 + 12\".");
                    break;
                case "english":
                    selectedLanguage = "en";
                    sendTextWithHelpButton(chatId, "Hello! I'm a bot that can add, subtract, multiply, and divide numbers. (+,-,*,/)\nExample of a message: \"12 + 12\".");
                    break;
                case "polish":
                    selectedLanguage = "pl";
                    sendTextWithHelpButton(chatId, "Cześć! Jestem botem, który może dodawać, odejmować, mnożyć i dzielić liczby. (+,-,*,/)\nPrzykład wiadomości: \"12 + 12\".");
                    break;
                case "help":
                    sendHelpMessage(chatId);
                    break;
            }
        }
    }

    public void processUserMessage(long chatId, String message) {
        String[] cuted = parseInput(message);
        if (cuted == null) {
            sendTextWithHelpButton(chatId, getLocalizedMessage("invalid_format"));
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
                    return getLocalizedMessage("divide_by_zero");
                result = num1 / num2;
                break;
            default:
                return getLocalizedMessage("invalid_operator");
        }
        return getLocalizedMessage("result") + ": " + result;
    }

    public void sendText(Long who, String what) {
        SendMessage send = SendMessage.builder().chatId(who.toString()).text(what).build();
        try {
            execute(send);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }

    public void sendLanguageSelectionButton(Long chatId, String text) {
        SendMessage message = SendMessage.builder().chatId(chatId.toString()).text(text).build();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        InlineKeyboardButton ukrButton = InlineKeyboardButton.builder()
                .text("Українська")
                .callbackData("ukrainian")
                .build();
        InlineKeyboardButton engButton = InlineKeyboardButton.builder()
                .text("English")
                .callbackData("english")
                .build();
        InlineKeyboardButton polButton = InlineKeyboardButton.builder()
                .text("Polski")
                .callbackData("polish")
                .build();
        row1.add(ukrButton);
        row1.add(engButton);
        row1.add(polButton);
        rows.add(row1);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendTextWithHelpButton(Long chatId, String text) {
        SendMessage message = SendMessage.builder().chatId(chatId.toString()).text(text).build();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton helpButton = InlineKeyboardButton.builder()
                .text(getLocalizedMessage("help_button"))
                .callbackData("help")
                .build();
        row.add(helpButton);
        rows.add(row);

        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendHelpMessage(Long chatId) {
        String helpMessage;
        switch (selectedLanguage) {
            case "uk":
                helpMessage = "Щоб скористатися ботом, відправте повідомлення у форматі:\n\n<number1> <operator> <number2>\n\nНаприклад:\n12 + 12\n15 * 4\n7.5 / 2.5";
                break;
            case "pl":
                helpMessage = "Aby skorzystać z bota, wyślij wiadomość w następującym formacie:\n\n<number1> <operator> <number2>\n\nNa przykład:\n12 + 12\n15 * 4\n7.5 / 2.5";
                break;
            default:
                helpMessage = "To use this bot, send a message in the following format:\n\n<number1> <operator> <number2>\n\nFor example:\n12 + 12\n15 * 4\n7.5 / 2.5";
                break;
        }
        sendText(chatId, helpMessage);
    }
    public String getLocalizedMessage(String key) {
        switch (key) {
            case "invalid_format":
                switch (selectedLanguage) {
                    case "uk":
                        return "Невірний формат.";
                    case "pl":
                        return "Nieprawidłowy format.";
                    default:
                        return "Invalid format.";
                }
            case "divide_by_zero":
                switch (selectedLanguage) {
                    case "uk":
                        return "Неможливо ділити на нуль.";
                    case "pl":
                        return "Nie można dzielić przez zero.";
                    default:
                        return "Cannot divide by zero.";
                }
            case "invalid_operator":
                switch (selectedLanguage) {
                    case "uk":
                        return "Неправильний оператор. Використовуйте +, -, *, /.";
                    case "pl":
                        return "Nieprawidłowy operator. Użyj +, -, *, /.";
                    default:
                        return "Invalid operator. Use +, -, *, /.";
                }
            case "result":
                switch (selectedLanguage) {
                    case "uk":
                        return "Результат";
                    case "pl":
                        return "Wynik";
                    default:
                        return "Result";
                }
            case "help_button":
                switch (selectedLanguage) {
                    case "uk":
                        return "Допомога";
                    case "pl":
                        return "Pomoc";
                    default:
                        return "Help";
                }
            default:
                return "";
        }
    }
}
