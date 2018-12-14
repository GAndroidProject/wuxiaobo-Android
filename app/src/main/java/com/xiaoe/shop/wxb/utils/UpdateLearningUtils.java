package com.xiaoe.shop.wxb.utils;

import android.content.Context;
import android.util.Log;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.db.LrSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.LearningRecord;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.UpdateMineLearningRequest;

import java.util.List;

public class UpdateLearningUtils implements IBizCallback {

    private INetworkResponse inr;

    public UpdateLearningUtils(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(IRequest iRequest, boolean success, Object entity) {
        inr.onResponse(iRequest, success, entity);
    }

    // 更新学习记录
    public void updateLearningProgress(String resourceId, int resourceType, int progress) {
        UpdateMineLearningRequest updateMineLearningRequest = new UpdateMineLearningRequest(this);

        updateMineLearningRequest.addDataParam("resource_id", resourceId);
        updateMineLearningRequest.addDataParam("resource_type", resourceType);
        updateMineLearningRequest.addDataParam("learn_progress", progress);
        // 原始学习进度，app 没有进度，所以传 0，从头开始
        updateMineLearningRequest.addDataParam("org_learn_progress", "0");
        updateMineLearningRequest.addDataParam("spend_time", 0);

        NetworkEngine.getInstance().sendRequest(updateMineLearningRequest);
    }

    /**
     * 将学习记录保存到本地
     * @param context
     * @param lr
     */
    public static void saveLr2Local(Context context, LearningRecord lr) {
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(context, new LrSQLiteCallback());
        if (!sqLiteUtil.tabIsExist(LrSQLiteCallback.TABLE_NAME_LR)) {
            sqLiteUtil.execSQL(LrSQLiteCallback.TABLE_SCHEMA_LR);
        }
        List<LearningRecord> lrList = sqLiteUtil.query(LrSQLiteCallback.TABLE_NAME_LR, "select * from " + LrSQLiteCallback.TABLE_NAME_LR, null);
        Log.d("setUserVisibleHint", "saveLr2Local: --- " + lrList.size());
        if (lrList.size() > 0) {
            sqLiteUtil.execSQL("delete from " + LrSQLiteCallback.TABLE_NAME_LR);
        }
        sqLiteUtil.insert(LrSQLiteCallback.TABLE_NAME_LR, lr);
    }
}
