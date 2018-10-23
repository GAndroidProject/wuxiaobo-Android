package xiaoe.com.shop.business.course_more.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.DecorateRecyclerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.course.presenter.CoursePresenter;
import xiaoe.com.shop.utils.StatusBarUtil;

public class CourseMoreActivity extends XiaoeActivity {

    private static final String TAG = "CourseMoreActivity";

    @BindView(R.id.course_more_back)
    ImageView courseMoreBack;
    @BindView(R.id.course_more_title)
    TextView courseMoreTitle;
    @BindView(R.id.course_more_content)
    RecyclerView courseMoreContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_more);
        ButterKnife.bind(this);

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            Global.g().setGlobalColor("#000000");
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_VISIBLE);
        }

//        CoursePresenter coursePresenter = new CoursePresenter(this);
//        coursePresenter.requestData();

        initData();
        initListener();
    }

    private void initData() {

        List<ComponentInfo> tempData = new ArrayList<>();

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayout.VERTICAL);
        courseMoreContent.setLayoutManager(llm);

        // 知识商品假数据 -- 分组形式
        ComponentInfo componentInfo_know_group = new ComponentInfo();
        componentInfo_know_group.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        componentInfo_know_group.setSubType("knowledgeGroup");
        componentInfo_know_group.setTitle("学会管理自己的财富");
        componentInfo_know_group.setDesc("查看更多");
        componentInfo_know_group.setHideTitle(true);
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
        groupItems.add(groupItem_1);
        groupItems.add(groupItem_2);
        groupItems.add(groupItem_3);
        groupItems.add(groupItem_4);
        componentInfo_know_group.setKnowledgeCommodityItemList(groupItems);

        tempData.add(componentInfo_know_group);

        courseMoreContent.setAdapter(new DecorateRecyclerAdapter(this, tempData));
    }

    private void initListener() {
        courseMoreBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: " + success);
    }
}
