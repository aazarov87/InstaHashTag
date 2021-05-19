package ru.azarov.instahashtag_bot.InstaHelper;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUserFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.*;
import org.telegram.telegrambots.api.methods.send.SendMessage;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstaCommand {

    public InstaCommand(){

    }

    private final int countReaderPosts = 10;

    public List<Map.Entry<String, Integer>> getHashTagList(String textSearch, Instagram4j instagram) throws IOException {
        HashMap<String, Integer> mapHashTag = new HashMap<String, Integer>();

        //try {
            InstagramSearchUsersResult usersRequest = instagram.sendRequest(new InstagramSearchUsersRequest(textSearch));
            /*System.out.println("size = " + usersRequest.getUsers().size());
            System.out.println("first full_name = " + usersRequest.getUsers().get(1).full_name);
            System.out.println("first username = " + usersRequest.getUsers().get(1).username);*/

            //List<InstagramSearchUsersResultUser> user = InstagramUserFeedRequest(usersRequest.getUsers());

            for (InstagramSearchUsersResultUser user : usersRequest.getUsers()
            ) {
                //user.getPk()
                System.out.println("!!!! username = " + user.username);
                // получение постов
                InstagramFeedResult postList = null;

                postList = instagram.sendRequest(new InstagramUserFeedRequest(/*usersRequest.getUsers().get(1).getPk()*/user.getPk()));


                if (postList != null && postList.getItems() != null/*&& user.username.equals("olga_vyazalochkina")*/) {

                    System.out.println("читаем посты");

                    int countPosts = 0;

                    for (InstagramFeedItem post : postList.getItems()) {
                        if (post.caption != null){
                            String postText = post.caption.getText();

                            if (postText != null) {
                                //System.out.println(postText);

                                System.out.println("пост № " + countPosts + "  " + postText);

                                int idx;
                                int idxEnd;
                                int idxNext;
                                String text;
                                String hashTag;

                                text = post.caption.getText();

                                idx = text.indexOf("#");
                                System.out.println("idx = " + idx);
                                while (idx > -1) {
                                    //idxEnd = text.indexOf(" ", idx+1);

                                    idxEnd = -1;
                                    text = text.substring(idx);

                                    Pattern pt = Pattern.compile("[\\n\\s.,!]");

                                    System.out.println("анализируем текс " + text);
                                    Matcher mt = pt.matcher(text);

                                    idxNext = text.indexOf("#", 1);

                                    //System.out.println("mtHash.find() " + mtHash.find());

                                    if (mt.find()) {
                                        //System.out.println("Нашли");
                                        System.out.println(mt.start());
                                        idxEnd = mt.start();

                                    } else {
                                        System.out.println("Not found");
                                    }

                                    if ((idxNext > -1 ) && (idxEnd > idxNext || idxEnd == -1))
                                        idxEnd = idxNext;

                                    System.out.println("idxEnd = " + idxEnd);
                                    if (idxEnd > -1)
                                        hashTag = text.substring(0, idxEnd);
                                    else
                                        hashTag = text;

                                    System.out.println("hashTag = " + hashTag + " Записали в мапу, пробуем читать  с индекса " + (idx + 1));
                                    mapHashTag.put(hashTag, mapHashTag.getOrDefault(hashTag, 0) + 1);
                                    idx = text.indexOf("#", 1);
                                    System.out.println("idx = " + idx);
                                }
                            }
                        }

                        countPosts += 1;

                        if (countPosts == countReaderPosts)
                            break;
                    }
                }
            }

            //сортируем
            List<Map.Entry<String, Integer>> list =
                    new LinkedList<>(mapHashTag.entrySet());
            Collections.sort( list, new Comparator<Map.Entry<String, Integer>>()
            {
                @Override
                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2)
                {
                    return (o1.getValue()).compareTo( o2.getValue() );
                }
            } );

            /*for (Map.Entry<String, Integer> pair : list) {
                System.out.println(pair.getKey() + " = " + pair.getValue());
            }*/

        return list;
    }
}
