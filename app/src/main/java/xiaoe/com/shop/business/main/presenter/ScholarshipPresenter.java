package xiaoe.com.shop.business.main.presenter;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkEngine;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.ScholarshipBoughtListRequest;
import xiaoe.com.network.requests.ScholarshipRequest;
import xiaoe.com.network.requests.ScholarshipTaskListRequest;
import xiaoe.com.network.requests.ScholarshipTaskStateRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class ScholarshipPresenter implements IBizCallback {

    private static final String TAG = "ScholarshipPresenter";

    private INetworkResponse inr;

    public ScholarshipPresenter(INetworkResponse inr) {
        this.inr = inr;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
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

    // 获取任务列表
    public void requestTaskList() {
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
}
