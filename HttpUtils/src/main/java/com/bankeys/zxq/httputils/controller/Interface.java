package com.bankeys.zxq.httputils.controller;

import android.view.View;

import com.bankeys.zxq.httputils.Exception.GeneralException;

/**
 * Created by Administrator on 2016/1/13.
 */
public class Interface {

    public interface OnDoInBackground<F> {
        public F onDoInBackground() throws GeneralException;
    }

    public interface OnResultExecute<K> {
        public void onResultExecute(K t) throws GeneralException;
    }

    public interface OnErrorResult {
        public void onErrorResult(GeneralException e);
    }

    public interface PositiveDialogClick{
        public void onPositiveDialogClick(String txt, View.OnClickListener mOnClickListener);
    }

    public interface NegatvieDialogClick{
        public void onNegatvieDialogClick(String txt, View.OnClickListener mOnClickListener);
    }
    public interface GetResultOk{
        public void onResult_Ok(int sub_activity_id);
    }
}
