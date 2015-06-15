package com.freelancer.whatsbeef.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.freelancer.whatsbeef.R;
import com.freelancer.whatsbeef.beans.Response;
import com.freelancer.whatsbeef.beans.Programs;
import com.freelancer.whatsbeef.dao.DataRetriever;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final int WEB_SERVICE_ITEM_COUNT = 10;
    private List<Programs> programList;
    private ListView lvMain;
    private int currentItemCount = 0;
    private int responseCount = 0;
    private DisplayDataTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        programList = new ArrayList<Programs>();
        lvMain = (ListView) findViewById(R.id.lv_main);
        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                //DO NOTHING
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int position = firstVisibleItem + visibleItemCount;
                int limit = totalItemCount;
                boolean scrollable = totalItemCount > visibleItemCount;
                if (position >= limit && totalItemCount > 0 && scrollable && currentItemCount < responseCount) {
                    if (mTask.getStatus() != AsyncTask.Status.RUNNING) {
                        mTask = new DisplayDataTask();
                        mTask.execute();
                    }
                }
            }
        });
        mTask = new DisplayDataTask();
        mTask.execute();

    }

    private class DisplayDataTask extends AsyncTask<Void, Void, Void> {
        private String errorMessage;
        private Response response;
        private String offlineData;
        private boolean isOffline = false;

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences pref = getSharedPreferences("WHATSBEEF", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            if (!isNetworkConnected()) {
                isOffline = true;
                offlineData = pref.getString("DATA_CACHE", null);
                if (offlineData == null) {
                    errorMessage = "There is a problem connecting to the server. Please check your connection then reopen the app.";
                    return null;
                } else {
                    Type type = new TypeToken<List<Programs>>() {
                    }.getType();
                    List<Programs> list = gson.fromJson(offlineData, type);
                    programList = list;
                }

            } else {
                try {
                    response = DataRetriever.getPrograms(currentItemCount);
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(Void unused) {
            SharedPreferences pref = getSharedPreferences("WHATSBEEF", Context.MODE_PRIVATE);
            Gson gson = new Gson();

            if (errorMessage != null) {
                showErrorMessage(errorMessage);
            } else if (isOffline && programList != null) {
                lvMain.setAdapter(new CustomAdapter());
            } else if (response != null) {

                responseCount = response.getCount();
                String resultsToString = gson.toJson(response.getResults());
                Type type = new TypeToken<List<Programs>>() {
                }.getType();
                List<Programs> list = gson.fromJson(resultsToString, type);



                if (currentItemCount == 0) {
                    currentItemCount += list.size();
                    programList = list;
                    lvMain.setAdapter(new CustomAdapter());
                } else if (list != null) {
                    currentItemCount += list.size();
                    programList.addAll(list);
                    ((CustomAdapter) lvMain.getAdapter()).notifyDataSetChanged();
                }

                offlineData = gson.toJson(programList);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("DATA_CACHE", offlineData);
                editor.commit();
            }
        }

    }

    private void showErrorMessage(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setIcon(0);
        builder.setTitle("Error");
        builder.setMessage(errorMessage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    private class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return programList.size();
        }

        @Override
        public Object getItem(int i) {
            return programList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                convertView = inflater.inflate(R.layout.list_item_main, null);

                holder = new ViewHolder();
                holder.tvName = (TextView) convertView.findViewById(R.id.main_tv_program_name);
                holder.startTime = (TextView) convertView.findViewById(R.id.main_tv_starttime);
                holder.endTime = (TextView) convertView.findViewById(R.id.main_tv_endtime);
                holder.channel = (TextView) convertView.findViewById(R.id.main_tv_channel);
                holder.rating = (TextView) convertView.findViewById(R.id.main_tv_rating);

                convertView.setTag(holder);
            }

            Programs program = programList.get(position);

            holder = (ViewHolder) convertView.getTag();
            holder.tvName.setText(program.getName());
            holder.startTime.setText(program.getStart_time());
            holder.endTime.setText(program.getEnd_time());
            holder.channel.setText(program.getChannel());
            holder.rating.setText(program.getRating());
            return convertView;
        }

        private class ViewHolder {
            public TextView tvName;
            public TextView startTime;
            public TextView endTime;
            public TextView channel;
            public TextView rating;
        }
    }

}
