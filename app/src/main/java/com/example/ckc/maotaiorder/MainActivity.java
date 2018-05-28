package com.example.ckc.maotaiorder;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    WebView webView;

    String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                CookieSyncManager.createInstance(MainActivity.this);
                CookieManager cookieManager = CookieManager.getInstance();
                String CookieStr = "";
                CookieStr = cookieManager.getCookie(url);
                cookie=CookieStr;
//                login();
                super.onPageFinished(view, url);
            }
        });

        webView.loadUrl("https://www.cmaotai.com/ysh5/page/LoginRegistr/userLogin.html");


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

        MyJsonObjectRequest request = new MyJsonObjectRequest(url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("onResponse==success", jsonObject.toString());
                isLogin();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("onResponse=onErrorResponse", volleyError.toString());
            }
        }, cookie) {
        };
        try {
            Log.i("MyJsonObject--login",request.getHeaders().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        queue.add(request);
    }

    private void isLogin() {
        String url = "https://www.cmaotai.com/YSApp_API/YSAppServer.ashx?";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("action", "UserManager.isLogin");
        paramsMap.put("timestamp121", new Date().getTime() + "");
        String params = appendParameter(url, paramsMap);

        MyJsonObjectRequest request = new MyJsonObjectRequest(url, params, new Response.Listener<JSONObject>() {
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
            Log.i("MyJsonObject--islogin",request.getHeaders().toString());
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

        MyJsonObjectRequest request = new MyJsonObjectRequest(url, params, new Response.Listener<JSONObject>() {
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

        try {
            Log.i("MyJsonObject--doOrder",request.getHeaders().toString());
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
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
