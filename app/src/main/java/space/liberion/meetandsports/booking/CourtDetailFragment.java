package space.liberion.meetandsports.booking;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import space.liberion.meetandsports.R;
import space.liberion.meetandsports.main.MyApplication;
import space.liberion.meetandsports.main.UrlFetcherThread;


public class CourtDetailFragment extends Fragment {

    public static final String ARG_ITEM_STRING = "item_name";

    private static class Handler1 extends Handler {

        private WeakReference<CourtDetailFragment> detailFragmentWeakReference;

        public Handler1(CourtDetailFragment courtDetailFragment) {
            detailFragmentWeakReference = new WeakReference<>(courtDetailFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            CourtDetailFragment courtDetailFragment = detailFragmentWeakReference.get();
            if (courtDetailFragment != null) {
                if (msg.what == UrlFetcherThread.MESSAGE_SUCCESS) {
                    try {
                        courtDetailFragment.setmItem(new JSONObject((String) msg.obj));

                        courtDetailFragment.initPages();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == UrlFetcherThread.MESSAGE_FAIL) {
                    Toast.makeText(courtDetailFragment.getContext(), "无法连接至服务器，请检查您的网络设置",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private static class PagerAdapter extends FragmentPagerAdapter {

        private List<OneDayPlaysFragment> fragmentList;

        public PagerAdapter(FragmentManager fm, List<OneDayPlaysFragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentList.get(position).getTitle();
        }
    }

    private JSONObject mItem;
    private Handler1 mHandler;

    public CourtDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler1(this);

        if (getArguments().containsKey(ARG_ITEM_STRING)) {
            try {
                mItem = new JSONObject(getArguments().getString(ARG_ITEM_STRING));
                String campusName = URLEncoder.encode(mItem.getString("campusName"), "utf-8");
                String name = URLEncoder.encode(mItem.getString("name"), "utf-8");
                new UrlFetcherThread(mHandler, MyApplication.URL_CAMPUSES_JSON +
                        campusName + "/courts/" + name).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.court_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PagerTabStrip tabStrip = (PagerTabStrip) getView().findViewById(R.id.one_day_play_pager_tab);
        tabStrip.setTabIndicatorColor(getResources().getColor(R.color.colorPrimary));
        tabStrip.setDrawFullUnderline(false);
    }

    public JSONObject getmItem() {
        return mItem;
    }

    public void setmItem(JSONObject mItem) {
        this.mItem = mItem;
    }

    public void initPages() {
        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("MM-dd EEEE");

        View rootView = getView();

        Date tmpDate = new Date (curDate.getTime() / (1000 * 3600 * 24) * (1000 * 3600 * 24) -
                TimeZone.getDefault().getRawOffset());
        List<OneDayPlaysFragment> fragmentList = new ArrayList<>();
        for (int i = 0; i < 7; i ++) {
            OneDayPlaysFragment fragment = new OneDayPlaysFragment();
            try {
                fragment.setArgs(dayOfWeekFormat.format(tmpDate), mItem.getString("campusName"),
                        mItem.getString("name"), tmpDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            tmpDate = new Date(tmpDate.getTime() + 24 * 60 * 60 * 1000);
            fragmentList.add(fragment);
        }

        if (mItem != null) {
            ViewPager viewPager = (ViewPager) rootView.findViewById(R.id.one_day_play_pager);
            viewPager.setAdapter(new PagerAdapter(getChildFragmentManager(), fragmentList));
        }

    }
}
