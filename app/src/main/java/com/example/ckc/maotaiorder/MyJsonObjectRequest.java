package com.example.ckc.maotaiorder;

import android.webkit.CookieManager;

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

/**
 * Created by ckc on 2018/5/27.
 */

public class MyJsonObjectRequest extends JsonRequest<JSONObject> {

    String stringRequest;

    public String cookieFromResponse;
    private String mHeader;
    private String cookie;

    /**
     * 这里的method必须是Method.POST，也就是必须带参数。
     * 如果不想带参数，可以用JsonObjectRequest，给它构造参数传null。GET方式请求。
     *
     * @param stringRequest 格式应该是 "key1=value1&key2=value2"
     */

    public MyJsonObjectRequest(String url, String stringRequest,
                               Response.Listener<JSONObject> listener, Response.ErrorListener errorListener,String cookie) {
        super(Method.POST, url, stringRequest, listener, errorListener);
        this.stringRequest = stringRequest;
        this.cookie=cookie;
    }

    @Override
    public String getBodyContentType() {
        return "application/x-www-form-urlencoded; charset=" + getParamsEncoding();
    }

//    @Override
//    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
//        try {
//            Log.w("LOG","parseNetworkResponse"+response.toString());
//            String jsonString = new String(response.data,
//                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
//
//            mHeader = response.headers.toString();
//            Log.w("LOG","get headers in parseNetworkResponse "+response.headers.toString());
//            //使用正则表达式从reponse的头中提取cookie内容的子串
//            Pattern pattern= Pattern.compile("Set-Cookie.*?;");
//            Matcher m=pattern.matcher(mHeader);
//            if(m.find()){
//                cookieFromResponse =m.group();
//                Log.w("LOG","cookie from server "+ cookieFromResponse);
//            }
//            //去掉cookie末尾的分号
//            cookieFromResponse = cookieFromResponse.substring(11,cookieFromResponse.length()-1);
//            Log.w("LOG","cookie substring "+ cookieFromResponse);
//            //将cookie字符串添加到jsonObject中，该jsonObject会被deliverResponse递交，调用请求时则能在onResponse中得到
//            JSONObject jsonObject = new JSONObject(jsonString);
//            jsonObject.put("cookie","acw_tc=AQAAAHZRxmvGLw4A3r3dci0r/dLsn0V1; ASP.NET_SessionId=efl0oznnn3j3wkxgipqw2r2d; Hm_lvt_8b83f87d060d93d589c9a2c17dde6656=1527404139; Hm_lpvt_8b83f87d060d93d589c9a2c17dde6656=1527410070; SERVERID=73717107c680adffd3c04c75b05bf592|1527410074|1527404138");
//            Log.w("LOG","jsonObject "+ jsonObject.toString());
//
//            return Response.success(new JSONObject(jsonString),
//                    HttpHeaderParser.parseCacheHeaders(response));
//        } catch (UnsupportedEncodingException e) {
//            return Response.error(new ParseError(e));
//        } catch (JSONException je) {
//            return Response.error(new ParseError(je));
//        }
//    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headerMap = new HashMap<String, String>();
        headerMap.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.0.2; MI 2 Build/LRX22G; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/57.0.2987.132 Mobile Safari/537.36[android/1.0.23/fd5a2d67b896af6a2603845f43c51072/40498f60941693187be98a1c7dd8c148]");
        String cookie = CookieManager.getInstance().getCookie("www.cmaotai.com");
        headerMap.put("Cookie", cookie);
        if(headerMap != null) {
            return headerMap;
        }
        return super.getHeaders();
    }



    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

}
