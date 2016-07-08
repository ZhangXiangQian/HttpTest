package com.bankeys.zxq.httputils.controller;


/**
 * Created by Administrator on 2016/1/13.
 */
public class Controller {

    /**
     * 异步线程
     *
     * @param <K>
     */
    public static class BaseTaskParams<K>

    {
        public Interface.OnDoInBackground<K> mOnDoInBackground;
        public Interface.OnResultExecute<K> mOnResultExecute;
        public boolean isCancel = true;
        public boolean isShowDialog = false;
        public String msg;
        public String title;
        public Interface.OnErrorResult mOnErrorResult;

        public void setmOnErrorResult(Interface.OnErrorResult mOnErrorResult) {
            this.mOnErrorResult = mOnErrorResult;
        }


        public void setTitle(String title) {
            this.title = title;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }


        public void setmOnDoInBackground(Interface.OnDoInBackground<K> mOnDoInBackground) {
            this.mOnDoInBackground = mOnDoInBackground;
        }

        public void setmOnResultExecute(Interface.OnResultExecute<K> mOnResultExecute) {
            this.mOnResultExecute = mOnResultExecute;
        }

        public void setShowDialog(boolean showDialog) {
            isShowDialog = showDialog;
        }

        public void setCancel(boolean cancel) {
            isCancel = cancel;
        }
    }

    public static class AdapterParams<R>{
        public int page = 0;
        public boolean isLoadMore;
        public boolean isRefresh;
        public boolean isShowDialog;

        public void setLoadMore(boolean loadMore) {
            isLoadMore = loadMore;
        }

        public void setRefresh(boolean refresh) {
            isRefresh = refresh;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public void setShowDialog(boolean showDialog) {
            isShowDialog = showDialog;
        }
    }

}
