package com.xiaoe.shop.wxb.business.bought_list.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.xiaoe.common.entitys.BoughtListItem;
import com.xiaoe.common.entitys.HadSharedEvent;
import com.xiaoe.common.entitys.TaskDetailIdEvent;
import com.xiaoe.common.interfaces.OnItemClickWithBoughtItemListener;
import com.xiaoe.common.utils.MeasureUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.ScholarshipBoughtListRequest;
import com.xiaoe.network.requests.ScholarshipSubmitRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.bought_list.presenter.BoughtListAdapter;
import com.xiaoe.shop.wxb.business.main.presenter.ScholarshipPresenter;

public class BoughtListActivity extends XiaoeActivity implements OnItemClickWithBoughtItemListener {

    private static final String TAG = "BoughtListActivity";

    @BindView(R.id.bought_list_refresh)
    SmartRefreshLayout boughtRefresh;
    @BindView(R.id.bought_list_back)
    ImageView boughtListBack;
    @BindView(R.id.bought_list_content)
    ListView boughtListContent;

    Unbinder unbinder;

    List<BoughtListItem> dataList;
    ScholarshipPresenter scholarshipPresenter;

    Intent intent;
    String taskId;
    String resourceId;
    String resourceType;
    boolean isSuperVip;
    BoughtListAdapter boughtListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bought_lsit);
        unbinder = ButterKnife.bind(this);
        intent = getIntent();
        taskId = intent.getStringExtra("taskId");
        isSuperVip = intent.getBooleanExtra("isSuperVip", false);
        EventBus.getDefault().register(this);
        dataList = new ArrayList<>();
        scholarshipPresenter = new ScholarshipPresenter(this);
        scholarshipPresenter.requestBoughtList();

        initListener();
    }

    private void initListener() {
        boughtListBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        boughtRefresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (scholarshipPresenter == null) {
                    scholarshipPresenter = new ScholarshipPresenter(BoughtListActivity.this);
                }
                if (dataList.size() != 0 && boughtListAdapter!=null) { // 有数据时刷新发送请求
                    dataList.clear();
                    boughtListAdapter.notifyDataSetChanged();
                    scholarshipPresenter.requestBoughtList();
                }
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof ScholarshipBoughtListRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    try {
                        JSONArray data = ((JSONArray) ((JSONObject) result.get("data")).get("audio"));
                        initPageData(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        boughtRefresh.finishRefresh();
                    }
                } else {
                    Log.d(TAG, "onMainThreadResponse: request fail...");
                }
            } else if (iRequest instanceof ScholarshipSubmitRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    String taskDetailId = data.getString("task_detail_id");
                    EventBus.getDefault().post(new TaskDetailIdEvent(taskDetailId));
                    finish();
                } else {
                    Log.d(TAG, "onMainThreadResponse: 提交奖学金任务失败");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail, param error maybe");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Subscribe
    public void obtainHadSharedEvent(HadSharedEvent hadSharedEvent) {
        if (hadSharedEvent != null && hadSharedEvent.hadShared) {
            // 已经分享了
            if (scholarshipPresenter == null) {
                scholarshipPresenter = new ScholarshipPresenter(this);
            }
            Log.d(TAG, "obtainHadSharedEvent: taskId --- " + taskId + " --- resourceId --- " + resourceId + " --- resourceType --- " + resourceType);
            scholarshipPresenter.requestSubmitTask(taskId, resourceId, resourceType, isSuperVip);
        }
    }

    // 初始化页面数据
    private void initPageData(JSONArray data) {
        if (dataList.size() != 0) {
            dataList.clear();
        }
        for (Object item : data) {
            JSONObject itemJson = (JSONObject) item;
            String resourceId = itemJson.getString("resource_id");
            String resourceType = itemJson.getInteger("resource_type") == null ? "" : String.valueOf(itemJson.getInteger("resource_type"));
            String imgUrl = itemJson.getString("img_url");
            String title = itemJson.getString("purchase_name");
            String shareLink = itemJson.getString("link");

            BoughtListItem boughtListItem = new BoughtListItem();
            boughtListItem.setItemResourceId(resourceId);
            boughtListItem.setItemResourceType(resourceType);
            boughtListItem.setItemIcon(imgUrl);
            boughtListItem.setItemTitle(title);
            boughtListItem.setItemShareLink(shareLink);

            dataList.add(boughtListItem);
        }

        // 初始化 ListView
        boughtListAdapter = new BoughtListAdapter(this, dataList);
        boughtListAdapter.setOnItemClickWithBoughtItemListener(this);
        boughtListContent.setAdapter(boughtListAdapter);
        MeasureUtil.setListViewHeightBasedOnChildren(boughtListContent);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBoughtListItemClick(View view, BoughtListItem boughtListItem) {
        umShare(boughtListItem.getItemTitle(), boughtListItem.getItemIcon(), boughtListItem.getItemShareLink(), "");
        resourceId = boughtListItem.getItemResourceId();
        resourceType = boughtListItem.getItemResourceType();
    }
}
