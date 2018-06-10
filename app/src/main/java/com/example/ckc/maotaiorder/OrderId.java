package com.example.ckc.maotaiorder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ckc on 18-5-29.
 */

public class OrderId {

    public List<Order> list=new ArrayList<>();

    public static class Order{
        public String orderId;
        public String orderInfo;
    }


    public HashMap<String, String> orderMap = new HashMap<>();

    public HashMap initOrder() {
        orderMap.put("391", "%7B%22Pid%22%3A391%2C%22PName%22%3A%22%E8%B4%B5%E5%B7%9E%E8%8C%85%E5%8F%B0%E9%85%92+(%E6%96%B0%E9%A3%9E%E5%A4%A9)+53%25vol+500ml%22%2C%22PCode%22%3A%2223%22%2C%22Unit%22%3A%22%E7%93%B6%22%2C%22CoverImage%22%3A%22%2Fupload%2FfileStore%2F20180415%2F6365942315164224808933821.jpg%22%2C%22SalePrice%22%3A1499%7D");
        orderMap.put("389", "%7B%22Pid%22%3A389%2C%22PName%22%3A%22%E8%B4%B5%E5%B7%9E%E8%8C%85%E5%8F%B0%E9%85%92+(%E4%BA%94%E6%98%9F)+53%25vol+500ml%22%2C%22PCode%22%3A%223%22%2C%22Unit%22%3A%22%E7%93%B6%22%2C%22CoverImage%22%3A%22%2Fupload%2FfileStore%2F20180415%2F6365942362964834856232180.jpg%22%2C%22SalePrice%22%3A1489%7D");
        return orderMap;
    }
}
