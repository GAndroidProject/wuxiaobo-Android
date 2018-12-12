package com.xiaoe.shop.wxb.business.audio.presenter;

import android.text.TextUtils;
import android.util.Log;
import com.google.gson.Gson;
import com.xiaoe.common.utils.SharedPreferencesUtil;

/**
 * Date: 2018/12/12 10:52
 * Author: hansyang
 * Description:
 */
@Deprecated
public class PlayerProgressRecordManager {

    static final String TAG = "PlayerProgressRecord";
    private static volatile PlayerProgressRecordManager mInstance;
    private ProgressBean mProgressBean;

    private PlayerProgressRecordManager(){
    }

    public static PlayerProgressRecordManager getInstance(){
        if (mInstance == null)
            synchronized (PlayerProgressRecordManager.class) {
                if (mInstance == null)
                    mInstance = new PlayerProgressRecordManager();
            }
        return mInstance;
    }

    public void saveLastOneProgress(String bigColumn,String column,String resId,int maxProgress,int progress){
        if (bigColumn == null)  bigColumn = "";
        if (column == null)  column = "";
        if (resId == null)  resId = "";
        if (progress < 0)   progress = 0;
        if (mProgressBean != null)   mProgressBean = null;

        ProgressBean progressBean = new ProgressBean(bigColumn,column,resId,maxProgress,progress);
        Gson gson = new Gson();
        String value = gson.toJson(progressBean);
        Log.d(TAG, "saveLastOneProgress: value = " + value);
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_PLAYER_PROGRESS_RECORD,value);
    }

    public void clearData(){
        mProgressBean = null;
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_PLAYER_PROGRESS_RECORD,"");
    }

    public ProgressBean getLastOneProgress(String bigColumn,String column,String resId){
        if (bigColumn == null)  bigColumn = "";
        if (column == null)  column = "";
        if (resId == null)  resId = "";
        if (mProgressBean == null) {
            String value = (String) SharedPreferencesUtil.getData(
                    SharedPreferencesUtil.KEY_PLAYER_PROGRESS_RECORD, "");

            Log.d(TAG, "getLastOneProgress: value = " + value);
            if (!TextUtils.isEmpty(value)) {
                Gson gson = new Gson();
                mProgressBean = gson.fromJson(value, ProgressBean.class);
            }
        }

        if (mProgressBean != null && mProgressBean.resId.equals(resId) && mProgressBean.bigColumn.equals(bigColumn)
                && mProgressBean.column.equals(column)){
            return mProgressBean;
        }else  return null;
    }

    public static class ProgressBean{
        public ProgressBean(String bigColumn,String column,String resId,int maxProgress,int progress){
            this.bigColumn = bigColumn;
            this.column = column;
            this.resId = resId;
            this.maxProgress = maxProgress;
            this.progress = progress;
        }
        public String bigColumn;
        public String column;
        public String resId;
        public int progress;
        public int maxProgress;
    }

}
