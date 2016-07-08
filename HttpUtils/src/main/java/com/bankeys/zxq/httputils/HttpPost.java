package com.bankeys.zxq.httputils;

import android.util.Log;

import com.bankeys.zxq.httputils.Exception.GeneralException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;


/**
 * Post访问
 * Created by Administrator on 2016/2/2.
 */
public class HttpPost {
    public static String post(String path, Map<String, String> map) throws GeneralException {
        HttpURLConnection conn = null;
        URL url;
        InputStream is = null;
        ByteArrayOutputStream out = null;
        String result = null;
        byte[] b;
        try {
            url = new URL(path);
            b = mapToByte2(map);
            out = new ByteArrayOutputStream();
            conn = (HttpURLConnection) url.openConnection();
            conn.setFixedLengthStreamingMode(b.length);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //conn.setRequestProperty("content-type", "application/json");
            conn.setRequestProperty("content-length", String.valueOf(b.length));
            conn.setRequestProperty("connection", "keep-alive");
            conn.getOutputStream().write(mapToByte2(map));
            int code = conn.getResponseCode();
            Log.e("-------", "path>>>" + path + "\n" + "code>>>" + code);
            if (code == 200) {
                b = new byte[1024];
                is = conn.getInputStream();
                int len;
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                result = out.toString();
                Log.i("HttpsPost", "result:" + result);
            } else {
                is = conn.getErrorStream();
                int len;
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                result = out.toString();
                throw new GeneralException(code, result);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new GeneralException(404, "发生未知错误");
        } catch (IOException e) {

            e.printStackTrace();
            throw new GeneralException(404, "发生未知错误");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static byte[] mapToByte(Map<String, String> map) {
        Iterator<String> it = map.keySet().iterator();
        JSONObject jo = new JSONObject();
        try {
            while (it.hasNext()) {
                String key = it.next();
                String value = map.get(key);
                jo.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String result = jo.toString();
        byte[] b = result.getBytes();
        Log.i("httpPost.mapToByte", "size:" + b.length + "\n" + "result>>" + result);
        return b;
    }
    private static byte[] mapToByte2(Map<String, String> map) {
        StringBuffer paramer = new StringBuffer();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = map.get(key);
            Log.e("KEY-VALUE","key : "+key +" , value = " +value);
            paramer.append(key + "=" + value + "&");

        }
        String result = paramer.toString();
        result = result.substring(0, result.length() - 1);
        Log.e("httpPost.mapToByte", "result>>" + result);
        return result.getBytes();
    }
}
