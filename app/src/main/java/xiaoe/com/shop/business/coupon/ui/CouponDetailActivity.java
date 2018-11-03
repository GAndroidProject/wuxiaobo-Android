package xiaoe.com.shop.business.coupon.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.CouponInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeListAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.coupon.presenter.CouponPresenter;
import xiaoe.com.shop.widget.CouponView;
import xiaoe.com.shop.widget.StatusPagerView;

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
        if(couponInfo != null){
            CouponPresenter couponPresenter = new CouponPresenter(this);
            couponPresenter.requestCouponCanResource(couponInfo.getId());
        }else{
            statusPagerView.setPagerState(StatusPagerView.FAIL, getResources().getString(R.string.request_fail), R.mipmap.network_none);
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
                statusPagerView.setPagerState(StatusPagerView.FAIL, getResources().getString(R.string.request_fail), R.mipmap.network_none);
                return;
            }
            statusPagerView.setPagerState(StatusPagerView.FINISH, "", 0);
            JSONArray jsonResourceInfoList = jsonObject.getJSONObject("data").getJSONArray("resource_info");
            for (Object itemObject : jsonResourceInfoList) {
                JSONObject itemData = (JSONObject) itemObject;
                KnowledgeCommodityItem knowledgeCommodityItem = new KnowledgeCommodityItem();
                knowledgeCommodityItem.setResourceId(itemData.getString("id"));
                knowledgeCommodityItem.setHasBuy(false);
                knowledgeCommodityItem.setItemImg(itemData.getString("img_url"));
                knowledgeCommodityItem.setItemTitle(itemData.getString("title"));
                knowledgeCommodityItem.setItemPrice("¥"+itemData.getString("price"));
                if(itemData.getIntValue("type") == 1){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.IMAGE_TEXT);
                }else if(itemData.getIntValue("type") == 2){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.AUDIO);
                }else if(itemData.getIntValue("type") == 3){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.VIDEO);
                }else if(itemData.getIntValue("type") == 5 || itemData.getIntValue("type") == 6){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.COLUMN);
                }else if(itemData.getIntValue("type") == 8){
                    knowledgeCommodityItem.setSrcType(DecorateEntityType.TOPIC);
                }

                knowledgeCommodityItem.setItemTitleColumn(itemData.getString("summary"));
                resourceInfoList.add(knowledgeCommodityItem);
            }
            couponListAdapter.addAllData(resourceInfoList);
        }else{
            statusPagerView.setPagerState(StatusPagerView.FAIL, getResources().getString(R.string.request_fail), R.mipmap.network_none);
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
