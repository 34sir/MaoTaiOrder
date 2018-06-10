package com.example.ckc.maotaiorder;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private HashMap<String, String> orders;
    private LinearLayout lin_orders;
    private LayoutInflater inflater;
    private View view;

    private String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "maotai/maotaiorder.txt";
    private String order_path = Environment.getExternalStorageDirectory().getPath() + File.separator + "maotai/maotaiorderid.txt";


    private View[] views = new View[5];

    private List<UserInfo.UserBean> list = new ArrayList<>();
    private List<OrderId.Order> orderIds = new ArrayList<>();


    private RadioButton box;

    private ListView listview;
    private OrderAdapter adapter;
    private OrderIdAdapter orderIdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initOrderId();

        etNo =  findViewById(R.id.et_no);
        etCount =findViewById(R.id.et_count);

        etNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPop();
            }
        });

        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                adapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.btn_load).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initView();
            }
        });
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDoOrder();
            }
        });

    }


    private void initData() {
        if (!new File(path).exists()) {
            return;
        }
        String json = FileUtil.getJsonFromFile(path);
        System.out.println("json=" + json);
        UserInfo userInfo = new Gson().fromJson(json, new TypeToken<UserInfo>() {
        }.getType());
        list = userInfo.list;
        for (int i = 0; i < list.size(); i++) {
            System.out.println("userInfo=" + list.get(i).account);
        }
    }

    private void initOrderId(){
        if (!new File(order_path).exists()) {
            return;
        }
        String json = FileUtil.getJsonFromFile(order_path);
        System.out.println("json=" + json);
        OrderId orderId = new Gson().fromJson(json, new TypeToken<OrderId>() {
        }.getType());
        orderIds = orderId.list;
    }

    EditText etCount;
    TextView etNo;

    String orderinfo="";

    private void initView() {
        initData();
        box = findViewById(R.id.checkbox);
        listview = findViewById(R.id.listview);
        adapter = new OrderAdapter(this, list);
        listview.setAdapter(adapter);
    }

    public void showPop(){
        orderIdAdapter=new OrderIdAdapter(this,orderIds);
        View popupView=LayoutInflater.from(this).inflate(R.layout.pop_list,null);
        ListView listView=popupView.findViewById(R.id.lv);
        listView.setAdapter(orderIdAdapter);

        final PopupWindow window = new PopupWindow(popupView, 400, 600);
        window.showAsDropDown(etNo);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                etNo.setText(orderIds.get(i).orderId);
                orderinfo=orderIds.get(i).orderInfo;
                window.dismiss();
            }
        });
        window.setOutsideTouchable(true);
    }

    int i = 0;
    CountDownTimer timer;
    public void startDoOrder() {
        int duration = 1000;
        i = 0;
        if(timer!=null){
            timer.cancel();
        }
        timer = new CountDownTimer(duration * (list.size()+1),duration) {

            @Override
            public void onTick(long millisUntilFinished) {
                //在计时器中轮询支付结果：每秒查询一次支付结果
                if (i < list.size()) {
                    login(i, "request" + i + new Date().getTime());
                    i++;
                }
            }

            @Override
            public void onFinish() {
                //倒数到0时的操作，一般认为倒数到0仍未收到支付结果，则认为支付失败，页面跳转
                timer.cancel();
            }
        };
        if (TextUtils.isEmpty(etNo.getText().toString().trim()) || TextUtils.isEmpty(etCount.getText().toString().trim())) {
            Toast.makeText(getApplicationContext(),"请填写编号和数量",Toast.LENGTH_LONG).show();
        }else {
            timer.start();
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

    /**
     * 登陆
     *
     * @param index
     */
    private synchronized void login(final int index, final String requestId) {
        Log.i("login","login--index="+index);
        String tel = list.get(index).account;
        String pwd = list.get(index).password;

        if (TextUtils.isEmpty(tel) || TextUtils.isEmpty(pwd)) {
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
        MyJsonObjectRequest request = null;
        request = new MyJsonObjectRequest(MainActivity.this, url, Request.Method.POST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("onResponse==success===login", jsonObject.toString());
                if (jsonObject.has("code")) {
                    try {
                        if (((int) jsonObject.get("code")) == 0) {
                            getAddress(index, requestId);
                            list.get(index).isLoginSucess = true;
                        } else {
                            if (!list.get(index).isSuccess) {
                                list.get(index).isSuccess = false;
                                list.get(index).isLoginSucess = false;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (!list.get(index).isSuccess) {
                            list.get(index).isSuccess = false;
                            list.get(index).isLoginSucess = false;
                        }
                    }
                } else {
                    if (!list.get(index).isSuccess) {
                        list.get(index).isSuccess = false;
                        list.get(index).isLoginSucess = false;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!list.get(index).isSuccess) {
                    list.get(index).isSuccess = false;
                    list.get(index).isLoginSucess = false;
                }
                adapter.notifyDataSetChanged();
            }
        }).setIsLogin(true).setRequestID(requestId);
        queue.add(request);
    }

    /**
     * 获取地址
     */
    public void getAddress(final int index, final String requestId) {
        String url = "https://www.cmaotai.com//YSApp_API/YSAppServer.ashx?";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("action", "AddressManager.list");
        paramsMap.put("index", 1 + "");
        paramsMap.put("size", 5 + "");
        paramsMap.put("timestamp121", new Date().getTime() + "");
        String params = appendParameter(url, paramsMap);

        MyJsonObjectRequest request = new MyJsonObjectRequest(MainActivity.this, url, Request.Method.POST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i("onResponse==success===getAddress", jsonObject.toString());
                if (jsonObject.has("code")) {
                    try {
                        if (((int) jsonObject.get("code")) == 0) {
                            JSONArray list = jsonObject.getJSONObject("data").getJSONArray("list");
                            doOrder(index, requestId, list.getJSONObject(0).get("SId") + "");
                        } else {
                            if (!list.get(index).isSuccess) {
                                list.get(index).isSuccess = false;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (!list.get(index).isSuccess) {
                            list.get(index).isSuccess = false;
                        }
                    }
                } else {
                    if (!list.get(index).isSuccess) {
                        list.get(index).isSuccess = false;
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (!list.get(index).isSuccess) {
                    list.get(index).isSuccess = false;
                }
            }
        }).setIsLogin(false).setRequestID(requestId);
        queue.add(request);
    }

    /**
     * 下单
     *
     * @param index
     */
    public void doOrder(final int index, String requestId, String sid) {
        if (TextUtils.isEmpty(sid) || TextUtils.isEmpty(etCount.getText().toString())) {
            list.get(index).isSuccess = false;
            adapter.notifyDataSetChanged();
            return;
        }
        String url = "https://www.cmaotai.com/API/Servers.ashx?";
        RequestQueue queue = Volley.newRequestQueue(this);
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("sid", sid);
        paramsMap.put("iid", "-1");
        paramsMap.put("qty", etCount.getText().toString());
        paramsMap.put("express", "14");
            paramsMap.put("product",orderinfo);
            try {
                list.get(index).bussinessInfo = new JSONObject(URLDecoder.decode(orderinfo)).getString("PName");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        paramsMap.put("remark", "");
        paramsMap.put("action", "GrabSingleManager.submit");
        paramsMap.put("timestamp121", new Date().getTime() + "");
        String params = appendParameter(url, paramsMap);

        MyJsonObjectRequest request = new MyJsonObjectRequest(MainActivity.this, url, Request.Method.POST, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.e("onResponse==success===doOrder", jsonObject.toString());
                if (jsonObject.has("code")) {
                    try {
                        int code=(int) jsonObject.get("code");
                        if (code == 0) {
                            list.get(index).isSuccess = true;
                        } else {
                            if(code==20){
                                list.get(index).isOrdered=true;
                            }
                            list.get(index).isSuccess = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (!list.get(index).isSuccess) {
                            list.get(index).isSuccess = false;
                        }
                    }
                } else {
                    if (!list.get(index).isSuccess) {
                        list.get(index).isSuccess = false;
                    }
                }
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("onResponse", volleyError.toString());
                if (!list.get(index).isSuccess) {
                    list.get(index).isSuccess = false;
                }
                adapter.notifyDataSetChanged();
            }
        }).setIsLogin(false).setRequestID(requestId);
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

    public static class OrderAdapter extends BaseAdapter {
        private List<UserInfo.UserBean> list = new ArrayList<>();
        private Context context;
        private LayoutInflater inflater;

        public OrderAdapter(Context context, List<UserInfo.UserBean> list) {
            this.list = list;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View contentView = inflater.inflate(R.layout.item_order, null);
            ((TextView) contentView.findViewById(R.id.tv_account)).setText(list.get(i).account);
            ((TextView) contentView.findViewById(R.id.tv_info)).setText(list.get(i).bussinessInfo);
            ((TextView) contentView.findViewById(R.id.tv_psw)).setText(list.get(i).password);
            if(list.get(i).isLoginSucess){
                if(list.get(i).isOrdered==true){
                    ((TextView) contentView.findViewById(R.id.tv_result)).setText("状态:" + "无购买资格");
                }else {
                    ((TextView) contentView.findViewById(R.id.tv_result)).setText("状态:" + (list.get(i).isSuccess ? "预约成功" : "预约失败"));
                }
            }else {
                if(!TextUtils.isEmpty(list.get(i).bussinessInfo)){
                    ((TextView) contentView.findViewById(R.id.tv_result)).setText("状态:" + (list.get(i).isSuccess ? "登录成功" : "登录失败"));
                }else {
                    ((TextView) contentView.findViewById(R.id.tv_result)).setText("状态:");
                }
            }
            return contentView;
        }
    }

    public static class OrderIdAdapter extends BaseAdapter{
        private List<OrderId.Order> list = new ArrayList<>();
        private Context context;
        private LayoutInflater inflater;

        public OrderIdAdapter(Context context, List<OrderId.Order> list) {
            this.list = list;
            this.context = context;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View contentView=inflater.inflate(R.layout.orderid_item,null);
            ((TextView)contentView.findViewById(R.id.tv_order_id)).setText(list.get(i).orderId);
            return contentView ;
        }
    }

}
