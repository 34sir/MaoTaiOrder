package com.example.ckc.maotaiorder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by ckc on 2018/6/3.
 */

public class FileUtil {

    public static String getJsonFromFile(String path){
        String json="";
        InputStreamReader isr = null;
        try {
            isr = new InputStreamReader(new FileInputStream(path));
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while((line = br.readLine()) != null){
                builder.append(line);
            }
            br.close();
            isr.close();

            json=builder.toString();
        } catch (Exception e) {
            System.out.println("Exception="+e.getMessage());
            e.printStackTrace();
        }

        return json;
    }
}
