package space.liberion.meetandsports.main;

import android.app.Application;

import com.firebase.client.Firebase;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by apple on 16/3/28.
 */
public class MyApplication extends Application {
    public static final String URL_CAMPUSES_JSON = "http://115.28.20.139:3000/campuses/";
    public static final SimpleDateFormat mongoDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

}
