package ru.azarov.instahashtag_bot.InstaHelper;

import org.brunocvcunha.instagram4j.Instagram4j;

import java.util.List;
import java.util.Map;

public class InstaUser {

    private String login;
    private String password;
    private boolean isAuthenticationOk;
    private Instagram4j instagram;
    private List<Map.Entry<String, Integer>> listHashTag;

    public InstaUser() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public boolean isAuthenticationOk() {
        return isAuthenticationOk;
    }

    public void setAuthenticationOk(boolean authenticationOk) {
        isAuthenticationOk = authenticationOk;
    }

    public Instagram4j getInstagram() {
        return instagram;
    }

    public void setInstagram(Instagram4j instagram) {
        this.instagram = instagram;
    }


    public void clearData(){
        this.login = null;
        this.password = null;
        this.isAuthenticationOk = false;
        this.instagram = null;
        this.listHashTag = null;
    }

    public void setLoginAndPass(String loginAndPass){
        int idx;
        String login = null;
        String password = null;
        idx = loginAndPass.indexOf(" ");

        if (idx > -1){
            login = loginAndPass.substring(0, idx);
            password = loginAndPass.substring(idx+1);
        }
        else
            login = loginAndPass;

        System.out.println("login = " + login + ", password = " + password);
        this.setLogin(login);
        this.setPassword(password);

    }

    public List<Map.Entry<String, Integer>> getListHashTag() {
        return listHashTag;
    }

    public void setListHashTag(List<Map.Entry<String, Integer>> listHashTag) {
        this.listHashTag = listHashTag;
    }
}