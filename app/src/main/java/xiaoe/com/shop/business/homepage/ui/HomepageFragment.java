package xiaoe.com.shop.business.homepage.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.common.entitys.RecentUpdateListItem;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.DecorateRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.homepage.presenter.HomepagePresenter;

public class HomepageFragment extends BaseFragment {

    private static final String TAG = "HomepageFragment";

    private Unbinder unbinder;
    private Context mContext;

    private HomepagePresenter homePagePresenter;

    private boolean destroyView = false;

    @BindView(R.id.home_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.home_title_time)
    TextView homeTitleTime;
    @BindView(R.id.home_title_desc)
    TextView homeTitleDesc;
    @BindView(R.id.home_title_icon)
    ImageView homeTitleIcon;
    @BindView(R.id.home_title_icon_desc)
    TextView homeTitleIconDesc;
    private DecorateRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 网络请求数据代码
//        HomepagePresenter hp = new HomepagePresenter(this);
//        hp.requestData();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
//        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HomepageFragment.this.Toast("弹出一个吐司");
//            }
//        });
//        toolbar.setLogo(R.mipmap.ic_launcher);
//        toolbar.setTitle("我是标题");
//        toolbar.setSubtitle("这是子标题");
    }

    public void init() {
        homeTitleTime.setText("9月4日 星期二");
        homeTitleDesc.setText("今天");
        homeTitleIcon.setImageResource(R.mipmap.icon_taday_learning);
        homeTitleIconDesc.setText("我正在学");
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayout.VERTICAL);
        List<ComponentInfo> tempData = new ArrayList<ComponentInfo>();
        // 信息流假数据
        ComponentInfo componentInfo_1 = new ComponentInfo();
        componentInfo_1.setType("flow_info");
        componentInfo_1.setSubType("imgText");
        componentInfo_1.setTitle("今日精选");
        componentInfo_1.setDesc("我的财富计划 新中产必修财富课程");
        componentInfo_1.setPrice("9.9");
        componentInfo_1.setHasBuy(false);
        componentInfo_1.setImgUrl("http://img.zcool.cn/community/0117e2571b8b246ac72538120dd8a4.jpg@1280w_1l_2o_100sh.jpg");
        ComponentInfo componentInfo_2 = new ComponentInfo();
        componentInfo_2.setType("flow_info");
        componentInfo_2.setSubType("audio");
        componentInfo_2.setTitle("音频组件");
        componentInfo_2.setDesc("我的财富计划 新中产必修财富课程");
        componentInfo_2.setPrice("999.99");
        componentInfo_2.setJoinedDesc("10928");
        componentInfo_2.setHasBuy(false);
        ComponentInfo componentInfo_3 = new ComponentInfo();
        componentInfo_3.setType("flow_info");
        componentInfo_3.setSubType("video");
        componentInfo_3.setTitle("视频组件");
        componentInfo_3.setDesc("每天看见吴晓波");
        componentInfo_3.setPrice("199.99");
        componentInfo_3.setImgUrl("http://pic14.nipic.com/20110605/1369025_165540642000_2.jpg");
        componentInfo_3.setHasBuy(true);
        // 轮播图假数据
        ComponentInfo componentInfo_4 = new ComponentInfo();
        componentInfo_4.setType("shuffling_figure");
        List<String> urlList = new ArrayList<>();
        urlList.add("http://pic.58pic.com/58pic/15/63/07/42Q58PIC42U_1024.jpg");
        urlList.add("http://img.zcool.cn/community/0125fd5770dfa50000018c1b486f15.jpg@1280w_1l_2o_100sh.jpg");
        urlList.add("res:///" + R.mipmap.audio_bg);
        urlList.add("http://img05.tooopen.com/images/20141020/sy_73154627197.jpg");
        componentInfo_4.setShufflingList(urlList);
        // 最近更新假数据
        ComponentInfo componentInfo_5 = new ComponentInfo();
        List<RecentUpdateListItem> itemList = new ArrayList<>();
        RecentUpdateListItem item_1 = new RecentUpdateListItem();
        item_1.setListTitle("09-22期 | 为什么哈哈哈哈哈哈哈，成为…");
        item_1.setListPlayState("play");
        RecentUpdateListItem item_2 = new RecentUpdateListItem();
        item_2.setListTitle("09-21期 | 你还想海外逃税？国家哒哒哒…");
        item_2.setListPlayState("play");
        RecentUpdateListItem item_3 = new RecentUpdateListItem();
        item_3.setListTitle("09-20期 | 十年后，我希望仍能与你在一起");
        item_3.setListPlayState("play");
        itemList.add(item_1);
        itemList.add(item_2);
        itemList.add(item_3);
        componentInfo_5.setType("recent_update");
        componentInfo_5.setTitle("每天听见吴晓波");
        componentInfo_5.setDesc("已更新至09-22期");
        componentInfo_5.setSubList(itemList);
        // 知识商品假数据 -- 列表形式
        ComponentInfo componentInfo_6 = new ComponentInfo();
        componentInfo_6.setType("knowledge_commodity");
        componentInfo_6.setSubType("knowledgeList");
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
        componentInfo_6.setKnowledgeCommodityItemList(listItems);
        // 知识商品假数据 -- 分组形式
        ComponentInfo componentInfo_7 = new ComponentInfo();
        componentInfo_7.setType("knowledge_commodity");
        componentInfo_7.setSubType("knowledgeGroup");
        componentInfo_7.setTitle("学会管理自己的财富");
        componentInfo_7.setDesc("查看更多");
        List<KnowledgeCommodityItem> groupItems = new ArrayList<>();
        KnowledgeCommodityItem groupItem_1 = new KnowledgeCommodityItem();
        groupItem_1.setItemTitle("我的财富计划 新中产必修理财课程");
        groupItem_1.setItemImg("http://pic15.nipic.com/20110619/7763155_101852942332_2.jpg");
        groupItem_1.setHasBuy(false);
        groupItem_1.setItemDesc("已更新至135期");
        groupItem_1.setItemPrice("￥980.08");
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
        componentInfo_7.setKnowledgeCommodityItemList(groupItems);
        tempData.add(componentInfo_1);
        tempData.add(componentInfo_2);
        tempData.add(componentInfo_3);
        tempData.add(componentInfo_4);
        tempData.add(componentInfo_5);
        tempData.add(componentInfo_6);
        tempData.add(componentInfo_7);
        recyclerView.setLayoutManager(llm);
        adapter = new DecorateRecyclerAdapter(getActivity(), tempData);
        recyclerView.setAdapter(adapter);
        getActivity().setExitSharedElementCallback(new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                Log.d(TAG, "onSharedElementEnd: ");
                for (View view : sharedElements) {
                    if (view instanceof SimpleDraweeView) {
                        view.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
//        Log.d(TAG, "onMainThreadResponse: isSuccess --- " + success);
    }

    @Override
    public void onResume() {
        super.onResume();
//        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
