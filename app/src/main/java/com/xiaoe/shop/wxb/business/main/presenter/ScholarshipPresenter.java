package com.xiaoe.shop.wxb.business.main.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.entitys.ScholarshipEntity;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.NetworkEngine;
import com.xiaoe.network.network_interface.IBizCallback;
import com.xiaoe.network.network_interface.INetworkResponse;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.ScholarshipBoughtListRequest;
import com.xiaoe.network.requests.ScholarshipReceiveRequest;
import com.xiaoe.network.requests.ScholarshipRequest;
import com.xiaoe.network.requests.ScholarshipSubmitRequest;
import com.xiaoe.network.requests.ScholarshipTaskListRequest;
import com.xiaoe.network.requests.ScholarshipTaskStateRequest;

import java.util.HashMap;

public class ScholarshipPresenter implements IBizCallback {

    private static final String TAG = "ScholarshipPresenter";

    private INetworkResponse inr;
    private boolean needInitData; // 是否需要初始化信息

    // 详情页创建此工具时需要初始化
    private String resourceId;
    private String resourceType;
    private boolean isSuperVip;
    // 不继续请求奖学金页面的数据
    private boolean stopGoOn;

    public ScholarshipPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    public ScholarshipPresenter(INetworkResponse inr, boolean stopGoOn) {
        this.stopGoOn = stopGoOn;
        this.inr = inr;
    }

    public ScholarshipPresenter(INetworkResponse inr, String resourceId, String resourceType, boolean isSuperVip) {
        this.inr = inr;
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.isSuperVip = isSuperVip;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {

        if (iRequest instanceof ScholarshipTaskListRequest) { // 接收任务列表的请求
            JSONObject result = (JSONObject) entity;
            JSONArray data = (JSONArray) result.get("data");
            if (stopGoOn) {
                if (data.size() == 0) { // 兼容没有任务的情况
                    inr.onResponse(iRequest, false, entity);
                } else {
                    inr.onResponse(iRequest, true, entity);
                }
            } else {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    getTaskId2Request(data, iRequest);
                } else {
                    // 请求失败，直接回调 false 结果给调用者
                    inr.onResponse(iRequest, false, null);
                }
            }
        } else {
            inr.onResponse(iRequest, success, entity);
        }
    }

    // 获取任务列表
    public void requestTaskList(boolean needInitData) {
        this.needInitData = needInitData;
        ScholarshipTaskListRequest scholarshipTaskListRequest = new ScholarshipTaskListRequest(this);
        scholarshipTaskListRequest.sendRequest();
    }

    // 获取排行榜
    public void requestRange(String taskId) {
        ScholarshipRequest scholarshipRequest = new ScholarshipRequest(this);

        scholarshipRequest.addRequestParam("task_id", taskId);
        scholarshipRequest.setNeedCache(true);
        scholarshipRequest.setCacheKey(taskId+"_ranking");
        NetworkEngine.getInstance().sendRequest(scholarshipRequest);
    }

    // 获取任务状态
    public void requestTaskStatues(String taskId) {
        ScholarshipTaskStateRequest scholarshipTaskStateRequest = new ScholarshipTaskStateRequest(this);

        scholarshipTaskStateRequest.addRequestParam("task_id", taskId);

        scholarshipTaskStateRequest.setNeedCache(true);
        scholarshipTaskStateRequest.setCacheKey(taskId+"_state");

        NetworkEngine.getInstance().sendRequest(scholarshipTaskStateRequest);
    }

    // 获取已购列表，目前只需要音频
    public void requestBoughtList() {
        ScholarshipBoughtListRequest scholarshipBoughtListRequest = new ScholarshipBoughtListRequest(this);

        NetworkEngine.getInstance().sendRequest(scholarshipBoughtListRequest);
    }

    // 请求提交任务
    public void requestSubmitTask(String taskId, String resourceId, String resourceType, boolean isSuperVip) {
        ScholarshipSubmitRequest scholarshipSubmitRequest = new ScholarshipSubmitRequest(this);

        scholarshipSubmitRequest.addRequestParam("task_id", taskId);
        scholarshipSubmitRequest.addRequestParam("resource_id", resourceId);
        scholarshipSubmitRequest.addRequestParam("resource_type", resourceType);
        HashMap<String, Integer> node = new HashMap<>();
        node.put("is_share", 1);
        node.put("is_pay", 1);
        scholarshipSubmitRequest.addRequestParam("nodes", node);
        int isMember = isSuperVip ? 1 : 0;
        scholarshipSubmitRequest.addRequestParam("is_members", isMember);

        NetworkEngine.getInstance().sendRequest(scholarshipSubmitRequest);
    }

    // 查询领取结果
    public void queryReceiveResult(String taskId, String taskDetailId) {
        ScholarshipReceiveRequest scholarshipReceiveRequest = new ScholarshipReceiveRequest(this);

        scholarshipReceiveRequest.addRequestParam("task_id", taskId);
        scholarshipReceiveRequest.addRequestParam("task_detail_id", taskDetailId);

        NetworkEngine.getInstance().sendRequest(scholarshipReceiveRequest);
    }

    // 获取 taskId
    private void getTaskId2Request(JSONArray data, IRequest iRequest) {
        if (data.size() == 0) {
            inr.onResponse(iRequest, false, null);
            return;
        }
        for (Object item : data) {
            JSONObject itemJson = (JSONObject) item;
            int taskType = itemJson.getInteger("task_type") == null ? 0 : itemJson.getInteger("task_type");
            if (taskType != 1) { // 不是奖学金的话就下一个
                continue;
            }
            String taskId = itemJson.getString("task_id");
            int taskTotalMoney = itemJson.getInteger("reward_amount") == null ? 0 : itemJson.getInteger("reward_amount");
            String totalMoney = String.format("%.0f", taskTotalMoney / 100f);
            NetworkEngine.getInstance().saveCacheData(taskId+"_money", totalMoney, null);
            SharedPreferencesUtil.getInstance(XiaoeApplication.getmContext(), SharedPreferencesUtil.FILE_NAME);
            SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_SCHOLARSHIP_ID, taskId);
            ScholarshipEntity.getInstance().setTaskId(taskId);
            ScholarshipEntity.getInstance().setTaskTotalMoney(totalMoney);
            if (needInitData) { // 需要初始化信息（奖学金去瓜分那个页面）
                requestRange(taskId);
                requestTaskStatues(taskId);
            } else { // 不需要初始化信息（音频详情页不需要知道排行榜，和初始的状态）
                if (TextUtils.isEmpty(resourceId) || TextUtils.isEmpty(resourceType)) {
                    return;
                }
                requestSubmitTask(taskId, resourceId, resourceType, isSuperVip);
            }
            break; // 拿到奖学金任务 id 后退出循环
        }
    }
}
