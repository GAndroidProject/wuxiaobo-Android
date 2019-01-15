package com.xiaoe.shop.wxb.adapter.decorate;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.flow_info.NewFlowInfoRecyclerAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.flow_info.FlowInfoViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation.GraphicNavRecyclerAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation.GraphicNavViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeGroupRecyclerAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeGroupViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeListAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeListViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.recent_update.RecentUpdateListAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.recent_update.RecentUpdateViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.search.SearchViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.shuffling_figure.ShufflingFigureViewHolder;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

/**
 * 店铺装修组件显示列表适配器
 */
public class DecorateRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "DecorateRecyclerAdapter";

    private Context mContext;
    // 组件实体类列表
    private List<ComponentInfo> mComponentList;

    // 信息流
    private SparseArray<NewFlowInfoRecyclerAdapter> flowInfoRecyclerAdapterArr;
    // 知识商品（列表）
    private SparseArray<KnowledgeListAdapter> knowledgeListAdapterArr;
    // 知识商品（宫格）
    private SparseArray<KnowledgeGroupRecyclerAdapter> knowledgeGroupRecyclerAdapterArr;
    // 导航组件 manager
    private SparseArray<GraphicNavRecyclerAdapter> graphicNavRecyclerAdapterArr;
    // 最近更新
    private SparseArray<RecentUpdateListAdapter> recentUpdateListAdapterArr;
    // 轮播图
    private SparseArray<Banner> bannerArr;

    // 知识商品分组形式的 recycler
    private List<RecyclerView> knowledgeGroupRecyclerList;

    boolean isSearch = false;

    private final int BASE_ITEM_TYPE_FOOTER = 10002;
    private boolean isShowLastItem = true;

    public DecorateRecyclerAdapter(Context context, List<ComponentInfo> componentList) {
        this.mContext = context;
        this.mComponentList = componentList;
    }

    public DecorateRecyclerAdapter(Context context, List<ComponentInfo> componentList,boolean isSearch) {
        this.mContext = context;
        this.mComponentList = componentList;
        this.isSearch = isSearch;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // 为了兼容 .9 图而分别设置 margin
        switch(viewType) {
            case DecorateEntityType.FLOW_INFO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info, null);
                layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 28), 0, 0);
                view.setLayoutParams(layoutParams);
                if (flowInfoRecyclerAdapterArr == null) {
                    flowInfoRecyclerAdapterArr = new SparseArray<>();
                }
                return new FlowInfoViewHolder(mContext, view);
            case DecorateEntityType.RECENT_UPDATE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_update, null);
                layoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 18), Dp2Px2SpUtil.dp2px(mContext, 20), 0);
                view.setPadding(0, 0, 0, Dp2Px2SpUtil.dp2px(mContext, 8));
                view.setLayoutParams(layoutParams);
                if (recentUpdateListAdapterArr == null) {
                    recentUpdateListAdapterArr = new SparseArray<>();
                }
                return new RecentUpdateViewHolder(mContext, view);
            case DecorateEntityType.KNOWLEDGE_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_list, null);
//                 layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 10), 0, 0);
                view.setPadding(Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 16), Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 16));
                view.setLayoutParams(layoutParams);
                if (knowledgeListAdapterArr == null) {
                    knowledgeListAdapterArr = new SparseArray<>();
                }
                return new KnowledgeListViewHolder(mContext, view);
            case DecorateEntityType.KNOWLEDGE_GROUP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_group, null);
                // layoutParams.setMargins(0, 0, 0, Dp2Px2SpUtil.dp2px(mContext, 12));
                view.setPadding(Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 16), Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 16));
                view.setLayoutParams(layoutParams);
                if (knowledgeGroupRecyclerList == null) { // 保证 list 不会被覆盖
                    knowledgeGroupRecyclerList = new ArrayList<>();
                }
                if (knowledgeGroupRecyclerAdapterArr == null) {
                    knowledgeGroupRecyclerAdapterArr = new SparseArray<>();
                }
                return new KnowledgeGroupViewHolder(mContext, view);
            case DecorateEntityType.SHUFFLING_FIGURE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shuffling_figure, null);
                // 15 是减去阴影的 (15.5px=5px)
                layoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, 15), Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 15), 0);
                view.setLayoutParams(layoutParams);
                if (bannerArr == null) {
                    bannerArr = new SparseArray<>();
                }
                return new ShufflingFigureViewHolder(mContext, view);
            case DecorateEntityType.GRAPHIC_NAVIGATION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphic_navigation, null);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 16), Dp2Px2SpUtil.dp2px(mContext, 20), 0);
                view.setPadding(0, 0, 0, Dp2Px2SpUtil.dp2px(mContext, 8));
                view.setLayoutParams(layoutParams);
                if (graphicNavRecyclerAdapterArr == null) {
                    graphicNavRecyclerAdapterArr = new SparseArray<>();
                }
                return new GraphicNavViewHolder(mContext, view);
            case DecorateEntityType.SEARCH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search, null);
                layoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, 20),
                        Dp2Px2SpUtil.dp2px(mContext, 28) + StatusBarUtil.getStatusBarHeight(mContext),
                        Dp2Px2SpUtil.dp2px(mContext, 20), 0);
                view.setLayoutParams(layoutParams);
                return new SearchViewHolder(mContext, view);
            case BASE_ITEM_TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.common_bottom_layout, null);
                return new BottomLineViewHolder(mContext, view);
            default: // 若出现不兼容类型，返回一个空的 view
                view = new TextView(mContext);
                view.setVisibility(View.GONE);
                return new BaseViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int currentBindPos = holder.getAdapterPosition();
        if (currentBindPos == mComponentList.size()) { // 最后一个 item
            ((BottomLineViewHolder) holder).initViewHolder(isShowLastItem);
            return;
        }
        int itemType = getItemViewType(currentBindPos);
        final ComponentInfo currentBindComponent = mComponentList.get(currentBindPos);
        if (itemType == -1) {
            Log.d(TAG, "onBindViewHolder: error return -1");
            return;
        }
        switch(itemType) {
            case DecorateEntityType.FLOW_INFO:
                FlowInfoViewHolder flowInfoViewHolder = (FlowInfoViewHolder) holder;
                flowInfoViewHolder.initViewHolder(currentBindComponent, currentBindPos, flowInfoRecyclerAdapterArr);
                break;
            case DecorateEntityType.RECENT_UPDATE:
                RecentUpdateViewHolder recentUpdateViewHolder = (RecentUpdateViewHolder) holder;
                recentUpdateViewHolder.initViewHolder(currentBindComponent, currentBindPos, recentUpdateListAdapterArr);
                break;
            case DecorateEntityType.KNOWLEDGE_LIST:
                KnowledgeListViewHolder knowledgeListViewHolder = (KnowledgeListViewHolder) holder;
                knowledgeListViewHolder.setSearch(isSearch);
                knowledgeListViewHolder.initViewHolder(currentBindComponent, currentBindPos, knowledgeListAdapterArr);
                break;
            case DecorateEntityType.KNOWLEDGE_GROUP:
                KnowledgeGroupViewHolder knowledgeGroupViewHolder = (KnowledgeGroupViewHolder) holder;
                knowledgeGroupViewHolder.setKnowledgeGroupRecyclerList(knowledgeGroupRecyclerList);
                knowledgeGroupViewHolder.initViewHolder(currentBindComponent, currentBindPos, knowledgeGroupRecyclerAdapterArr);
                break;
            case DecorateEntityType.SHUFFLING_FIGURE:
                ShufflingFigureViewHolder shufflingFigureViewHolder = (ShufflingFigureViewHolder) holder;
                shufflingFigureViewHolder.initViewHolder(currentBindComponent, currentBindPos, bannerArr);
                break;
            case DecorateEntityType.GRAPHIC_NAVIGATION:
                GraphicNavViewHolder graphicNavViewHolder = (GraphicNavViewHolder) holder;
                graphicNavViewHolder.initViewHolder(currentBindComponent, currentBindPos, graphicNavRecyclerAdapterArr);
                break;
            case DecorateEntityType.SEARCH: // 搜索组件不需要做重复判断
                SearchViewHolder searchViewHolder = (SearchViewHolder) holder;
                searchViewHolder.initViewHolder(currentBindComponent);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mComponentList == null ? 0 : mComponentList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mComponentList.size()) { // 表示最后一个元素
            return BASE_ITEM_TYPE_FOOTER;
        }
        // 当前 item
        ComponentInfo currentComponent = mComponentList.get(position);
        String type = currentComponent.getType();
        String subType = currentComponent.getSubType();
        switch (type) {
            case DecorateEntityType.FLOW_INFO_STR:
                return DecorateEntityType.FLOW_INFO;
            case DecorateEntityType.RECENT_UPDATE_STR:
                return DecorateEntityType.RECENT_UPDATE;
            case DecorateEntityType.KNOWLEDGE_COMMODITY_STR:
                if (DecorateEntityType.KNOWLEDGE_LIST_STR.equals(subType)) {
                    return DecorateEntityType.KNOWLEDGE_LIST;
                } else if (DecorateEntityType.KNOWLEDGE_GROUP_STR.equals(subType)) {
                    return DecorateEntityType.KNOWLEDGE_GROUP;
                } else {
                    return -1;
                }
            case DecorateEntityType.SHUFFLING_FIGURE_STR:
                return DecorateEntityType.SHUFFLING_FIGURE;
            case DecorateEntityType.GRAPHIC_NAVIGATION_STR:
                return DecorateEntityType.GRAPHIC_NAVIGATION;
            case DecorateEntityType.SEARCH_STR:
                return DecorateEntityType.SEARCH;
            default:
                return -1;
        }
    }
    public void notifyDataSetChangedRecentUpdate(){
        if(recentUpdateListAdapterArr == null){
            return;
        }
        for (int i = 0; i < recentUpdateListAdapterArr.size(); i++) {
            int key = recentUpdateListAdapterArr.keyAt(i);
            RecentUpdateListAdapter recentUpdateListAdapter = recentUpdateListAdapterArr.get(key);
            // list 需要刷新
            recentUpdateListAdapter.notifyDataSetChanged();
            // title 也需要刷新
            notifyItemChanged(key);
        }
    }

    public void showLastItem(boolean isShow) {
        isShowLastItem = isShow;
    }
}
