package com.android.volley.impl;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SocketParams 具有JSONObject拓展的Params
 * Http 中传入的 Value 始终为 String，没法满足 TCP 中 Value 需要为 JSONObject 的情况
 * 故作此拓展
 * author:张冠之
 * time: 2018/1/12 上午11:30
 * e-mail: guanzhi.zhang@sojex.cn
 */

public class SocketParams{
    protected LinkedHashMap<String, Object> ht = new LinkedHashMap<String, Object>();
    protected String rtp = "";

    public SocketParams(){

    }

    public SocketParams(String rtp) {
        ht.put("rtp", rtp);
    }

    public void addParam(String key, Object value) {
        ht.put(key, value);
    }

    public Object getParam(String key) {
        return ht.get(key);
    }

    public LinkedHashMap<String, Object> getRequestParams() {
//		 String s=getRequestParamsString();
//	        GLog.e("url", s);
        return ht;
    }

    public void remove(String key)
    {
        ht.remove(key);
    }

    @Override
    public String toString() {
        String s=getRequestParamsString();
//        GLog.e("url",s);
        return s;
    }

    public String getRequestParamsString() {
        StringBuilder param = new StringBuilder();
        if (ht != null && ht.size() > 0) {
            // param += "?";
            Iterator ite = ht.entrySet().iterator();
            while (ite.hasNext()) {
                Map.Entry<String,String> entry = (Map.Entry) ite.next();
                String key = entry.getKey();
                String value = entry.getValue();
                param.append( key + "=" + value + "&");
            }
            param.deleteCharAt(param.length()-1);
        }
        return param.toString();
    }

    /**
     * 将 params 转化为 JSONObject 再转化为 String
     * @return 返回 String
     */
    public String getRequestParamsJSON() {
        String params;
        JSONObject object = new JSONObject();
        try {
            if (ht != null && ht.size() > 0) {
                for (Map.Entry<String, Object> entry : ht.entrySet()) {
                    if (!TextUtils.equals("rtp",entry.getKey())) {
                        object.put(entry.getKey(), entry.getValue());
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //加密
        params = object.toString();
        return params;
    }
}
