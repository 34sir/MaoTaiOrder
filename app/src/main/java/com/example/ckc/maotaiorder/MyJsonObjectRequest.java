package com.example.ckc.maotaiorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ckc on 2018/5/27.
 */

public class MyJsonObjectRequest extends JsonRequest<JSONObject> {

    String stringRequest;

    public String cookieFromResponse;
    private String mHeader;

    private SharedPreferences preferences;
    private Context context;
    private SharedPreferences.Editor editor;

    private boolean isLogin;
    private String requestId;

    private HashMap<String, String> map = new HashMap<>();

    private MyJsonObjectRequest request;

    /**
     * 这里的method必须是Method.POST，也就是必须带参数。
     * 如果不想带参数，可以用JsonObjectRequest，给它构造参数传null。GET方式请求。
     *
     * @param stringRequest 格式应该是 "key1=value1&key2=value2"
     */

    public MyJsonObjectRequest(Context context, String url, int method, String stringRequest,
                               Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, stringRequest, listener, errorListener);
        this.stringRequest = stringRequest;
        this.context = context;

        preferences = context.getSharedPreferences("cookie_data", MODE_PRIVATE);
        editor = preferences.edit();
    }

    public MyJsonObjectRequest setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
        return this;
    }

    public MyJsonObjectRequest setRequestID(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public MyJsonObjectRequest setRequest(MyJsonObjectRequest request) {
        this.request = request;
        return this;
    }

    public String cookie;


    public String getCookie() {
        return cookie;
    }


    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            Log.w("LOG", "parseNetworkResponse" + jsonString);
            mHeader = response.headers.toString();
            Log.w("LOG", "get headers in parseNetworkResponse " + response.headers.toString());
            //使用正则表达式从reponse的头中提取cookie内容的子串
            Pattern pattern = Pattern.compile("Set-Cookie.*?,");
            Matcher m = pattern.matcher(mHeader);
            if (m.find()) {
                cookieFromResponse = m.group();
                cookieFromResponse = cookieFromResponse.replace("Set-Cookie=", "").replace(",", "");
                if (isLogin&&cookieFromResponse.contains("acw_tc") && cookieFromResponse.contains("ASP.NET_SessionId") && cookieFromResponse.contains("SERVERID") && cookieFromResponse.contains("Vshop-Member")) {
                    editor.putString(requestId, cookieFromResponse);
                    editor.apply();
                }
                Log.w("LOG", "cookie from server " + cookieFromResponse);
            }
//            //去掉cookie末尾的分号
//            cookieFromResponse = cookieFromResponse.substring(11,cookieFromResponse.length()-1);
//            Log.w("LOG","cookie substring "+ cookieFromResponse);
            //将cookie字符串添加到jsonObject中，该jsonObject会被deliverResponse递交，调用请求时则能在onResponse中得到
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject.put("Cookie", cookieFromResponse);
            Log.w("LOG", "jsonObject " + jsonObject.toString());

            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.0.2; MI 2 Build/LRX22G; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 Mobile Safari/537.36[android/1.0.23/fd5a2d67b896af6a2603845f43c51072/40498f60941693187be98a1c7dd8c148]");

        String Cookie = preferences.getString(requestId, "");
        if (!isLogin && !TextUtils.isEmpty(Cookie)) {
            headerMap.put("Cookie", Cookie);
            System.out.println("getHeadersCookie=" + Cookie + "-----requestid=" + requestId);
        }
        if (headerMap != null) {
            return headerMap;
        }
        return super.getHeaders();
    }


//    @Override
//    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//        try {
//            String jsonString = new String(response.data,
//                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
//            return Response.success(new JSONObject(jsonString),
//                    HttpHeaderParser.parseCacheHeaders(response));
//        } catch (UnsupportedEncodingException e) {
//            return Response.error(new ParseError(e));
//        } catch (JSONException je) {
//            return Response.error(new ParseError(je));
//        }
//    }

}
