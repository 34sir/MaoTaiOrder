package com.example.ckc.maotaiorder;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    String cookie;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private HashMap<String, String> orders;
    private LinearLayout lin_orders;
    private LayoutInflater inflater;
    private View view;


    private View[] views = new View[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orders = new Order().initOrder();
        setContentView(R.layout.activity_main);
        initView();

        findViewById(R.id.tv_order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDoOrder();
            }
        });
        findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearInfo();
            }
        });
    }


    private void initView() {
        lin_orders = findViewById(R.id.lin_orders);
        inflater = LayoutInflater.from(this);
        for (int i = 0; i < views.length; i++) {
            view = inflater.inflate(R.layout.item_order, null);
            lin_orders.addView(view);
            views[i] = view;
        }
    }


    public void startDoOrder() {
        for (int i = 0; i < views.length; i++) {
            login(i);
        }
    }

    public void clearInfo(){
        for (int i = 0; i < views.length; i++) {
            ((EditText)views[i].findViewById(R.id.et_info)).setText("");
            ((EditText)views[i].findViewById(R.id.et_account)).setText("");
            ((EditText)views[i].findViewById(R.id.et_psw)).setText("");
            ((TextView)views[i].findViewById(R.id.tv_result)).setText("状态:");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    public void clearCookie() {
        preferences = this.getSharedPreferences("cookie_data", MODE_PRIVATE);
        editor = preferences.edit();
        editor.remove("Set-Cookie");
        editor.commit();
    }

    private void login(final int index) {
        clearCookie();

        String tel=((EditText) views[index].findViewById(R.id.et_account)).getText().toString().trim();
        String pwd=((EditText) views[index].findViewById(R.id.et_psw)).getText().toString().trim();

        if(TextUtils.isEmpty(tel)||TextUtils.isEmpty(pwd)){
            return;
        }

        String url = "https://www.cmaotai.com/API/Servers.ashx?";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("action", "UserManager.login");
        paramsMap.put("tel", tel);
        paramsMap.put("pwd", pwd);
        paramsMap.put("timestamp121", new Date().getTime() + "");
        String params = appendParameter(url, paramsMap);

        MyJsonObjectRequest request = new MyJsonObjectRequest(MainActivity.this, url, Request.Method.POST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("onResponse==success", jsonObject.toString());
                if (jsonObject.has("code")) {
                    try {
                        if (((int)jsonObject.get("code"))==0) {
                            doOrder(index);
                        }else {
                            ((TextView) views[index].findViewById(R.id.tv_result)).setText("状态:失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ((TextView) views[index].findViewById(R.id.tv_result)).setText("状态:失败");

                    }
                }else {
                    ((TextView) views[index].findViewById(R.id.tv_result)).setText("状态:失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                ((TextView) views[index].findViewById(R.id.tv_result)).setText("状态:失败");
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


    public void doOrder(final int index) {
        String sid = ((EditText) views[index].findViewById(R.id.et_info)).getText().toString().trim();
        String qty=((EditText) views[index].findViewById(R.id.et_count)).getText().toString().trim();

        if(TextUtils.isEmpty(sid)||TextUtils.isEmpty(qty)){
            return;
        }

        String url = "https://www.cmaotai.com/API/Servers.ashx?";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("sid", "2353140");
        paramsMap.put("iid", "-1");
        paramsMap.put("qty", qty);
        paramsMap.put("express", "14");
        if (orders.containsKey(sid)) {
            paramsMap.put("product", orders.get(sid));
        } else {
            Toast.makeText(MainActivity.this, "无此商品，请联系开发者添加此商品", Toast.LENGTH_LONG).show();
            return;
        }
        paramsMap.put("remark", "");
        paramsMap.put("action", "GrabSingleManager.submit");
        paramsMap.put("timestamp121", new Date().getTime() + "");
        String params = appendParameter(url, paramsMap);

        MyJsonObjectRequest request = new MyJsonObjectRequest(MainActivity.this, url, Request.Method.POST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("onResponse==success", jsonObject.toString());
                if (jsonObject.has("code")) {
                    try {
                        if ((int)jsonObject.get("code")==0) {
                            ((TextView) views[index].findViewById(R.id.tv_result)).setText("状态:成功");
                        }else {
                            ((TextView) views[index].findViewById(R.id.tv_result)).setText("状态:失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        ((TextView) views[index].findViewById(R.id.tv_result)).setText("状态:失败");

                    }
                }else {
                    ((TextView) views[index].findViewById(R.id.tv_result)).setText("状态:失败");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("onResponse=onErrorResponse", volleyError.toString());
                ((TextView) views[index].findViewById(R.id.tv_result)).setText("状态:失败");

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

}
