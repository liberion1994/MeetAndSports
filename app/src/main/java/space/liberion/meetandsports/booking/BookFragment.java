package space.liberion.meetandsports.booking;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import space.liberion.meetandsports.R;
import space.liberion.meetandsports.main.BaseFragment;
import space.liberion.meetandsports.main.MyApplication;
import space.liberion.meetandsports.main.UrlFetcherThread;
import space.liberion.meetandsports.widgets.NestedGridView;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookFragment extends BaseFragment {


    private static class Handler1 extends Handler {

        private WeakReference<BookFragment> bookFragmentWeakReference;

        public Handler1(BookFragment bookFragment) {
            this.bookFragmentWeakReference = new WeakReference<>(bookFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            BookFragment bookFragment = bookFragmentWeakReference.get();
            if (bookFragment != null) {
                if (msg.what == UrlFetcherThread.MESSAGE_FAIL) {
                    Toast.makeText(bookFragment.getContext(), "无法连接至服务器，请检查您的网络设置",
                            Toast.LENGTH_SHORT).show();
                    bookFragment.needFresh = true;
                } else if (msg.what == UrlFetcherThread.MESSAGE_SUCCESS) {
                    try {
                        bookFragment.listViewAdapter = new ListViewAdapter(bookFragment.getContext(),
                                new JSONArray((String) msg.obj));
                        bookFragment.initListView();
                        bookFragment.needFresh = false;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static class ListViewAdapter extends BaseAdapter {

        private static class GridViewAdapter extends BaseAdapter {

            private Context context;
            private String campusName;
            private JSONArray sportsTypes;

            private class ViewHolder {
                public TextView typeView;
                public TextView countView;
                public TextView bookView;
            }

            public GridViewAdapter(Context context, String campusName, JSONArray sportsTypes) {
                this.context = context;
                this.campusName = campusName;
                this.sportsTypes = sportsTypes;
            }

            @Override
            public int getCount() {
                return sportsTypes.length();
            }

            @Override
            public Object getItem(int position) {
                try {
                    return sportsTypes.get(position);
                } catch (JSONException e) {
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
                    convertView = LayoutInflater.from(context).inflate(
                            R.layout.item_item_list_view_book_fragment_types, null);

                    viewHolder = new ViewHolder();
                    viewHolder.typeView = (TextView) convertView.findViewById(
                            R.id.item_item_list_view_book_fragment_types_type);
                    viewHolder.countView = (TextView) convertView.findViewById(
                            R.id.item_item_list_view_book_fragment_types_count);
                    viewHolder.bookView = (TextView) convertView.findViewById(
                            R.id.item_item_list_view_book_fragment_types_book);

                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                try {
                    final JSONObject sportsType = sportsTypes.getJSONObject(position);
                    viewHolder.typeView.setText(sportsType.getString("sportsType") + "场");
                    viewHolder.countView.setText(sportsType.getString("count") + "座");
                    viewHolder.bookView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent(context, CourtListActivity.class);
                                intent.putExtra("campusName", campusName);
                                intent.putExtra("sportsType", sportsType.getString("sportsType"));
                                context.startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return convertView;
            }

            @Override
            public boolean areAllItemsEnabled() {
                return false;
            }

            @Override
            public boolean isEnabled(int position) {
                return false;
            }
        }

        private class ViewHolder {
            public TextView nameView;
            public NestedGridView typesView;
            public TextView descriptionView;
        }

        private Context context;
        private JSONArray campuses;

        public ListViewAdapter(Context context, JSONArray campuses) {
            this.context = context;
            this.campuses = campuses;
        }

        @Override
        public int getCount() {
            return campuses.length();
        }

        @Override
        public Object getItem(int position) {
            try {
                return campuses.get(position);
            } catch (JSONException e) {
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_ist_view_book_fragment, null);

                viewHolder = new ViewHolder();
                viewHolder.nameView = (TextView) convertView.findViewById(R.id.item_list_view_book_fragment_name);
                viewHolder.typesView = (NestedGridView) convertView.findViewById(R.id.item_list_view_book_fragment_types);
                viewHolder.descriptionView = (TextView) convertView.findViewById(R.id.item_list_view_book_fragment_description);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            try {
                JSONObject campus = (JSONObject) campuses.get(position);

                viewHolder.nameView.setText(campus.getString("name"));
                viewHolder.typesView.setAdapter(new GridViewAdapter(context, campus.getString("name"), campus.getJSONArray("sportsTypes")));
                viewHolder.descriptionView.setText(campus.getString("description"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
    private ListView listView;
    private Handler1 mHandler;
    private ListViewAdapter listViewAdapter;

    private boolean needFresh = true;

    public BookFragment() {
        // Required empty public constructor
        mHandler = new Handler1(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView = (ListView) getView().findViewById(R.id.list_view_book_fragment);
        assert listView != null;
        if (needFresh) {
            needFresh = false;
            String url = MyApplication.URL_CAMPUSES_JSON;
            new UrlFetcherThread(mHandler, url).start();
        } else {
            initListView();
        }

    }

    private void initListView() {
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
