package xiaoe.com.shop.business.search.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.common.entitys.SearchHistory;
import xiaoe.com.common.utils.SQLiteUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.DecorateRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.search.presenter.OnTabClickListener;
import xiaoe.com.shop.business.search.presenter.HistoryRecyclerAdapter;
import xiaoe.com.shop.business.search.presenter.RecommendRecyclerAdapter;
import xiaoe.com.shop.business.search.presenter.SearchSQLiteCallback;

public class SearchPageFragment extends BaseFragment implements OnTabClickListener {

    private static final String TAG = "SearchPageFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    SearchActivity searchActivity;

    // 软键盘
    InputMethodManager imm;

    SearchContentView historyContentView;
    SearchContentView recommendContentView;

    List<SearchHistory> historyList;
    List<String> recommendData; // 暂时写死

    public static SearchPageFragment newInstance(int layoutId) {
        SearchPageFragment searchPageFragment = new SearchPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        searchPageFragment.setArguments(bundle);
        return searchPageFragment;
    }

    protected void setHistoryList(List<SearchHistory> historyList) {
        this.historyList = historyList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            layoutId = bundle.getInt("layoutId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewWrap = inflater.inflate(layoutId, null, false);
        unbinder = ButterKnife.bind(this, viewWrap);
        mContext = getContext();
        searchActivity = (SearchActivity) getActivity();
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        return viewWrap;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (layoutId != -1) {
            initView();
        }
    }

    private void initView() {
        switch (layoutId) {
            case R.layout.fragment_search_main:
                initSearchMainFragment();
                break;
            case R.layout.fragment_search_empty:
                // do nothing;
                break;
            case R.layout.fragment_search_result:
                initSearchResultFragment();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    // 初始化搜索主页 fragment
    private void initSearchMainFragment() {
        historyContentView = (SearchContentView) viewWrap.findViewById(R.id.history_content);

        if (historyList.size() == 0) { // 没有历史记录
            historyContentView.setVisibility(View.GONE);
        } else { // 有历史记录
            historyContentView.setTitleStartText("历史搜索");
            historyContentView.setTitleEndText("清空历史搜索");

            historyContentView.setTitleEndClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 删除数据库中全部数据
                    SQLiteUtil.execSQL("delete from " + SearchSQLiteCallback.TABLE_NAME_CONTENT);
                    Toast.makeText(searchActivity, "删除成功", Toast.LENGTH_SHORT).show();
                    historyContentView.setVisibility(View.GONE);
                }
            });

            List<SearchHistory> historyData = historyList;
            HistoryRecyclerAdapter historyAdapter = new HistoryRecyclerAdapter(mContext, historyData);
            historyAdapter.setOnTabClickListener(this);
            historyContentView.setHistoryContentAdapter(historyAdapter);
        }

        recommendContentView = (SearchContentView) viewWrap.findViewById(R.id.recommend_content);;
        recommendContentView.setTitleEndVisibility(View.GONE);
        recommendContentView.setTitleStartText("大家都在搜");

        recommendData = new ArrayList<>();
        recommendData.add("区块链");
        recommendData.add("我的财富计划");
        recommendData.add("升职记");
        recommendData.add("健康");
        recommendData.add("新税收政策");
        RecommendRecyclerAdapter recommendAdapter = new RecommendRecyclerAdapter(mContext, recommendData);
        recommendAdapter.setOnTabClickListener(this);
        recommendContentView.setRecommendContentAdapter(recommendAdapter);
    }

    // 初始化搜索结果 fragment
    private void initSearchResultFragment() {
        RecyclerView recyclerView = (RecyclerView) viewWrap.findViewById(R.id.result_content);

        List<ComponentInfo> tempData = new ArrayList<>();

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

        tempData.add(componentInfo_know_list);
        tempData.add(componentInfo_know_group);

        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(llm);
        DecorateRecyclerAdapter adapter = new DecorateRecyclerAdapter(mContext, tempData);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        toggleSoftKeyboard();
        if (historyContentView.getVisibility() == View.VISIBLE && historyContentView.isMatchRecycler(view.getParent())) {
            String searchContent = historyList.get(position).getmContent();
            ((SearchActivity) getActivity()).searchContent.setText(searchContent);
            ((SearchActivity) getActivity()).searchContent.setSelection(searchContent.length());
            imm.toggleSoftInput(0, 0);
        } else if (recommendContentView.getVisibility() == View.VISIBLE && recommendContentView.isMatchRecycler(view.getParent())) {
            String searchContent = recommendData.get(position);
            ((SearchActivity) getActivity()).searchContent.setText(searchContent);
            ((SearchActivity) getActivity()).searchContent.setSelection(searchContent.length());
            imm.toggleSoftInput(0, 0);
        }
    }

    /**
     * 如果软键盘弹出，就关闭软键盘
     */
    private void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = searchActivity.getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
