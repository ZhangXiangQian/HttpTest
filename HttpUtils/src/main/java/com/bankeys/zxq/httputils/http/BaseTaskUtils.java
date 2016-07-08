package com.bankeys.zxq.httputils.http;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.bankeys.zxq.httputils.Exception.GeneralException;
import com.bankeys.zxq.httputils.controller.Controller;
import com.bankeys.zxq.httputils.controller.Interface;


/**
 * Created by Administrator on 2016/1/8.
 * 异步线程
 *
 * @author ZhangXiangQian
 */
public class BaseTaskUtils<T> extends AsyncTask<Void, Void, T>

{
    private ProgressDialog mDialog;
    private Controller.BaseTaskParams<T> taskParams;
    private Activity context;
    private String errMessage;

    private void setTaskParams(final Activity context, Controller.BaseTaskParams<T> taskParams) {
        this.taskParams = taskParams;
        this.context = context;
        mDialog = new ProgressDialog(context);
        mDialog.setTitle(taskParams.title);
        mDialog.setMessage(taskParams.msg);
        mDialog.setCancelable(taskParams.isCancel);
    }


    @Override
    protected void onPostExecute(T rtn) {
        super.onPostExecute(rtn);
        if (mDialog != null && !context.isFinishing() && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if(null != errMessage && !"".equals(errMessage)){
            if (taskParams.mOnErrorResult != null) {
                taskParams.mOnErrorResult.onErrorResult(new GeneralException(404,errMessage));
            } else {
                Toast.makeText(context,errMessage,Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (taskParams.mOnResultExecute != null) {
            try {
                taskParams.mOnResultExecute.onResultExecute(rtn);
            } catch (GeneralException e) {
                e.printStackTrace();
                if (taskParams.mOnErrorResult != null) {
                    taskParams.mOnErrorResult.onErrorResult(e);
                } else {
                    Toast.makeText(context, e.getMessage(),Toast.LENGTH_SHORT).show();
                }
                taskParams.mOnResultExecute = null;
            }
        }

    }

    @Override
    protected T doInBackground(Void... params) {
        if (!isNetConnected(context)) {
            errMessage = "网络连接已断开";
            return null;
        }
        try {
            return taskParams.mOnDoInBackground.onDoInBackground();
        } catch (GeneralException e) {
            e.printStackTrace();
            errMessage = e.getMessage();
            return null;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mDialog != null && !context.isFinishing() && taskParams.isShowDialog) {
            mDialog.show();
        }
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    private   boolean isNetConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        return (mNetworkInfo != null && mNetworkInfo.isConnected());
    }

    public static class Builder<F> {
        private Controller.BaseTaskParams<F> P;
        private Activity context;

        public Builder(Activity context) {
            this.context = context;
            P = new Controller.BaseTaskParams<F>();
        }

        public Builder setTitle(String title) {
            P.setTitle(title);
            return this;
        }

        public Builder setTitle(int resId) {
            return setTitle(context.getResources().getString(resId));
        }

        public Builder setMessage(String msg) {
            P.setMsg(msg);
            return this;
        }

        public Builder setOnErrorResult(Interface.OnErrorResult mOnErrorResult) {
            P.setmOnErrorResult(mOnErrorResult);
            return this;
        }

        public Builder setMessage(int msgId) {
            return setMessage(context.getResources().getString(msgId));
        }

        public Builder setOnDoInBackground(Interface.OnDoInBackground<F> mOnDoInBackground) {
            P.setmOnDoInBackground(mOnDoInBackground);
            return this;
        }

        public Builder setOnResultExecute(Interface.OnResultExecute<F> mOnResultExecute) {
            P.setmOnResultExecute(mOnResultExecute);
            return this;
        }

        public Builder setIsCancel(boolean isCancel) {
            P.setCancel(isCancel);
            return this;
        }

        public Builder setIsShowDialog(boolean isShowDialog) {
            P.setShowDialog(isShowDialog);
            return this;
        }

        public void execute() {
            final BaseTaskUtils<F> task = new BaseTaskUtils<F>();
            task.setTaskParams(context, P);
            task.execute();
        }
    }
}
