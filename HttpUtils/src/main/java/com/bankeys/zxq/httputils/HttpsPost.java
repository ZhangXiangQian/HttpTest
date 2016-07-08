package com.bankeys.zxq.httputils;

import android.util.Log;


import com.bankeys.zxq.httputils.Exception.GeneralException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Posts访问
 * Created by Administrator on 2016/2/2.
 */
public class HttpsPost {
    public static String post(String path, Map<String, String> map) throws GeneralException{
        Log.e("-------", "path>>>" + path);
        HttpsURLConnection conn = null;
        URL url;
        InputStream is = null;
        ByteArrayOutputStream out = null;
        String result = null;
        byte[] b;
        try {
            //配置验证策略  这里不验证后台
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{new SelfX509TrustManager()}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new SelfHostnameVerifier());

            url = new URL(path);
            b = mapToByte(map);
            out = new ByteArrayOutputStream();
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setConnectTimeout(60 * 1000);
            conn.setReadTimeout(60 * 1000);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //  conn.setRequestProperty("content-length", String.valueOf(b.length));
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("connection", "keep-alive");
            //   conn.setRequestProperty("content-type", "application/json");
            OutputStream ous = conn.getOutputStream();
            ous.write(b);
            ous.flush();
            ous.close();
            int code = conn.getResponseCode();
            Log.e("-------", "code>>>" + code);
            if (code == 200) {
                is = conn.getInputStream();
                int len;
                b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                result = out.toString();
                Log.e("-------", "https.result>>>" + result);
            } else {
                //如果后台返回ErrorStream则处理，否则 则不处理
                is = conn.getErrorStream();
                int len;
                b = new byte[1024];
                while ((len = is.read(b)) != -1) {
                    out.write(b, 0, len);
                }
                result = out.toString();
                if (result != null && !"".equals(result)) {
                    throw new GeneralException(code, result);
                } else {
                    throw new GeneralException(code, "发生未知错误");
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new GeneralException(404, "发生未知错误");
        } catch (IOException e) {
            e.printStackTrace();
            throw new GeneralException(404, "发生未知错误");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new GeneralException(404, "发生未知错误");
        } catch (KeyManagementException e) {
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
        StringBuffer paramer = new StringBuffer();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            String value = map.get(key);
            paramer.append(key + "=" + value + "&");
        }
        String result = paramer.toString();
        result = result.substring(0, result.length() - 1);
        Log.e("httpPost.mapToByte", "result>>" + result);
        return result.getBytes();
    }

    private static class SelfHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * 信任证书管理类
     */
    private static class SelfX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
