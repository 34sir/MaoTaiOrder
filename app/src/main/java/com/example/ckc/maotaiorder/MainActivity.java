package com.example.ckc.maotaiorder;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview);
        listView.setAdapter(new MyAdapter(this));
        login();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void login() {
        String url = "https://www.cmaotai.com/API/Servers.ashx?";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("action", "UserManager.login");
        paramsMap.put("tel", "13145016210");
        paramsMap.put("pwd", "123456");
        paramsMap.put("timestamp121", new Date().getTime() + "");
        String params = appendParameter(url, paramsMap);

        MyJsonObjectRequest request = new MyJsonObjectRequest(MainActivity.this, url, Request.Method.POST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("onResponse==success", jsonObject.toString());
                doOrder();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("onResponse=onErrorResponse", volleyError.toString());
            }
        }, cookie) {
        };
        try {
            Log.i("MyJsonObject--login", request.getHeaders().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        queue.add(request);
    }


    public void doOrder() {
        String url = "https://www.cmaotai.com/API/Servers.ashx?";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("sid", "2353140");
        paramsMap.put("iid", "-1");
        paramsMap.put("qty", "1");
        paramsMap.put("express", "14");
        paramsMap.put("product", "%7B%22Pid%22%3A391%2C%22PName%22%3A%22%E8%B4%B5%E5%B7%9E%E8%8C%85%E5%8F%B0%E9%85%92+(%E6%96%B0%E9%A3%9E%E5%A4%A9)+53%25vol+500ml%22%2C%22PCode%22%3A%2223%22%2C%22Unit%22%3A%22%E7%93%B6%22%2C%22CoverImage%22%3A%22%2Fupload%2FfileStore%2F20180415%2F6365942315164224808933821.jpg%22%2C%22SalePrice%22%3A1499%7D");
        paramsMap.put("remark", "");
        paramsMap.put("action", "GrabSingleManager.submit");
        paramsMap.put("timestamp121", new Date().getTime() + "");
        String params = appendParameter(url, paramsMap);

        MyJsonObjectRequest request = new MyJsonObjectRequest(MainActivity.this, url, Request.Method.POST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("onResponse==success", jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("onResponse=onErrorResponse", volleyError.toString());
            }
        }, cookie) {
        };
        queue.add(request);
    }


    private String appendParameter(String url, Map<String, String> params) {
        Uri uri = Uri.parse(url);
        Uri.Builder builder = uri.buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().getQuery();
    }


    private class MyAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;

        public MyAdapter(Context context) {
            this.context = context;
            inflater=LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return inflater.inflate(R.layout.item_order,null);
        }
    }
}
