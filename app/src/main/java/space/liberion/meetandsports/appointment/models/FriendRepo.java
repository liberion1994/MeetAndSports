package space.liberion.meetandsports.appointment.models;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by apple on 16/4/6.
 */
public class FriendRepo {

    public static class User {
        String id;
        String nickname;
        Bitmap image;
    }

    private static List<User> userList = new ArrayList<>();

    public static void add(User user) {
        userList.add(user);
    }

    public static User findUserById(String id) {
        if (userList.size() == 0)
            return null;
        for (User user : userList) {
            if (user.id.equals(id))
                return user;
        }
        return null;
    }

    public static boolean remove(User user) {
        return userList.remove(user);
    }

    public static void load() {

    }

}
