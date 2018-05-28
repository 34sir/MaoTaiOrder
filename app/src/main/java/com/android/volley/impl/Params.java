package com.android.volley.impl;

import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Params {
	protected LinkedHashMap<String, String> ht = new LinkedHashMap<String, String>();
	protected String rtp = "";

    public Params(){

    }

	public Params(String rtp) {
		ht.put("rtp", rtp);
	}

	public void addParam(String key, String value) {
		ht.put(key, value);
	}

	public String getParam(String key) {
		return ht.get(key);
	}

	public LinkedHashMap<String, String> getRequestParams() {
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
				for (Map.Entry<String, String> entry : ht.entrySet()) {
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
