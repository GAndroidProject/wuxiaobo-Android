package xiaoe.com.shop.business.main.presenter;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.entitys.ScholarshipEntity;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.ScholarshipBoughtListRequest;
import xiaoe.com.network.requests.ScholarshipReceiveRequest;
import xiaoe.com.network.requests.ScholarshipRequest;
import xiaoe.com.network.requests.ScholarshipSubmitRequest;
import xiaoe.com.network.requests.ScholarshipTaskListRequest;
import xiaoe.com.network.requests.ScholarshipTaskStateRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class ScholarshipPresenter implements IBizCallback {

    private static final String TAG = "ScholarshipPresenter";

    private INetworkResponse inr;
    private boolean needInitData; // 是否需要初始化信息

    // 详情页创建此工具时需要初始化
    private String resourceId;
    private String resourceType;
    private boolean isSuperVip;

    public ScholarshipPresenter(INetworkResponse inr) {
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
        JSONObject data = (JSONObject) entity;
        if (iRequest instanceof ScholarshipTaskListRequest) { // 接收任务列表的请求
            int code = data.getInteger("code");
            if (code == NetworkCodes.CODE_SUCCEED) {
                JSONArray result = (JSONArray) data.get("data");
                getTaskId2Request(result);
            } else {
                Log.d(TAG, "onMainThreadResponse: request fail, params error maybe...");
            }
        } else {
            ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (success && entity != null) {
                        inr.onMainThreadResponse(iRequest, true, entity);
                    } else {
                        inr.onMainThreadResponse(iRequest, false, entity);
                    }
                }
            });
        }
    }

    // 获取任务列表
    public void requestTaskList(boolean needInitData) {
        this.needInitData = needInitData;
        ScholarshipTaskListRequest scholarshipTaskListRequest = new ScholarshipTaskListRequest(NetworkEngine.SCHOLARSHIP_BASE_URL + "get_task_list", this);

        scholarshipTaskListRequest.addRequestParam("app_id", CommonUserInfo.getShopId());

        NetworkEngine.getInstance().sendRequest(scholarshipTaskListRequest);
    }

    // 获取排行榜
    public void requestRange(String taskId) {
        ScholarshipRequest scholarshipRequest = new ScholarshipRequest(NetworkEngine.SCHOLARSHIP_BASE_URL + "get_reward_ranking_list", this);

        scholarshipRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        scholarshipRequest.addRequestParam("task_id", taskId);

        NetworkEngine.getInstance().sendRequest(scholarshipRequest);
    }

    // 获取任务状态
    public void requestTaskStatues(String taskId) {
        ScholarshipTaskStateRequest scholarshipTaskStateRequest = new ScholarshipTaskStateRequest(NetworkEngine.SCHOLARSHIP_BASE_URL + "get_task_status", this);

        scholarshipTaskStateRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        scholarshipTaskStateRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
        scholarshipTaskStateRequest.addRequestParam("task_id", taskId);

        NetworkEngine.getInstance().sendRequest(scholarshipTaskStateRequest);
    }

    // 获取已购列表，目前只需要音频
    public void requestBoughtList() {
        ScholarshipBoughtListRequest scholarshipBoughtListRequest = new ScholarshipBoughtListRequest(NetworkEngine.SCHOLARSHIP_BASE_URL + "get_share_list", this);

        scholarshipBoughtListRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        scholarshipBoughtListRequest.addRequestParam("user_id", CommonUserInfo.getUserId());

        NetworkEngine.getInstance().sendRequest(scholarshipBoughtListRequest);
    }

    // 请求提交任务
    public void requestSubmitTask(String taskId, String resourceId, String resourceType, boolean isSuperVip) {
        ScholarshipSubmitRequest scholarshipSubmitRequest = new ScholarshipSubmitRequest(NetworkEngine.SCHOLARSHIP_BASE_URL + "submit_task", this);

        scholarshipSubmitRequest.addRequestParam("app_id", CommonUserInfo.getShopId());
        scholarshipSubmitRequest.addRequestParam("user_id", CommonUserInfo.getUserId());
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
        ScholarshipReceiveRequest scholarshipReceiveRequest = new ScholarshipReceiveRequest(NetworkEngine.SCHOLARSHIP_BASE_URL + "get_task_result", this);

        scholarshipReceiveRequest.addRequestParam("task_id", taskId);
        scholarshipReceiveRequest.addRequestParam("task_detail_id", taskDetailId);
        scholarshipReceiveRequest.addRequestParam("user_id", CommonUserInfo.getUserId());

        NetworkEngine.getInstance().sendRequest(scholarshipReceiveRequest);
    }

    // 获取 taskId
    private void getTaskId2Request(JSONArray data) {
        for (Object item : data) {
            JSONObject itemJson = (JSONObject) item;
            int taskType = itemJson.getInteger("task_type") == null ? 0 : itemJson.getInteger("task_type");
            if (taskType != 1) { // 不是奖学金的话就下一个
                continue;
            }
            String taskId = itemJson.getString("task_id");
            ScholarshipEntity.getInstance().setTaskId(taskId);
            if (needInitData) { // 需要初始化信息（奖学金去瓜分那个页面）
                requestRange(taskId);
                requestTaskStatues(taskId);
            } else { // 不需要初始化信息（音频详情页不需要知道排行榜，和初始的状态）
                requestSubmitTask(taskId, resourceId, resourceType, isSuperVip);
            }
            break; // 拿到奖学金任务 id 后退出循环
        }
    }
}
