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

    @BindView(R.id.course_background)
    SimpleDraweeView simpleDraweeView;

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
        simpleDraweeView.setImageURI("http://txt25-2.book118.com/2017/1105/book138977/138976472.jpg");
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
        ComponentInfo componentInfo = new ComponentInfo();
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
        componentInfo.setType("recent_update");
        componentInfo.setTitle("每天听见吴晓波");
        componentInfo.setDesc("已更新至09-22期");
        componentInfo.setSubList(itemList);
        tempData.add(componentInfo);
        if (tempData.get(0).getType().equals(DecorateEntityType.SEARCH_STR)) {
            courseAppBar.setVisibility(View.VISIBLE);
            List<ComponentInfo> titleData = new ArrayList<>();
            titleData.add(tempData.get(0));
            DecorateRecyclerAdapter dra = new DecorateRecyclerAdapter(getActivity(), titleData);
            courseTitleRecyclerView.setAdapter(dra);
            tempData.remove(0);
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
