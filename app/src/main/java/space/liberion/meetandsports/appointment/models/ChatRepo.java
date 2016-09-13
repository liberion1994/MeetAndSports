package space.liberion.meetandsports.appointment.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/4/6.
 */
public class ChatRepo {

    public static class Chat {
        public String id;
        public FriendRepo user1;
        public FriendRepo user2;
        public List<ChatMessage> contents;
    }

    private static List<Chat> chatList = new ArrayList<>();

    public static void add(Chat chat) {
        chatList.add(chat);
    }

    public static Chat findChatById(String id) {
        if (chatList.size() == 0)
            return null;
        for (Chat chat : chatList) {
            if (chat.id.equals(id))
                return chat;
        }
        return null;
    }

    public static boolean remove(Chat chat) {
        return chatList.remove(chat);
    }

    public static void load() {

    }
}
