package com.qiwoo.inception;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.qiwoo.inception.canvas.util.FileHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ListActivity {
    private ListView listView = null;
    JSONArray appList = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String str = FileHelper.loadFromAssetsFile(this, "app/Conf.json");
        try{
            JSONObject appConf = new JSONObject(str);
            appList = appConf.getJSONArray("list");
        }catch (Exception e){
            e.printStackTrace();
        }

        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(int i=0;i<appList.length();i++){
            Map<String, Object> item = new HashMap<String, Object>();
            try {
                JSONObject appInfo = appList.getJSONObject(i);
                item.put("title", appInfo.getString("title"));
                item.put("description", appInfo.getString("description"));
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

        listView = getListView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent newIntent = new Intent();
                String appName = null;
                try {
                    appName = appList.getJSONObject(i).getString("app");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //activity = Class.forName("org.qiwoo.inception.examples."+appName);
                newIntent.setClass(MainActivity.this, AppActivity.class);
                newIntent.putExtra("appName", appName);
                startActivity(newIntent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
