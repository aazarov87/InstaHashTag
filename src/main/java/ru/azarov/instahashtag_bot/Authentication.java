package ru.azarov.instahashtag_bot;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.payload.InstagramLoginResult;
import ru.azarov.instahashtag_bot.InstaHelper.InstaUser;

import java.io.IOException;

public class Authentication {
    private InstaUser user;


    public Authentication(InstaUser user) {
        this.user = user;
    }

    public void run(){
        Instagram4j instagram = Instagram4j.builder().username(user.getLogin()).password(user.getPassword()).build();
        instagram.setup();
        try {
            InstagramLoginResult instagramLoginResult = instagram.login();
            System.out.println(instagramLoginResult);
            if (instagramLoginResult.getStatus().equals("ok")) {
                user.setInstagram(instagram);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }
}
