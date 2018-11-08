package xiaoe.com.shop.business.bought_list.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.BoughtListItem;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.interfaces.OnItemClickWithBoughtItemListener;
import xiaoe.com.common.utils.MeasureUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.ScholarshipBoughtListRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.bought_list.presenter.BoughtListAdapter;
import xiaoe.com.shop.business.main.presenter.ScholarshipPresenter;

public class BoughtListActivity extends XiaoeActivity implements OnItemClickWithBoughtItemListener {

    private static final String TAG = "BoughtListActivity";

    @BindView(R.id.bought_list_back)
    ImageView boughtListBack;
    @BindView(R.id.bought_list_content)
    ListView boughtListContent;

    Unbinder unbinder;

    List<BoughtListItem> dataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bought_lsit);
        unbinder = ButterKnife.bind(this);
        dataList = new ArrayList<>();
        ScholarshipPresenter scholarshipPresenter = new ScholarshipPresenter(this);
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
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof ScholarshipBoughtListRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONArray data = ((JSONArray) ((JSONObject) result.get("data")).get("audio"));
                    initPageData(data);
                } else {
                    Log.d(TAG, "onMainThreadResponse: request fail...");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail, param error maybe");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    // 初始化页面数据
    private void initPageData(JSONArray data) {
        for (Object item : data) {
            JSONObject itemJson = (JSONObject) item;
            String resourceId = itemJson.getString("resource_id");
            int resourceType = itemJson.getInteger("resource_type");
            String resType = "";
            // 音频
            if (resourceType == 2) {
                resType = DecorateEntityType.AUDIO;
            }
            String imgUrl = itemJson.getString("img_url");
            String title = itemJson.getString("purchase_name");

            BoughtListItem boughtListItem = new BoughtListItem();
            boughtListItem.setItemResourceId(resourceId);
            boughtListItem.setItemResourceType(resType);
            boughtListItem.setItemIcon(imgUrl);
            boughtListItem.setItemTitle(title);

            dataList.add(boughtListItem);
        }

        // 初始化 ListView
        BoughtListAdapter boughtListAdapter = new BoughtListAdapter(this, dataList);
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
        umShare("http:www.baidu.com");
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
