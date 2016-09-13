package space.liberion.meetandsports.main;

import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by apple on 16/3/30.
 */
public class UrlFetcherThread extends Thread {

    public static int MESSAGE_SUCCESS = 1;
    public static int MESSAGE_FAIL = 2;

    public static int TIME_OUT = 3000;

    private Handler handler;
    private String urlString;

    public UrlFetcherThread(Handler handler, String urlString) {
        this.handler = handler;
        this.urlString = urlString;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIME_OUT);
            urlConnection.setRequestProperty("Charset", "UTF-8");
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String response = "";
            String readLine;
            while ((readLine = br.readLine()) != null) {
                response = response + readLine;
            }
            is.close();
            br.close();
            urlConnection.disconnect();
            Message message = handler.obtainMessage();
            message.what = MESSAGE_SUCCESS;
            message.obj = response;
            handler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            Message message = handler.obtainMessage();
            message.what = MESSAGE_FAIL;
            handler.sendMessage(message);
        }
    }
}
