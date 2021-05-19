package ru.azarov.instahashtag_bot;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.azarov.instahashtag_bot.Bot.Bot;

public class App {
    public static void main(String[] args) {
        ApiContextInitializer.init();

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "applicationContext.xml"
        );

        Bot bot = context.getBean("Bot", Bot.class);

        System.out.println(bot.getBotUsername());
        System.out.println(bot.getBotToken());

        bot.botConnect();

        context.close();
    }
}
