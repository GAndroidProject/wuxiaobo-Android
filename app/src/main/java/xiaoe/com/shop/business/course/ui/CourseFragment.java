package xiaoe.com.shop.business.course.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.GraphicNavItem;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.common.entitys.RecentUpdateListItem;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.DecorateRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.interfaces.OnBottomTabSelectListener;
import xiaoe.com.shop.widget.SearchView;

public class CourseFragment extends BaseFragment {

    private static final String TAG = "CourseFragment";

    private Unbinder unbinder;
    private Context mContext;

    @BindView(R.id.course_app_bar)
    AppBarLayout courseAppBar;
    @BindView(R.id.course_title_recycler)
    RecyclerView courseTitleRecyclerView;
    @BindView(R.id.course_content_recycler)
    RecyclerView courseContentRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    public void init() {
        mContext = getActivity();
        List<ComponentInfo> tempData = new ArrayList<>();
        // 搜索框假数据
        ComponentInfo componentInfo_title = new ComponentInfo();
        componentInfo_title.setTitle("课程");
        componentInfo_title.setType("search");
        tempData.add(componentInfo_title);

        LinearLayoutManager llm_1 = new LinearLayoutManager(getActivity());
        llm_1.setOrientation(LinearLayout.VERTICAL);
        courseTitleRecyclerView.setLayoutManager(llm_1);

        // 初始化 RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayout.VERTICAL);
        courseContentRecyclerView.setLayoutManager(llm);

        // 轮播图假数据
        ComponentInfo componentInfo_shuffling = new ComponentInfo();
        componentInfo_shuffling.setType("shuffling_figure");
        List<String> urlList = new ArrayList<>();
//        urlList.add("res:///" + R.mipmap.audio_bg);
        urlList.add("http://img05.tooopen.com/images/20141020/sy_73154627197.jpg");
        urlList.add("http://pic.58pic.com/58pic/15/63/07/42Q58PIC42U_1024.jpg");
        urlList.add("http://img.zcool.cn/community/0125fd5770dfa50000018c1b486f15.jpg@1280w_1l_2o_100sh.jpg");
        componentInfo_shuffling.setShufflingList(urlList);

        // 导航假数据
        ComponentInfo componentInfo_navigator = new ComponentInfo();
        componentInfo_navigator.setType("graphic_navigation");
        List<GraphicNavItem> graphicNavItemList = new ArrayList<>();
        GraphicNavItem graphicNavItem_1 = new GraphicNavItem();
        graphicNavItem_1.setNavIcon("res:///" + R.mipmap.class_fundation);
        graphicNavItem_1.setNavContent("财富");
        GraphicNavItem graphicNavItem_2 = new GraphicNavItem();
        graphicNavItem_2.setNavIcon("res:///" + R.mipmap.class_bussines);
        graphicNavItem_2.setNavContent("职场");
        GraphicNavItem graphicNavItem_3 = new GraphicNavItem();
        graphicNavItem_3.setNavIcon("res:///" + R.mipmap.class_knowle);
        graphicNavItem_3.setNavContent("见识");
        GraphicNavItem graphicNavItem_4 = new GraphicNavItem();
        graphicNavItem_4.setNavIcon("res:///" + R.mipmap.class_family);
        graphicNavItem_4.setNavContent("家庭");
        graphicNavItemList.add(graphicNavItem_1);
        graphicNavItemList.add(graphicNavItem_2);
        graphicNavItemList.add(graphicNavItem_3);
        graphicNavItemList.add(graphicNavItem_4);
        componentInfo_navigator.setGraphicNavItemList(graphicNavItemList);

        // 最近更新假数据
        ComponentInfo componentInfo_recent = new ComponentInfo();
        List<RecentUpdateListItem> itemList = new ArrayList<>();
        RecentUpdateListItem item_1 = new RecentUpdateListItem();
        item_1.setListTitle("09-22期 | 为什么哈尔滨超过上海，成为…");
        item_1.setListPlayState("play");
        RecentUpdateListItem item_2 = new RecentUpdateListItem();
        item_2.setListTitle("09-21期 | 你还想海外逃税？国家已出双…");
        item_2.setListPlayState("play");
        RecentUpdateListItem item_3 = new RecentUpdateListItem();
        item_3.setListTitle("09-20期 | 十年后，我希望仍能与你在一起");
        item_3.setListPlayState("play");
        itemList.add(item_1);
        itemList.add(item_2);
        itemList.add(item_3);
        componentInfo_recent.setType("recent_update");
        componentInfo_recent.setTitle("每天听见吴晓波");
        componentInfo_recent.setDesc("已更新至09-22期");
        componentInfo_recent.setSubList(itemList);

        // 知识商品假数据 -- 列表形式
        ComponentInfo componentInfo_know_list = new ComponentInfo();
        componentInfo_know_list.setType("knowledge_commodity");
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
        componentInfo_know_group.setType("knowledge_commodity");
        componentInfo_know_group.setSubType("knowledgeGroup");
        componentInfo_know_group.setTitle("学会管理自己的财富");
        componentInfo_know_group.setDesc("查看更多");
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

        tempData.add(componentInfo_shuffling);
        tempData.add(componentInfo_navigator);
        tempData.add(componentInfo_recent);
        tempData.add(componentInfo_know_list);
        tempData.add(componentInfo_know_group);
        if (tempData.get(0).getType().equals(DecorateEntityType.SEARCH_STR)) {
            courseAppBar.setVisibility(View.VISIBLE);
            List<ComponentInfo> titleData = new ArrayList<>();
            titleData.add(tempData.get(0));
            titleData.add(tempData.get(1));
            titleData.add(tempData.get(2));
            DecorateRecyclerAdapter dra = new DecorateRecyclerAdapter(getActivity(), titleData);
            courseTitleRecyclerView.setAdapter(dra);
            tempData.remove(0);
            tempData.remove(1);
            tempData.remove(2);
        } else {
            courseAppBar.setVisibility(View.GONE);
        }
        courseContentRecyclerView.setAdapter(new DecorateRecyclerAdapter(getActivity(), tempData));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 网络请求数据代码
//        CoursePresenter cp = new CoursePresenter(this);
//        cp.requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: isSuccess --- " + success);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
