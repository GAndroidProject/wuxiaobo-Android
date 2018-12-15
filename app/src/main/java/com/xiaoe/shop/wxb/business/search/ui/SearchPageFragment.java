package com.xiaoe.shop.wxb.business.search.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.db.SQLiteUtil;

import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.common.entitys.SearchHistory;
import com.xiaoe.common.interfaces.OnItemClickWithPosListener;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.DecorateRecyclerAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.search.presenter.HistoryRecyclerAdapter;
import com.xiaoe.shop.wxb.business.search.presenter.RecommendRecyclerAdapter;
import com.xiaoe.shop.wxb.business.search.presenter.SearchSQLiteCallback;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.interfaces.OnCustomDialogListener;
import com.xiaoe.shop.wxb.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SearchPageFragment extends BaseFragment implements OnItemClickWithPosListener {

    private static final String TAG = "SearchPageFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    SearchActivity searchActivity;
    Object dataList; // 搜索结果

    // 软键盘
    InputMethodManager imm;

    // 首页 fragment 所需要的基础内容
    SearchContentView historyContentView;
    SearchContentView recommendContentView;
    List<SearchHistory> historyList; // 历史搜索记录集合
    List<String> recommendData; // 推荐集合（暂时写死）

    // 搜索结果 fragment 需要的基础内容
    RecyclerView searchResultRecycler;
    List<ComponentInfo> itemComponentList;
    List<JSONObject> itemJsonList; // 单品 json 集合
    List<JSONObject> groupJsonList; // 专栏、大专栏 json 集合
    List<SearchHistory> historyData; // 历史数据
    HistoryRecyclerAdapter historyAdapter; // 历史数据适配器

    LinearLayoutManager layoutManager;
    DecorateRecyclerAdapter decorateRecyclerAdapter; // 搜索结果

    public static SearchPageFragment newInstance(int layoutId) {
        SearchPageFragment searchPageFragment = new SearchPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        searchPageFragment.setArguments(bundle);
        return searchPageFragment;
    }

    protected void setDataList(Object dataList) {
        if (this.dataList != null) { // 保证数据都是最新的
            this.dataList = null;
            // 组件集合、专栏/大专栏集合、单品集合都需要清空
            this.itemComponentList.clear();
            this.groupJsonList.clear();
            this.itemJsonList.clear();
        }
        this.dataList = dataList;
        if (this.searchResultRecycler != null) { // recyclerView 已经初始化过一次，重新加载数据
            decorateRecyclerAdapter.notifyDataSetChanged();
            initNewDataList();
        }
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
        if (layoutId != -1) {
            initView();
        }
        imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        return viewWrap;
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
        recommendContentView = (SearchContentView) viewWrap.findViewById(R.id.recommend_content);

        initHistoryView(historyContentView);
        initRecommendView(recommendContentView);
    }

    // 初始化历史搜索的 view
    private void initHistoryView(SearchContentView historyContentView) {
        if (historyList.size() == 0) { // 没有历史记录
            historyContentView.setVisibility(View.GONE);
        } else { // 有历史记录
            historyContentView.setVisibility(View.VISIBLE);
            historyContentView.setTitleStartText("历史搜索");
            historyContentView.setTitleEndText("清空历史搜索");

            historyContentView.setTitleEndClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    // 删除数据库中全部数据
                    getDialog().setTitleVisibility(View.GONE);
                    getDialog().setHintMessage(getString(R.string.clear_history_record));
                    getDialog().setConfirmText(getResources().getString(R.string.confirm_title));
                    getDialog().setConfirmTextColor(getResources().getColor(R.color.recent_update_btn_pressed));
                    getDialog().setCancelText(getResources().getString(R.string.cancel_title));
                    getDialog().setCancelTextColor(getResources().getColor(R.color.recent_update_btn_pressed));
                    getDialog().setOnCustomDialogListener(new OnCustomDialogListener() {
                        @Override
                        public void onClickCancel(View view, int tag) {
                            getDialog().dismissDialog();
                            Log.d(TAG, "onClickCancel: 2");
                        }

                        @Override
                        public void onClickConfirm(View view, int tag) {
                            getDialog().dismissDialog();
                            Log.d(TAG, "onClickConfirm: 1");
                            SQLiteUtil.init(mContext, new SearchSQLiteCallback()).execSQL("delete from " + SearchSQLiteCallback.TABLE_NAME_CONTENT);
                            ToastUtils.show(searchActivity, R.string.successfully_delete);
                            historyData.clear();
                            historyContentView.setVisibility(View.GONE);
                        }

                        @Override
                        public void onDialogDismiss(DialogInterface dialog, int tag, boolean backKey) {

                        }
                    });
                    getDialog().showDialog(1);
                }
            });

            historyData = historyList;
            historyAdapter = new HistoryRecyclerAdapter(mContext, historyData);
            historyAdapter.setOnItemClickWithPosListener(this);
            historyContentView.setHistoryContentAdapter(historyAdapter);
        }
    }

    // 初始化推荐搜索的 view
    private void initRecommendView(SearchContentView recommendContentView) {
        recommendContentView.setTitleEndVisibility(View.GONE);
        recommendContentView.setTitleStartText("大家都在搜");

        recommendData = new ArrayList<>();
        // 默认的四个关键词
        recommendData.add(getString(R.string.my_wealth_plan));
        recommendData.add(getString(R.string.search_wxb_channel));
        recommendData.add(getString(R.string.search_my_career_plan));
        recommendData.add(getString(R.string.search_avoid_failure));
        RecommendRecyclerAdapter recommendAdapter = new RecommendRecyclerAdapter(mContext, recommendData);
        recommendAdapter.setOnTabClickListener(this);
        recommendContentView.setRecommendContentAdapter(recommendAdapter);
    }

    // 初始化搜索结果 fragment
    private void initSearchResultFragment() {
        // 初始化基础设施
        itemJsonList = new ArrayList<>();
        groupJsonList = new ArrayList<>();
        // 初始化组件容器
        itemComponentList = new ArrayList<>();
        searchResultRecycler = (RecyclerView) viewWrap.findViewById(R.id.result_content);
        initNewDataList();
    }

    /**
     * 初始化搜索结果
     * 需求是根据每个课程的类型进行显示，查看更多需要跳页完成
     */
    private void initNewDataList() {
        JSONArray dataJsonList = (JSONArray) dataList;
        for (Object jsonItem : dataJsonList) {
            JSONObject dataListItem = (JSONObject) jsonItem;
            String label = dataListItem.getString("label");
            int type = dataListItem.getInteger("type");
            boolean showMore = dataListItem.getBoolean("show_more");
            if ("".equals(label) || label == null) {
                // 异常情况，跳过
                continue;
            }
            JSONArray list = (JSONArray) dataListItem.get("list");
            initTypeData(label, type, showMore, list);
        }
        // 初始化 recyclerView
        if (!searchActivity.hasDecorate) {
            initRecycler();
        }
    }

    /**
     * 初始化对应类型的数据
     */
    private void initTypeData(String typeTitle, int type, boolean showMore, JSONArray list) {
        ComponentInfo itemComponentInfo = new ComponentInfo();
        itemComponentInfo.setHideTitle(false);
        itemComponentInfo.setTitle(typeTitle);
        itemComponentInfo.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
        itemComponentInfo.setSubType(DecorateEntityType.KNOWLEDGE_LIST_STR);
        itemComponentInfo.setSearchType(type);
        String keyword = searchActivity.searchKeyword;
        itemComponentInfo.setJoinedDesc(keyword); // 注意：使用 joinedDesc 字段表示搜索的关键字
        if (showMore) { // 只是让组件知道需要显示查看全部的按钮，需要查看更多就传 !""，否则传 ""
            itemComponentInfo.setDesc(getString(R.string.list_watch_more));
        } else {
            itemComponentInfo.setDesc("");
        }
        List<KnowledgeCommodityItem> contentList = new ArrayList<>();
        // 遍历系列课程
        for (Object listItem : list) {
            JSONObject item = (JSONObject) listItem;
            KnowledgeCommodityItem commodityItem = new KnowledgeCommodityItem();
            String resourceId = item.getString("id");
            String resourceType = convertInt2Str(type);
            if (TextUtils.isEmpty(resourceType)) {
                continue;
            }
            String imgUrl = item.getString("img_url");
            String title = item.getString("title");
            String columnDesc = item.getString("summary");
            float price = item.getFloat("price");
            String priceStr;
            if (price == 0) {
                priceStr = "";
            } else {
                priceStr = "￥" + price;
            }
            commodityItem.setItemTitle(title);
            commodityItem.setItemImg(imgUrl);
            commodityItem.setItemTitleColumn(columnDesc);
            commodityItem.setSrcType(resourceType);
            commodityItem.setResourceId(resourceId);
            commodityItem.setItemPrice(priceStr);
            contentList.add(commodityItem);
        }
        // 有数据才显示
        if (contentList.size() > 0) {
            itemComponentInfo.setKnowledgeCommodityItemList(contentList);
            if (searchActivity.hasDecorate) {
                itemComponentInfo.setNeedDecorate(false);
            } else {
                itemComponentInfo.setNeedDecorate(true);
            }
            itemComponentList.add(itemComponentInfo);
        }
    }

    /**
     * 初始化搜索结果数据类型，分类依据 -- 单品 / 专栏、大专栏
     * 备注一下，这套代码是用于筛选返回结果中的单品课程和系列课程，然后进行显示
     * 11.23 需求变更后不再使用，但保留
     * @deprecated 使用 initTypeData 代替
     */
    private void initDataList() {
        JSONArray dataJsonList = (JSONArray) dataList;
        for (Object jsonItem : dataJsonList) {
            JSONObject dataListItem = (JSONObject) jsonItem;
            String label = dataListItem.getString("label");
            // 内容 list
            JSONArray list = (JSONArray) dataListItem.get("list");
            if (label.equals(getString(R.string.image_text))
                    || label.equals(getString(R.string.audio_text))
                    || label.equals(getString(R.string.video_text))) {
                for (Object listItem : list) {
                    JSONObject item = (JSONObject) listItem;
                    item.put("resource_type", dataListItem.getInteger("type"));
                    itemJsonList.add(item);
                }
            } else if (label.equals(getString(R.string.column_text))
                    || label.equals(getString(R.string.big_column_text))
                    || label.equals(getString(R.string.member_text))) {
                for (Object listItem : list) {
                    JSONObject item = (JSONObject) listItem;
                    item.put("resource_type", dataListItem.getInteger("type"));
                    groupJsonList.add(item);
                }
            }
        }

        initSearchResultData();
    }

    /**
     * 初始化搜索结果数据（配合分类进行初始化）
     * @deprecated
     */
    private void initSearchResultData() {
        // 专栏、大专栏
        if (groupJsonList.size() > 0) {
            initPageDataByJson(groupJsonList, true);
        }
        // 单品
        if (itemJsonList.size() > 0) {
            initPageDataByJson(itemJsonList, false);
        }
        // 初始化 recyclerView
        if (!searchActivity.hasDecorate) {
            initRecycler();
        }
    }

    /**
     * 初始化页面数据 -- 大于 3 个宫格显示，否则列表显示（配合分类进行初始化）
     *
     * @param listData 页面数据（包括单品、专栏、大专栏）
     * @param isGroup 是否为专栏/大专栏
     * @deprecated
     */
    private void initPageDataByJson(List<JSONObject> listData, boolean isGroup) {
        ComponentInfo itemComponentInfo = new ComponentInfo();
        itemComponentInfo.setHideTitle(false);
        if (isGroup) { // 系列课程 -- 列表显示
            itemComponentInfo.setTitle(getString(R.string.series_courses));
            itemComponentInfo.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
            itemComponentInfo.setSubType(DecorateEntityType.KNOWLEDGE_LIST_STR);
        } else { // 单品课程 -- 宫格显示
            itemComponentInfo.setTitle(getString(R.string.item_course));
            itemComponentInfo.setType(DecorateEntityType.KNOWLEDGE_COMMODITY_STR);
            itemComponentInfo.setSubType(DecorateEntityType.KNOWLEDGE_GROUP_STR);
        }
        // 目的将 title 的查看更多隐藏掉
        itemComponentInfo.setDesc("");
        List<KnowledgeCommodityItem> contentList = new ArrayList<>();
        // 遍历系列课程
        for (int i = 0 ; i < listData.size(); i++) {
            if (isGroup && i >= 3) { // 分组形式，只显示 3 个
                break;
            }
            KnowledgeCommodityItem commodityItem = new KnowledgeCommodityItem();
            JSONObject item = listData.get(i);
            String resourceId = item.getString("id");
            String resourceType = convertInt2Str((int) item.get("resource_type"));
            String imgUrl = item.getString("img_url");
            String title = item.getString("title");
            String columnDesc = item.getString("summary");
            float price = item.getFloat("price");
            String priceStr;
            if (price == 0) {
                priceStr = "";
            } else {
                priceStr = "￥" + price;
            }
            commodityItem.setItemTitle(title);
            commodityItem.setItemImg(imgUrl);
            commodityItem.setItemTitleColumn(columnDesc);
            commodityItem.setSrcType(resourceType);
            commodityItem.setResourceId(resourceId);
            commodityItem.setItemPrice(priceStr);
            contentList.add(commodityItem);
        }
        // 有数据才显示
        if (contentList.size() > 0) {
            itemComponentInfo.setKnowledgeCommodityItemList(contentList);
            if (searchActivity.hasDecorate) {
                itemComponentInfo.setNeedDecorate(false);
            } else {
                itemComponentInfo.setNeedDecorate(true);
            }
            itemComponentList.add(itemComponentInfo);
        }
    }

    // 初始化 recyclerView
    private void initRecycler() {
        layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchResultRecycler.setLayoutManager(layoutManager);
        decorateRecyclerAdapter = new DecorateRecyclerAdapter(mContext, itemComponentList,true);
        searchResultRecycler.setAdapter(decorateRecyclerAdapter);
        decorateRecyclerAdapter.notifyDataSetChanged();
        searchActivity.hasDecorate = true;
    }

    @Override
    public void onItemClick(View view, int position) {
        toggleSoftKeyboard();
        if (historyContentView.getVisibility() == View.VISIBLE && historyContentView.isMatchRecycler(view.getParent())) {
            String searchContent = historyList.get(position).getmContent();
            ((SearchActivity) getActivity()).searchContent.setText(searchContent);
            ((SearchActivity) getActivity()).searchContent.setSelection(searchContent.length());
            ((SearchActivity) getActivity()).obtainSearchResult(((SearchActivity) getActivity()).searchContent.getText().toString());
        } else if (recommendContentView.getVisibility() == View.VISIBLE && recommendContentView.isMatchRecycler(view.getParent())) {
            String searchContent = recommendData.get(position);
            ((SearchActivity) getActivity()).searchContent.setText(searchContent);
            ((SearchActivity) getActivity()).searchContent.setSelection(searchContent.length());
            ((SearchActivity) getActivity()).obtainSearchResult(((SearchActivity) getActivity()).searchContent.getText().toString());
            if (!searchActivity.hasDbData(searchContent)) { // 点击推荐，查库没有后需要入库
                String currentTime = searchActivity.obtainCurrentTime();
                SearchHistory searchHistory = new SearchHistory(searchContent, currentTime);
                SQLiteUtil.init(mContext, new SearchSQLiteCallback()).insert(SearchSQLiteCallback.TABLE_NAME_CONTENT, searchHistory);

                // 入库后集合需要改变（已有历史搜索的记录）
                if (historyData != null) {
                    historyContentView.setVisibility(View.VISIBLE);
                    if (historyData.size() >= 5) {
                        historyData.add(0, searchHistory);
                        historyData.remove(historyData.size() - 1); // 去掉最后一个
                    } else { // 否则直接添加
                        historyData.add(0, searchHistory);
                    }
                } else {
                    // 没有历史搜索记录
                    if (historyAdapter == null) {
                        historyList = new ArrayList<>();
                        historyList.add(searchHistory);
                        historyContentView = (SearchContentView) viewWrap.findViewById(R.id.history_content);
                        initHistoryView(historyContentView);
                    }
                }
                historyAdapter.notifyDataSetChanged();
            }
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

    /**
     * 资源类型转换 int - str （暂不支持直播）
     * @param resourceType 资源类型
     * @return 资源类型的字符串形式
     */
    protected String convertInt2Str(int resourceType) {
        switch (resourceType) {
            case 1: // 图文
                return DecorateEntityType.IMAGE_TEXT;
            case 2: // 音频
                return DecorateEntityType.AUDIO;
            case 3: // 视频
                return DecorateEntityType.VIDEO;
            case 5: // 会员
                return DecorateEntityType.MEMBER;
            case 6: // 专栏
                return DecorateEntityType.COLUMN;
            case 8: // 大专栏
                return DecorateEntityType.TOPIC;
            default:
                return "";
        }
    }
}
