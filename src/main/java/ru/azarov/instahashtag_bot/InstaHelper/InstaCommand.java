package ru.azarov.instahashtag_bot.InstaHelper;

import org.brunocvcunha.instagram4j.Instagram4j;
import org.brunocvcunha.instagram4j.requests.InstagramSearchUsersRequest;
import org.brunocvcunha.instagram4j.requests.InstagramUserFeedRequest;
import org.brunocvcunha.instagram4j.requests.payload.*;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstaCommand {

    public InstaCommand(){

    }
    // количество последних постов для анализа
    private final int countReaderPosts = 10;

    // шаблон для поиска конца хэштега
    private final Pattern pt = Pattern.compile("[\\n\\s.,!]");

    /**
     * Метод анализирует параметр textSearch для сбора информации по хештегам
     * @param textSearch строка для поиска аккаунтов инстаграмм
     * @param instagram экземпляр класса Instagram4j для подключения к API instagramm
     * @return Отсортированный список уникальных хэштегов и их количество. Отсортирован по возрастанию
     * @throws IOException
     */
    public List<Map.Entry<String, Integer>> getHashTagList(String textSearch, Instagram4j instagram) throws IOException {
        HashMap<String, Integer> mapHashTag = new HashMap<String, Integer>();

            InstagramSearchUsersResult usersRequest = instagram.sendRequest(new InstagramSearchUsersRequest(textSearch));

            for (InstagramSearchUsersResultUser user : usersRequest.getUsers()) {
                System.out.println("username = " + user.username);
                // получение постов
                InstagramFeedResult postList;

                postList = instagram.sendRequest(new InstagramUserFeedRequest(user.getPk()));

                if (postList != null && postList.getItems() != null) {

                    System.out.println("читаем посты");

                    int countPosts = 0;

                    for (InstagramFeedItem post : postList.getItems()) {
                        if (post.caption != null){
                            String postText = post.caption.getText();

                            if (postText != null) {
                                int idx;
                                int idxEnd;
                                int idxNext;
                                String text;
                                String hashTag;

                                text = post.caption.getText();

                                // ищем символ хэштэга
                                idx = text.indexOf("#");
                                while (idx > -1) {
                                    idxEnd = -1;
                                    text = text.substring(idx);

                                    Matcher mt = pt.matcher(text);

                                    idxNext = text.indexOf("#", 1);

                                    //проверыем нашли ли символ
                                    if (mt.find()) {
                                        idxEnd = mt.start();
                                    }

                                    if ((idxNext > -1 ) && (idxEnd > idxNext || idxEnd == -1))
                                        idxEnd = idxNext;

                                    if (idxEnd > -1)
                                        hashTag = text.substring(0, idxEnd);
                                    else
                                        hashTag = text;

                                    System.out.println("hashTag = " + hashTag + " Записали в мапу, пробуем читать  с индекса " + (idx + 1));
                                    mapHashTag.put(hashTag, mapHashTag.getOrDefault(hashTag, 0) + 1);
                                    idx = text.indexOf("#", 1);
                                }
                            }
                        }

                        countPosts += 1;

                        //ограничиваем количество постов на чтение
                        if (countPosts == countReaderPosts)
                            break;
                    }
                }
            }

            //сортируем по количеству хэштэгов
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

        return list;
    }
}
