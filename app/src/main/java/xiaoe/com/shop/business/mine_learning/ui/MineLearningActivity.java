package xiaoe.com.shop.business.mine_learning.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeListAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.utils.StatusBarUtil;

public class MineLearningActivity extends XiaoeActivity {

    private static final String TAG = "MineLearningActivity";

    private String pageTitle;

    @BindView(R.id.learning_back)
    ImageView learningBack;
    @BindView(R.id.learning_title)
    TextView learningTitle;
    @BindView(R.id.learning_list)
    ListView learningList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_learning);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        pageTitle = intent.getStringExtra("pageTitle");
        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            Global.g().setGlobalColor("#000000");
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_VISIBLE);
        }
        // TODO: 根据不同的 title 请求不同的接口
        if (pageTitle.equals("我的收藏")) {
//            MineLearningPresenter mineCollectionPresenter = new MineLearningPresenter(this, "我的收藏的接口");
//            mineCollectionPresenter.requestData();
        } else if (pageTitle.equals("我正在学")) {
//            MineLearningPresenter mineCollectionPresenter = new MineLearningPresenter(this, "我正在学的接口");
//            mineCollectionPresenter.requestData();
        } else {
            // 其他情况处理（没传 title）
        }

        initData();
        initListener();
    }

    private void initData() {
        learningTitle.setText(pageTitle);
        // 我的收藏页面使用的是只是商品的列表组件，所以可以直接用列表组件
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
        listItems.add(listItem_1);
        listItems.add(listItem_2);
        listItems.add(listItem_3);

        KnowledgeListAdapter knowledgeListAdapter = new KnowledgeListAdapter(this, listItems);
        learningList.setAdapter(knowledgeListAdapter);
    }

    private void initListener() {
        learningBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: isSuccess --- " + success);
    }
}
