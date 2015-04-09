package org.qiwoo.inception;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qiwoo.inception.util.FileHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ListActivity {
    private ListView listView = null;
    JSONArray activityList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String str = FileHelper.readFile(this, "Conf/activity.json");

        try{
            JSONObject activityConf = new JSONObject(str);
            activityList = activityConf.getJSONArray("list");
        }catch (Exception e){
            e.printStackTrace();
        }

        listView = getListView();
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i=0;i<activityList.length();i++){
            Map<String, Object> item = new HashMap<String, Object>();
            try {
                JSONObject activityInfo = activityList.getJSONObject(i);
                item.put("title", activityInfo.getString("title"));
                item.put("description", activityInfo.getString("description"));
                list.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, list,
                android.R.layout.simple_list_item_2,
                new String[]{"title", "description"},
                new int[]{android.R.id.text1, android.R.id.text2});
        setListAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent newIntent = new Intent();
                String activityName = null;
                Class activity;
                try {
                    activityName = activityList.getJSONObject(i).getString("activity");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    activity = Class.forName("org.qiwoo.inception.examples."+activityName);
                    newIntent.setClass(MainActivity.this, activity);
                    startActivity(newIntent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}