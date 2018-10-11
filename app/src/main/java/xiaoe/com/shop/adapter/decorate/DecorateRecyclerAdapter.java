package xiaoe.com.shop.adapter.decorate;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.youth.banner.BannerConfig;

import java.util.List;

import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.flow_info.FlowInfoRecyclerAdapter;
import xiaoe.com.shop.adapter.decorate.flow_info.FlowInfoViewHolder;
import xiaoe.com.shop.adapter.decorate.graphic_navigation.GraphicNavItemDecoration;
import xiaoe.com.shop.adapter.decorate.graphic_navigation.GraphicNavRecyclerAdapter;
import xiaoe.com.shop.adapter.decorate.graphic_navigation.GraphicNavViewHolder;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeGroupRecyclerAdapter;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeGroupRecyclerItemDecoration;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeGroupViewHolder;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeListAdapter;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeListViewHolder;
import xiaoe.com.shop.adapter.decorate.recent_update.RecentUpdateListAdapter;
import xiaoe.com.shop.adapter.decorate.recent_update.RecentUpdateViewHolder;
import xiaoe.com.shop.adapter.decorate.search.SearchViewHolder;
import xiaoe.com.shop.adapter.decorate.shuffling_figure.ShufflingFigureViewHolder;
import xiaoe.com.shop.adapter.decorate.shuffling_figure.ShufflingImageLoader;
import xiaoe.com.shop.base.BaseViewHolder;

/**
 * 店铺装修组件显示列表适配器
 */
public class DecorateRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "DecorateRecyclerAdapter";

    private Context mContext;
    // 组件实体类列表
    private List<ComponentInfo> mComponentList;
    // 当前下标
    private int currentPos;
    // 当前 item
    private ComponentInfo currentComponent;

    public DecorateRecyclerAdapter(Context context, List<ComponentInfo> componentList) {
        this.mContext = context;
        this.mComponentList = componentList;
    }

    public DecorateRecyclerAdapter(Activity activity, List<ComponentInfo> componentList) {
        this.mContext = activity;
        this.mComponentList = componentList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (viewType == -1) {
            return null;
        }
        View view = null;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,Dp2Px2SpUtil.dp2px(mContext, 30));
        currentComponent = mComponentList.get(currentPos);
        // 获取组件的显示类型
        String subType = currentComponent.getSubType();
        switch(viewType) {
            case DecorateEntityType.FLOW_INFO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoViewHolder(view);
            case DecorateEntityType.RECENT_UPDATE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_update, null);
                view.setLayoutParams(layoutParams);
                return new RecentUpdateViewHolder(view);
            case DecorateEntityType.KNOWLEDGE_COMMODITY:
                switch (subType) {
                    case DecorateEntityType.KNOWLEDGE_LIST:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_list, null);
                        layoutParams.setMargins(0, 0, 0, 0);
                        view.setLayoutParams(layoutParams);
                        return new KnowledgeListViewHolder(view);
                    case DecorateEntityType.KNOWLEDGE_GROUP:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_group, null);
                        view.setLayoutParams(layoutParams);
                        return new KnowledgeGroupViewHolder(view);
                }
            case DecorateEntityType.SHUFFLING_FIGURE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shuffling_figure, null);
                view.setLayoutParams(layoutParams);
                return new ShufflingFigureViewHolder(view);
            case DecorateEntityType.BOOKCASE: // 书架的 case 本次不做
                return null;
            case DecorateEntityType.GRAPHIC_NAVIGATION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphic_navigation, null);
                layoutParams.gravity = Gravity.CENTER;
                view.setLayoutParams(layoutParams);
                return new GraphicNavViewHolder(view);
            case DecorateEntityType.SEARCH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search, null);
                layoutParams.setMargins(0, 0, 0, Dp2Px2SpUtil.dp2px(mContext, 20));
                view.setLayoutParams(layoutParams);
                return new SearchViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == -1) {
            Log.d(TAG, "onBindViewHolder: error return -1");
            return;
        }
        String subType = currentComponent.getSubType();
        switch(itemType) {
            case DecorateEntityType.FLOW_INFO:
                FlowInfoViewHolder flowInfoViewHolder = (FlowInfoViewHolder) holder;
                flowInfoViewHolder.flowInfoTitle.setText(currentComponent.getTitle());
                flowInfoViewHolder.flowInfoDesc.setText(currentComponent.getDesc());
                flowInfoViewHolder.flowInfoIcon.setImageURI(currentComponent.getImgUrl());
                flowInfoViewHolder.flowInfoIconDesc.setText(currentComponent.getJoinedDesc());
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayout.VERTICAL);
                flowInfoViewHolder.flowInfoRecycler.setLayoutManager(llm);
                FlowInfoRecyclerAdapter flowInfoRecyclerAdapter = new FlowInfoRecyclerAdapter(mContext, currentComponent.getFlowInfoItemList());
                // recyclerView 取消滑动
                flowInfoViewHolder.flowInfoRecycler.setNestedScrollingEnabled(false);
                flowInfoViewHolder.flowInfoRecycler.setAdapter(flowInfoRecyclerAdapter);
                break;
            case DecorateEntityType.RECENT_UPDATE:
                RecentUpdateViewHolder recentUpdateViewHolder = (RecentUpdateViewHolder) holder;
                recentUpdateViewHolder.recentUpdateAvatar.setImageURI("res:///" + R.mipmap.detail_disk);
                recentUpdateViewHolder.recentUpdateSubTitle.setText(currentComponent.getTitle());
                recentUpdateViewHolder.recentUpdateSubDesc.setText(currentComponent.getDesc());
                recentUpdateViewHolder.recentUpdateSubBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Toast.makeText(mContext, "全部播放...", Toast.LENGTH_SHORT).show();
                    }
                });
                // 加载 ListView 的数据
                RecentUpdateListAdapter adapter = new RecentUpdateListAdapter(mContext, currentComponent.getSubList());
                recentUpdateViewHolder.recentUpdateListView.setAdapter(adapter);
                setListViewHeightBasedOnChildren(recentUpdateViewHolder.recentUpdateListView);
                break;
            case DecorateEntityType.KNOWLEDGE_COMMODITY:
                switch (subType) {
                    case DecorateEntityType.KNOWLEDGE_LIST:
                        KnowledgeListViewHolder knowledgeListViewHolder = (KnowledgeListViewHolder) holder;
                        KnowledgeListAdapter knowledgeListAdapter = new KnowledgeListAdapter(mContext, currentComponent.getKnowledgeCommodityItemList());
                        knowledgeListViewHolder.knowledgeListView.setAdapter(knowledgeListAdapter);
                        setListViewHeightBasedOnChildren(knowledgeListViewHolder.knowledgeListView);
                        break;
                    case DecorateEntityType.KNOWLEDGE_GROUP:
                        KnowledgeGroupViewHolder knowledgeGroupViewHolder = (KnowledgeGroupViewHolder) holder;
                        knowledgeGroupViewHolder.groupTitle.setText(currentComponent.getTitle());
                        knowledgeGroupViewHolder.groupMore.setText(currentComponent.getDesc());
                        GridLayoutManager lm = new GridLayoutManager(mContext, 2);
                        knowledgeGroupViewHolder.groupRecyclerView.setLayoutManager(lm);
                        // recyclerView 取消滑动
                        knowledgeGroupViewHolder.groupRecyclerView.setNestedScrollingEnabled(false);
                        if (currentComponent.isNeedDecorate()) {
                            knowledgeGroupViewHolder.groupRecyclerView.addItemDecoration(new KnowledgeGroupRecyclerItemDecoration(15, 2));
                            currentComponent.setNeedDecorate(false);
                        }
                        KnowledgeGroupRecyclerAdapter groupAdapter = new KnowledgeGroupRecyclerAdapter(mContext, currentComponent.getKnowledgeCommodityItemList());
                        knowledgeGroupViewHolder.groupRecyclerView.setAdapter(groupAdapter);
                        knowledgeGroupViewHolder.groupMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(mContext, "查看更多", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
                break;
            case DecorateEntityType.SHUFFLING_FIGURE:
                List<String> shufflingList = currentComponent.getShufflingList();
                ShufflingFigureViewHolder shufflingFigureViewHolder = (ShufflingFigureViewHolder) holder;
                shufflingFigureViewHolder.banner.setImages(shufflingList).setImageLoader(new ShufflingImageLoader()).setBannerStyle(BannerConfig.NOT_INDICATOR).start();
                break;
            case DecorateEntityType.BOOKCASE:
                break;
            case DecorateEntityType.GRAPHIC_NAVIGATION:
                GraphicNavViewHolder graphicNavViewHolder = (GraphicNavViewHolder) holder;
                GridLayoutManager graphicNavGlm = new GridLayoutManager(mContext, currentComponent.getGraphicNavItemList().size());
                GraphicNavRecyclerAdapter graphicNavRecyclerAdapter = new GraphicNavRecyclerAdapter(mContext, currentComponent.getGraphicNavItemList());
                graphicNavViewHolder.graphicNavRecycler.setLayoutManager(graphicNavGlm);
                if (currentComponent.isNeedDecorate()) {
                    graphicNavViewHolder.graphicNavRecycler.addItemDecoration(new GraphicNavItemDecoration(mContext));
                    currentComponent.setNeedDecorate(false);
                }
                graphicNavViewHolder.graphicNavRecycler.setNestedScrollingEnabled(false);
                graphicNavViewHolder.graphicNavRecycler.setAdapter(graphicNavRecyclerAdapter);
                break;
            case DecorateEntityType.SEARCH:
                SearchViewHolder searchViewHolder = (SearchViewHolder) holder;
                searchViewHolder.searchTitle.setText(currentComponent.getTitle());
                searchViewHolder.searchIcon.setImageURI("res:///" + R.mipmap.search_grey_search);
                if (!currentComponent.isNeedDecorate()) {
                    searchViewHolder.searchWxb.setVisibility(View.GONE);
                }
                searchViewHolder.searchIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Toast.makeText(mContext, "点击了搜索...", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 根据 ListView 的子项计算高度
     * @param recentUpdateSubList 需要计算的 ListView
     */
    private void setListViewHeightBasedOnChildren(ListView recentUpdateSubList) {
        // 获取 ListView 对应的 Adapter
        ListAdapter adapter = recentUpdateSubList.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for(int i = 0, len = adapter.getCount(); i < len; i++) {
            // 拿到每个 View
            View listItem = adapter.getView(i, null, recentUpdateSubList);
            // 计算宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = recentUpdateSubList.getLayoutParams();
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        params.height = totalHeight + (recentUpdateSubList.getDividerHeight() * (adapter.getCount() - 1));
        recentUpdateSubList.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return mComponentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentPos = position;
        currentComponent = mComponentList.get(position);
        String type = currentComponent.getType();
        switch (type) {
            case DecorateEntityType.FLOW_INFO_STR:
                return DecorateEntityType.FLOW_INFO;
            case DecorateEntityType.RECENT_UPDATE_STR:
                return DecorateEntityType.RECENT_UPDATE;
            case DecorateEntityType.KNOWLEDGE_COMMODITY_STR:
                return DecorateEntityType.KNOWLEDGE_COMMODITY;
            case DecorateEntityType.SHUFFLING_FIGURE_STR:
                return DecorateEntityType.SHUFFLING_FIGURE;
            case DecorateEntityType.BOOKCASE_STR:
                return DecorateEntityType.BOOKCASE;
            case DecorateEntityType.GRAPHIC_NAVIGATION_STR:
                return DecorateEntityType.GRAPHIC_NAVIGATION;
            case DecorateEntityType.SEARCH_STR:
                return DecorateEntityType.SEARCH;
            default:
                return -1;
        }
    }
}
