package com.bankeys.zxq.httputils;

import android.app.Activity;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by zhang on 2016/7/8.
 */
public class HttpTask {
    private HttpParams P;
    public static final String GET = "get";
    public static final String HTTP = "http";
    public static final String HTTPS = "https";

    private HttpTask(HttpParams P) {
        this.P = P;
    }

    private void executor() {
        HttpURLConnection conn;
        InputStream is;
        ByteArrayOutputStream bos;
        URL url;
        try {
            url = new URL(P.getUrl());
            bos = new ByteArrayOutputStream();
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(P.getHttpType());
            conn.setReadTimeout((int) P.getReadTimeout());
            conn.setConnectTimeout((int) P.getRequestTimeout());
            //添加头文件数据
            if (P.getRequestProperty() != null && P.getRequestProperty().size() != 0) {
                Iterator<String> it = P.getRequestProperty().keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    String value = P.getRequestProperty().get(key);
                    conn.setRequestProperty(key, value);
                }
            }
            int code = conn.getResponseCode();
            byte[] b = new byte[1024];
            if (HttpURLConnection.HTTP_OK == code) {
                is = conn.getInputStream();
                int len;
                while ((len = is.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
                String result = bos.toString();
                if (P.getCallback() != null) {
                    P.getCallback().onResultSuccess(result);
                }
            } else {
                is = conn.getErrorStream();
                int len;
                while ((len = is.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
                if (P.getCallback() != null) {
                    P.getCallback().onResultFailure(code, bos.toString());
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (P.getCallback() != null) {
                P.getCallback().onResultFailure(400, "错误请求");
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (P.getCallback() != null) {
                P.getCallback().onResultFailure(400, "IO流异常");
            }
        }
    }


    public static class Builder {
        private HttpParams P;
        private Activity context;
        private Map<String, String> map;
        private Map<String,String> data;

        public Builder(Activity context) {
            this.context = context;
            map = new HashMap<>();
            data = new HashMap<>();
            P.setRequestProperty(map);
            P.setRequestData(data);
        }

        public Builder addRequestProperty(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Builder addReuqestData(String key,String value){
            data.put(key,value);
            return this;
        }
        public Builder setHttpType(String type) {
            P.setHttpType(type);
            return this;
        }

        public Builder setCallback(HttpResult callback) {
            P.setCallback(callback);
            return this;
        }

        public Builder setRequestTimeout(long requestTimeout) {
            P.setRequestTimeout(requestTimeout);
            return this;
        }

        public Builder setReadTimeout(long readTimeout) {
            P.setReadTimeout(readTimeout);
            return this;
        }

        public Builder setUrl(String url) {
            P.setUrl(url);
            return this;
        }

        public void request() {
            new HttpTask(P).executor();
        }
    }

    private static class HttpParams {
        private String httpType;
        private Map<String, String> RequestProperty;
        private Map<String, String> RequestData;
        private long requestTimeout;
        private long readTimeout;
        private String url;
        private HttpResult callback;

        public void setHttpType(String httpType) {
            this.httpType = httpType;
        }

        public String getHttpType() {
            return httpType;
        }

        public Map<String, String> getRequestProperty() {
            return RequestProperty;
        }

        public void setRequestProperty(Map<String, String> requestProperty) {
            RequestProperty = requestProperty;
        }

        public long getRequestTimeout() {
            return requestTimeout;
        }

        public void setRequestTimeout(long requestTimeout) {
            this.requestTimeout = requestTimeout;
        }

        public long getReadTimeout() {
            return readTimeout;
        }

        public void setReadTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Map<String, String> getRequestData() {
            return RequestData;
        }

        public void setRequestData(Map<String, String> requestData) {
            RequestData = requestData;
        }

        public HttpResult getCallback() {
            return callback;
        }

        public void setCallback(HttpResult callback) {
            this.callback = callback;
        }
    }

    public interface HttpResult {
        public void onResultSuccess(String s);

        public void onResultFailure(int code, String msg);
    }

}
