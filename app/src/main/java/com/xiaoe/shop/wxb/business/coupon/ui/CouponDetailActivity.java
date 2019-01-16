package com.xiaoe.shop.wxb.business.coupon.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.CouponInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeListAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.coupon.presenter.CouponPresenter;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.CouponView;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

public class CouponDetailActivity extends XiaoeActivity implements View.OnClickListener {
    private static final String TAG = "CouponDetailActivity";
    private CouponView couponView;
    private StatusPagerView statusPagerView;
    private List<KnowledgeCommodityItem> resourceInfoList;
    private ListView couponListView;
    private KnowledgeListAdapter couponListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_detail);
        initView();
        initData();
    }

    private void initView() {

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        ImageView  btnCouponBack = (ImageView) findViewById(R.id.coupon_back);
        btnCouponBack.setOnClickListener(this);
        resourceInfoList = new ArrayList<KnowledgeCommodityItem>();

        couponView = (CouponView) findViewById(R.id.coupon_view);
        statusPagerView = (StatusPagerView) findViewById(R.id.state_pager_view);
        statusPagerView.setPagerState(StatusPagerView.LOADING, "", 0);
        couponListView = (ListView) findViewById(R.id.coupon_can_resource_list);
        couponListAdapter = new KnowledgeListAdapter(this, resourceInfoList);
        couponListView.setAdapter(couponListAdapter);
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("coupon_bundle");
        CouponInfo couponInfo = (CouponInfo) bundle.getSerializable("coupon_info");
        couponView.setCouponInfo(couponInfo);
        couponView.showRightBthOnlyText(getString(R.string.enable_text));
        if(couponInfo != null){
            CouponPresenter couponPresenter = new CouponPresenter(this);
            couponPresenter.requestCouponCanResource(couponInfo.getId());
        }else{
            statusPagerView.setPagerState(StatusPagerView.FAIL, getResources().getString(R.string.request_fail), R.mipmap.ic_network_error);
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if(activityDestroy){
            return;
        }
        if(success){
            JSONObject jsonObject = (JSONObject) entity;
            if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED){
                statusPagerView.setPagerState(StatusPagerView.FAIL, getResources().getString(R.string.request_fail), R.mipmap.ic_network_error);
                return;
            }
            statusPagerView.setLoadingFinish();
            JSONArray jsonResourceInfoList = jsonObject.getJSONObject("data").getJSONArray("resource_info");
            for (Object itemObject : jsonResourceInfoList) {
                JSONObject itemData = (JSONObject) itemObject;
                KnowledgeCommodityItem knowledgeCommodityItem = new KnowledgeCommodityItem();
                String price = "¥" + itemData.getString("price");
                int type = itemData.getInteger("type") == null ? 0 : itemData.getInteger("type");
                if(type == 1){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.IMAGE_TEXT);
                }else if(type == 2){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.AUDIO);
                }else if(type == 3){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.VIDEO);
                }else if(type == 5 || type == 6){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.COLUMN);
                }else if(type == 8){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.TOPIC);
                } else if (type == 23) {
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.SUPER_VIP);
                    price = price + "/年";
                }

                knowledgeCommodityItem.setResourceId(itemData.getString("id"));
                knowledgeCommodityItem.setHasBuy(false);
                knowledgeCommodityItem.setItemImg(itemData.getString("img_url"));
                knowledgeCommodityItem.setItemTitle(itemData.getString("title"));
                knowledgeCommodityItem.setItemPrice(price);

                knowledgeCommodityItem.setItemTitleColumn(itemData.getString("summary"));
                resourceInfoList.add(knowledgeCommodityItem);
            }
//            couponListAdapter.addAllData(resourceInfoList);
            couponListAdapter.notifyDataSetChanged();
        }else{
            statusPagerView.setPagerState(StatusPagerView.FAIL, getResources().getString(R.string.request_fail), R.mipmap.ic_network_error);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.coupon_back:
                finish();
                break;
            default:
                break;
        }
    }
}
