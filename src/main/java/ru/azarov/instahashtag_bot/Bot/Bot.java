package ru.azarov.instahashtag_bot.Bot;

import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.azarov.instahashtag_bot.Authentication;
import ru.azarov.instahashtag_bot.InstaHelper.InstaCommand;
import ru.azarov.instahashtag_bot.InstaHelper.InstaUser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Bot extends TelegramLongPollingBot {
    final int RECONNECT_PAUSE = 10000;

    public void setBotUsername(String botUsername) {
        this.botUsername = botUsername;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

    private String botUsername;
    private String botToken;

    //максимальное число символов для одного сообщения отправляемого в телеграмм
    private int maxLenghtForMessage = 4096;

    // map зарегистрированных пользователей
    private HashMap<Integer, InstaUser> mapUser = new HashMap<Integer, InstaUser>();

    public Bot(){
    }

    @Override
    public void onUpdateReceived(Update update) {

        Long chatId = update.getMessage().getChatId();

        // получаем идентификатор пользователя в чате
        Integer userBotId = update.getMessage().getFrom().getId();
        InstaUser instaUser;

        // запоминаем зарегистрированных пользователе
        if (mapUser.containsKey(userBotId))
            instaUser = mapUser.get(userBotId);
        else {
            instaUser = new InstaUser();
            mapUser.put(userBotId, instaUser);
        }

        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        String inputText = update.getMessage().getText();

        System.out.println("inputText = " + inputText);
        try {
            if (inputText.startsWith("/start")) {
                message.setText("Привет! Введите логи и пароль для инстаграмм через пробел");
                execute(message);
            } else if (inputText.startsWith("/clear")) {
                if (instaUser.getInstagram() == null) {
                    instaUser.getInstagram().setup();
                    instaUser.clearData();
                }
            } else {
                if (!inputText.equals("null")) { // запись логина и пароля
                    System.out.println("instaUser.getLogin() = " + instaUser.getLogin());
                    if (instaUser.getLogin() == null) {

                        instaUser.setLoginAndPass(inputText);
                        if (instaUser.getLogin() == null || instaUser.getPassword() == null) {
                            message.setText("Ошибка в чтение логина и пароля. Введите данные повторно");
                            execute(message);
                            instaUser.clearData();
                        } else {
                            Authentication authentication = new Authentication(instaUser);
                            authentication.run();

                            //System.out.println(instaUser.getInstagram());
                            if (instaUser.getInstagram() != null) {
                                message.setText("Авторизация в инстгарамм с логином " + instaUser.getLogin() + " выполнена успешно. Введите ключевое слово для поиска хэштегов");
                                execute(message);
                            } else {
                                message.setText("Авторизация в инстгарамм с логином " + instaUser.getLogin() + " НЕ выполнена");
                                execute(message);
                                instaUser.clearData();
                            }
                        }
                    } else {
                        System.out.println("Обработка поисковой фразы");

                        InstaCommand instaCommand = new InstaCommand();
                        try {
                            instaUser.setListHashTag( instaCommand.getHashTagList(inputText, instaUser.getInstagram()));

                            String textForSend = null;
                            int prevValue = 0;
                            //System.out.println("мапа собрана");
                            for (Map.Entry<String, Integer> pair : instaUser.getListHashTag()) {
                                //System.out.println(pair.getKey() + " = " + pair.getValue());

                                if (prevValue == 0 || prevValue != pair.getValue()){
                                    prevValue = pair.getValue();
                                    message.setText("количество = " + prevValue);
                                    execute(message);

                                    if (textForSend != null){
                                        message.setText(textForSend);
                                        execute(message);
                                        textForSend = null;
                                    }
                                }

                                if (textForSend == null)
                                    textForSend = pair.getKey() + " ";
                                else if ( textForSend == null || (textForSend.length() + pair.getKey().length() + pair.getValue().toString().length() + 1) < maxLenghtForMessage)
                                    textForSend = textForSend + pair.getKey() + " ";
                                else {
                                    //System.out.println("отправляем textForSend = " + textForSend);
                                    message.setText(textForSend);
                                    execute(message);
                                    textForSend = null;
                                }
                            }

                            if (textForSend != null){
                                message.setText(textForSend);
                                execute(message);
                            }
                            instaUser.getListHashTag().clear();

                        } catch (IOException e) {
                            e.printStackTrace();
                            instaUser.clearData();
                        }
                    }

                }
            }
        }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
            System.out.println("connect");
        } catch (TelegramApiRequestException e) {
            System.out.println("tryReconnect");
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }
}
