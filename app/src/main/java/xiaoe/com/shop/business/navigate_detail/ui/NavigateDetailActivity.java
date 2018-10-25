package xiaoe.com.shop.business.navigate_detail.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.DecorateRecyclerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.navigate_detail.presenter.NavigateDetailPresenter;
import xiaoe.com.shop.business.search.ui.SearchActivity;

public class NavigateDetailActivity extends XiaoeActivity {

    private static final String TAG = "NavigateDetailActivity";

    @BindView(R.id.navigate_back)
    ImageView navigateBack;
    @BindView(R.id.navigate_title)
    TextView navigateTitle;
    @BindView(R.id.navigate_search)
    ImageView navigateSearch;
    @BindView(R.id.navigate_content)
    RecyclerView navigateContent;

    Intent intent;
    String pageTitle;
    String groupId;
    String resourceType = DecorateEntityType.RESOURCE_TAG; // 商品分组的资源类型

    NavigateDetailPresenter navigateDetailPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_detail);
        ButterKnife.bind(this);

        intent = getIntent();
        pageTitle = intent.getStringExtra("pageTitle");
        groupId = intent.getStringExtra("resourceId");

        // 发送网络请求
        navigateDetailPresenter = new NavigateDetailPresenter(this);
//        navigateDetailPresenter.requestData(groupId);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        navigateTitle.setText(pageTitle);
    }

    private void initData() {
        List<ComponentInfo> tempData = new ArrayList<>();

        // 知识商品假数据 -- 列表形式
        ComponentInfo componentInfo_know_list = new ComponentInfo();
        componentInfo_know_list.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        componentInfo_know_list.setSubType("knowledgeList");
        List<KnowledgeCommodityItem> listItems = new ArrayList<>();
        // 有价格没买
        KnowledgeCommodityItem listItem_1 = new KnowledgeCommodityItem();
        listItem_1.setItemTitle("我的财富计划新中产必修理财课程");
        listItem_1.setItemImg("http://img05.tooopen.com/images/20141020/sy_73154627197.jpg");
        listItem_1.setItemPrice("￥999.99");
        listItem_1.setHasBuy(false);
        listItem_1.setItemDesc("已更新至135期");
        // 有价格买了
        KnowledgeCommodityItem listItem_2 = new KnowledgeCommodityItem();
        listItem_2.setItemTitle("我的财富计划新中产必修理财课程杀杀杀");
        listItem_2.setItemImg("http://img.zcool.cn/community/01f39a59a7affba801211d25185cd3.jpg@1280w_1l_2o_100sh.jpg");
        listItem_2.setItemPrice("￥85.55");
        listItem_2.setHasBuy(true);
        listItem_2.setItemDesc("已更新至120期");
        // 没有价格，免费的
        KnowledgeCommodityItem listItem_3 = new KnowledgeCommodityItem();
        listItem_3.setItemTitle("我的财富计划新中产必修理财课程哒哒哒");
        listItem_3.setItemImg("http://img.zcool.cn/community/01951d55dd8f336ac7251df845a2ae.jpg");
        listItem_3.setItemDesc("已更新至101期");
        listItems.add(listItem_1);
        listItems.add(listItem_2);
        listItems.add(listItem_3);
        componentInfo_know_list.setKnowledgeCommodityItemList(listItems);
        // 知识商品假数据 -- 分组形式
        ComponentInfo componentInfo_know_group = new ComponentInfo();
        componentInfo_know_group.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        componentInfo_know_group.setSubType("knowledgeGroup");
        componentInfo_know_group.setTitle("栏目二");
        componentInfo_know_group.setDesc("");
        List<KnowledgeCommodityItem> groupItems = new ArrayList<>();
        KnowledgeCommodityItem groupItem_1 = new KnowledgeCommodityItem();
        groupItem_1.setItemTitle("我的财富计划 新中产必修理财课程");
        groupItem_1.setItemImg("http://pic15.nipic.com/20110619/7763155_101852942332_2.jpg");
        groupItem_1.setHasBuy(false);
        groupItem_1.setItemDesc("已更新至135期");
        groupItem_1.setItemPrice("￥980");
        KnowledgeCommodityItem groupItem_2 = new KnowledgeCommodityItem();
        groupItem_2.setItemTitle("我的财富计划");
        groupItem_2.setItemImg("http://pic38.nipic.com/20140212/17942401_101320663138_2.jpg");
        groupItem_2.setHasBuy(true);
        groupItem_2.setItemPrice("￥555.21");
        groupItem_2.setItemDesc("187次播放");
        KnowledgeCommodityItem groupItem_3 = new KnowledgeCommodityItem();
        groupItem_3.setItemTitle("学会规划自己的职场 必修理财课程");
        groupItem_3.setItemImg("http://gbres.dfcfw.com/Files/picture/20170925/9B00CEC6F06B756A4A9C256E870A324B.jpg");
        groupItem_3.setHasBuy(false);
        groupItem_3.setItemDesc("已更新至134期");
        KnowledgeCommodityItem groupItem_4 = new KnowledgeCommodityItem();
        groupItem_4.setItemTitle("这个文案是测试的，随便搞搞");
        groupItem_4.setItemImg("http://img.zcool.cn/community/01a9e45a60510ca8012113c7899c89.jpg@1280w_1l_2o_100sh.jpg");
        groupItem_4.setHasBuy(true);
        groupItem_4.setItemPrice("￥123.12");
        groupItem_4.setItemDesc("130次播放");
        groupItems.add(groupItem_1);
        groupItems.add(groupItem_2);
        groupItems.add(groupItem_3);
        groupItems.add(groupItem_4);
        componentInfo_know_group.setKnowledgeCommodityItemList(groupItems);

        tempData.add(componentInfo_know_list);
        tempData.add(componentInfo_know_group);

        DecorateRecyclerAdapter decorateRecyclerAdapter = new DecorateRecyclerAdapter(this, tempData);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        navigateContent.setLayoutManager(llm);
        navigateContent.setAdapter(decorateRecyclerAdapter);
    }

    private void initListener() {
        navigateBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        navigateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigateDetailActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            int code = result.getInteger("code");
            JSONObject data = (JSONObject) result.get("data");
        } else {
            Log.d(TAG, "onMainThreadResponse: 请求失败...");
            onBackPressed();
        }
    }
}
