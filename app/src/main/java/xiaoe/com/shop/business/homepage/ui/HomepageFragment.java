package xiaoe.com.shop.business.homepage.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import xiaoe.com.common.entitys.FlowInfoItem;
import xiaoe.com.common.entitys.GraphicNavItem;
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

    @BindView(R.id.home_app_bar)
    AppBarLayout homeAppBar;
    @BindView(R.id.home_head_recycler)
    RecyclerView homeTitleRecyclerView;
    @BindView(R.id.home_content_recycler)
    RecyclerView homeContentRecyclerView;
    @BindView(R.id.home_collapsing_toolbar)
    CollapsingToolbarLayout homeCollLayout;
    @BindView(R.id.home_tool_bar)
    Toolbar homeToolBar;

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
        HomepagePresenter hp = new HomepagePresenter(this);
        hp.requestData();

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
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayout.VERTICAL);
        List<ComponentInfo> tempData = new ArrayList<ComponentInfo>();
        // 搜索框假数据
//        ComponentInfo componentInfo = new ComponentInfo();
//        componentInfo.setTitle("课程");
//        componentInfo.setType("search");
//        tempData.add(componentInfo);
//        LinearLayoutManager llm_1 = new LinearLayoutManager(getActivity());
//        llm.setOrientation(LinearLayout.VERTICAL);
//        homeTitleRecyclerView.setLayoutManager(llm_1);
        // 信息流假数据
        ComponentInfo componentInfo_1 = new ComponentInfo();
        componentInfo_1.setType("flow_info");
        componentInfo_1.setTitle("10月10日 星期三");
        componentInfo_1.setDesc("今天");
        componentInfo_1.setJoinedDesc("我正在学");
        componentInfo_1.setImgUrl("res:///" + R.mipmap.icon_taday_learning);
        List<FlowInfoItem> flowInfoItemList = new ArrayList<>();
        FlowInfoItem flowInfoItem_1 = new FlowInfoItem();
        flowInfoItem_1.setItemType("imgText");
        flowInfoItem_1.setItemTitle("今日精选");
        flowInfoItem_1.setItemDesc("我的财富计划 新中产必修财富课程");
        flowInfoItem_1.setItemPrice("￥9.9");
        flowInfoItem_1.setItemHasBuy(false);
        flowInfoItem_1.setItemImg("http://pic.58pic.com/58pic/15/62/48/98Z58PICyiN_1024.jpg");
        FlowInfoItem flowInfoItem_2 = new FlowInfoItem();
        flowInfoItem_2.setItemType("audio");
        flowInfoItem_2.setItemTitle("音频组件");
        flowInfoItem_2.setItemDesc("我的财富计划 新中产必修财富课程");
        flowInfoItem_2.setItemPrice("￥999");
        flowInfoItem_2.setItemJoinedDesc("10928");
        flowInfoItem_2.setItemHasBuy(false);
        FlowInfoItem flowInfoItem_3 = new FlowInfoItem();
        flowInfoItem_3.setItemType("video");
        flowInfoItem_3.setItemTitle("视频组件");
        flowInfoItem_3.setItemDesc("每天看见吴晓波");
        flowInfoItem_3.setItemPrice("199.99");
        flowInfoItem_3.setItemImg("http://pic6.nipic.com/20100417/4578581_140045259657_2.jpg");
        flowInfoItem_3.setItemHasBuy(true);
        flowInfoItemList.add(flowInfoItem_1);
        flowInfoItemList.add(flowInfoItem_2);
        flowInfoItemList.add(flowInfoItem_3);
        componentInfo_1.setFlowInfoItemList(flowInfoItemList);

        tempData.add(componentInfo_1);
        homeContentRecyclerView.setLayoutManager(llm);
        if (tempData.get(0).getType().equals(DecorateEntityType.SEARCH_STR)) { // 标题在头顶
            List<ComponentInfo> titleData = new ArrayList<>();
            homeAppBar.setVisibility(View.VISIBLE);
            titleData.add(tempData.get(0));
            DecorateRecyclerAdapter dra = new DecorateRecyclerAdapter(getActivity(), titleData);
            homeTitleRecyclerView.setAdapter(dra);
            tempData.remove(0);
        } else {
            homeAppBar.setVisibility(View.GONE);
        }

        adapter = new DecorateRecyclerAdapter(getActivity(), tempData);
        homeContentRecyclerView.setAdapter(adapter);

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

            @Override
            public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
                Log.d(TAG, "onSharedElementStart: ");
            }

            @Override
            public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
                super.onSharedElementsArrived(sharedElementNames, sharedElements, listener);
                Log.d(TAG, "onSharedElementsArrived: ");
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: isSuccess --- " + success);
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
