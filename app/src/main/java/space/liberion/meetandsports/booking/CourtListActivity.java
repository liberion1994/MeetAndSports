package space.liberion.meetandsports.booking;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;

import space.liberion.meetandsports.R;
import space.liberion.meetandsports.main.MyApplication;
import space.liberion.meetandsports.main.UrlFetcherThread;

import static android.support.v4.app.NavUtils.navigateUpFromSameTask;


public class CourtListActivity extends AppCompatActivity {

    private static class Handler1 extends Handler {
        private WeakReference<CourtListActivity> targetActivityReference;

        public Handler1(CourtListActivity targetActivity) {
            this.targetActivityReference = new WeakReference<>(targetActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            CourtListActivity targetActivity = targetActivityReference.get();
            if (targetActivity != null) {
                if (msg.what == UrlFetcherThread.MESSAGE_SUCCESS) {
                    try {
                        targetActivity.initView(new JSONArray((String) msg.obj));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (msg.what == UrlFetcherThread.MESSAGE_FAIL) {
                    Toast.makeText(targetActivity, "无法连接至服务器，请检查您的网络设置",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean mTwoPane;

    private Handler1 mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_list);

        Intent intent = getIntent();
        String campusName = intent.getStringExtra("campusName");
        String sportsType = intent.getStringExtra("sportsType");

        mHandler = new Handler1(this);
        try {
            String url = MyApplication.URL_CAMPUSES_JSON + URLEncoder.encode(campusName,  "utf-8")
                    + "/courts?sportsType=" + URLEncoder.encode(sportsType,  "utf-8");
            new UrlFetcherThread(mHandler, url).start();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView titleText = (TextView) findViewById(R.id.title_text);
        titleText.setText(campusName + " " + sportsType);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.court_detail_container) != null) {
            mTwoPane = true;
        }
    }

    protected void initView(JSONArray courts) {
        View recyclerView = findViewById(R.id.court_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView, courts);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, JSONArray courts) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(courts));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final JSONArray mValues;

        public SimpleItemRecyclerViewAdapter(JSONArray items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.court_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            try {
                holder.mItem = mValues.getJSONObject(position);
                holder.mIdView.setText(holder.mItem.getString("name"));
                holder.mContentView.setText(holder.mItem.getString("addressInCampus"));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(CourtDetailFragment.ARG_ITEM_STRING,
                                    holder.mItem.toString());
                            CourtDetailFragment fragment = new CourtDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.court_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, CourtDetailActivity.class);
                            intent.putExtra(CourtDetailFragment.ARG_ITEM_STRING,
                                    holder.mItem.toString());

                            context.startActivity(intent);
                        }
                    }
                });
            } catch (Exception e) {

            }
        }

        @Override
        public int getItemCount() {
            return mValues.length();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public JSONObject mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
