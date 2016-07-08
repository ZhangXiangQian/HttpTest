package com.bankeys.zxq.httputils;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/1/22.
 */
public class HttpGet {

    /**
     * httpGet请求
     *
     * @param path
     * @return
     */
    public static String get(String path) {
        Log.i(">>>HttpGet<<<", path);
        HttpURLConnection connect = null;
        URL url;
        ByteArrayOutputStream outputStream = null;
        InputStream is = null;
        try {
            url = new URL(path);
            outputStream = new ByteArrayOutputStream();
            connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("GET");
            connect.setConnectTimeout(20 * 1000);
            connect.setReadTimeout(10 * 1000);
            if (connect.getResponseCode() == HttpURLConnection.HTTP_OK) {
                byte[] b = new byte[1024];
                is = connect.getInputStream();
                int len = 0;
                while (is.read(b) != -1) {
                    outputStream.write(b, 0, len);
                }
                String jsonString = new String(b);
                return jsonString;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connect != null) {
                connect.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}
