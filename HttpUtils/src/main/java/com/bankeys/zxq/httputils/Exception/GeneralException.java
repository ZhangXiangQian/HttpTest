package com.bankeys.zxq.httputils.Exception;

/**
 * Created by Administrator on 2016/2/1.
 */
public class GeneralException extends Exception {
    private int code;
    private String message;

    public GeneralException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
