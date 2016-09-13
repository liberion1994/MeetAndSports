package space.liberion.meetandsports.booking;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import space.liberion.meetandsports.R;
import space.liberion.meetandsports.main.MyApplication;
import space.liberion.meetandsports.main.UrlFetcherThread;

/**
 * A simple {@link Fragment} subclass.
 */
public class OneDayPlaysFragment extends Fragment {

    private static class Handler1 extends Handler {
        private WeakReference<OneDayPlaysFragment> oneDayPlaysFragmentWeakReference;

        public Handler1(OneDayPlaysFragment oneDayPlaysFragment) {
            this.oneDayPlaysFragmentWeakReference = new WeakReference<>(oneDayPlaysFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            OneDayPlaysFragment oneDayPlaysFragment = oneDayPlaysFragmentWeakReference.get();
            if (oneDayPlaysFragment != null) {
                if (msg.what == UrlFetcherThread.MESSAGE_SUCCESS) {
                    try {
                        JSONArray plays = new JSONArray((String) msg.obj);
                        oneDayPlaysFragment.init(plays);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == UrlFetcherThread.MESSAGE_FAIL) {
                    Toast.makeText(oneDayPlaysFragment.getContext(), "无法连接至服务器，请检查您的网络设置",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static class PlayGridViewAdapter extends BaseAdapter {
        private JSONArray plays;
        private Context context;

        private static class ViewHolder {
            public CheckedTextView textView;
        }

        public PlayGridViewAdapter(JSONArray plays, Context context) {
            this.plays = plays;
            this.context = context;
        }

        @Override
        public int getCount() {
            return plays.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return plays.getJSONObject(position);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_plays_grid_view, null);
                viewHolder = new ViewHolder();
                viewHolder.textView = (CheckedTextView) convertView.findViewById(R.id.item_plays_list_view_text);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            try {
                JSONObject play = plays.getJSONObject(position);
                Date startDate = MyApplication.mongoDateFormatter.parse(play.getString("startDate"));
                Date endDate = MyApplication.mongoDateFormatter.parse(play.getString("endDate"));
                String dateString = simpleDateFormat.format(startDate) + " - " + simpleDateFormat.format(endDate);
                boolean bookable = play.getBoolean("bookable");
                if (!bookable) {
                    viewHolder.textView.setTextColor(Color.LTGRAY);
                } else {
                    viewHolder.textView.setTextColor(Color.DKGRAY);
                }
                viewHolder.textView.setText(dateString);
                viewHolder.textView.setEnabled(bookable);
                viewHolder.textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((CheckedTextView) v).toggle();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

            return convertView;
        }
    }

    private String title;
    private String campusName;
    private String name;
    private Date startDate;

    private Handler1 mHandler;

    public OneDayPlaysFragment() {
        // Required empty public constructor
        mHandler = new Handler1(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one_day_plays, container, false);
    }

    public String getTitle() {
        return title;
    }

    public void setArgs(String title, String campusName, String name, Date startDate) {
        this.title = title;
        this.campusName = campusName;
        this.name = name;
        this.startDate = startDate;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            String url = MyApplication.URL_CAMPUSES_JSON + URLEncoder.encode(campusName, "utf-8")
                    + "/courts/" + URLEncoder.encode(name, "utf-8") + "/plays?date=" +
                    MyApplication.mongoDateFormatter.format(startDate);
            new UrlFetcherThread(mHandler, url).start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    private void init(JSONArray plays) {
        GridView gridView = (GridView) getView().findViewById(R.id.one_day_play_grid);
        gridView.setOnItemClickListener(null);
        gridView.setAdapter(new PlayGridViewAdapter(plays, getContext()));

    }
}
