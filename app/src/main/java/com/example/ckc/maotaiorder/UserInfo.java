package com.example.ckc.maotaiorder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ckc on 2018/6/3.
 */

public class UserInfo {
    public List<UserBean> list=new ArrayList<>();
    public static class UserBean{
        public String account="";
        public String password="";
        public String bussinessInfo="";
        public boolean isSuccess=false;
        public boolean isLoginSucess=false;
        public boolean isOrdered=false;
    }
}
