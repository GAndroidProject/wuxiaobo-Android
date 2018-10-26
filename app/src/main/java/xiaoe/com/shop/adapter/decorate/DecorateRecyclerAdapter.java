package xiaoe.com.shop.adapter.decorate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.youth.banner.BannerConfig;

import java.util.List;

import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.GraphicNavItem;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.common.interfaces.OnItemClickWithKnowledgeListener;
import xiaoe.com.common.interfaces.OnItemClickWithNavItemListener;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.common.utils.MeasureUtil;
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
import xiaoe.com.shop.business.course.ui.CourseImageTextActivity;
import xiaoe.com.shop.business.column.ui.ColumnActivity;
import xiaoe.com.shop.business.course_more.ui.CourseMoreActivity;
import xiaoe.com.shop.business.mine_learning.ui.MineLearningActivity;
import xiaoe.com.shop.business.navigate_detail.ui.NavigateDetailActivity;
import xiaoe.com.shop.business.search.ui.SearchActivity;
import xiaoe.com.shop.business.video.ui.VideoActivity;

/**
 * 店铺装修组件显示列表适配器
 */
public class DecorateRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> implements OnItemClickWithKnowledgeListener, OnItemClickWithNavItemListener {

    private static final String TAG = "DecorateRecyclerAdapter";

    private Context mContext;
    // 组件实体类列表
    private List<ComponentInfo> mComponentList;
    // 当前下标
    private int currentPos;
    // 当前 item
    private ComponentInfo currentComponent;

    // 知识商品分组形式的 recycler
    RecyclerView knowledgeGroupRecycler;

    public DecorateRecyclerAdapter(Context context, List<ComponentInfo> componentList) {
        this.mContext = context;
        this.mComponentList = componentList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (viewType == -1) {
            return null;
        }
        View view = null;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 30), 0, 0);
        currentComponent = mComponentList.get(currentPos);
        // 获取组件的显示类型
        String subType = currentComponent.getSubType();
        switch(viewType) {
            case DecorateEntityType.FLOW_INFO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info, null);
                layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 28), 0, 0);
                view.setLayoutParams(layoutParams);
                return new FlowInfoViewHolder(view);
            case DecorateEntityType.RECENT_UPDATE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_update, null);
                layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 26), 0, 0);
                view.setLayoutParams(layoutParams);
                return new RecentUpdateViewHolder(view);
            case DecorateEntityType.KNOWLEDGE_COMMODITY:
                switch (subType) {
                    case DecorateEntityType.KNOWLEDGE_LIST:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_list, null);
                        layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 18), 0, 0);
                        view.setLayoutParams(layoutParams);
                        return new KnowledgeListViewHolder(view);
                    case DecorateEntityType.KNOWLEDGE_GROUP:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_group, null);
                        layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 24), 0, Dp2Px2SpUtil.dp2px(mContext, 24));
                        view.setLayoutParams(layoutParams);
                        return new KnowledgeGroupViewHolder(view);
                }
            case DecorateEntityType.SHUFFLING_FIGURE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shuffling_figure, null);
                layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 20), 0, 0);
                view.setLayoutParams(layoutParams);
                return new ShufflingFigureViewHolder(view);
            case DecorateEntityType.BOOKCASE: // 书架的 case 本次不做
                return null;
            case DecorateEntityType.GRAPHIC_NAVIGATION:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphic_navigation, null);
                layoutParams.gravity = Gravity.CENTER;
                layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 24), 0, 0);
                view.setLayoutParams(layoutParams);
                return new GraphicNavViewHolder(view);
            case DecorateEntityType.SEARCH:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search, null);
                layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 28), 0, 0);
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
                flowInfoViewHolder.flowInfoLearnWrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MineLearningActivity.class);
                        intent.putExtra("pageTitle", "我正在学");
                        mContext.startActivity(intent);
                    }
                });
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
                MeasureUtil.setListViewHeightBasedOnChildren(recentUpdateViewHolder.recentUpdateListView);
                break;
            case DecorateEntityType.KNOWLEDGE_COMMODITY:
                switch (subType) {
                    case DecorateEntityType.KNOWLEDGE_LIST:
                        KnowledgeListViewHolder knowledgeListViewHolder = (KnowledgeListViewHolder) holder;
                        KnowledgeListAdapter knowledgeListAdapter = new KnowledgeListAdapter(mContext, currentComponent.getKnowledgeCommodityItemList());
                        if (currentComponent.isHideTitle()) {
                            knowledgeListViewHolder.knowledgeListTitle.setVisibility(View.GONE);
                            // title 没有了需要讲 listView 的 marginTop 设置为 0
                            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.setMargins(0, 0, 0, 0);
                            knowledgeListViewHolder.knowledgeListView.setLayoutParams(layoutParams);
                        } else {
                            knowledgeListViewHolder.knowledgeListTitle.setText(currentComponent.getTitle());
                        }
                        knowledgeListViewHolder.knowledgeListView.setAdapter(knowledgeListAdapter);
                        MeasureUtil.setListViewHeightBasedOnChildren(knowledgeListViewHolder.knowledgeListView);
                        break;
                    case DecorateEntityType.KNOWLEDGE_GROUP:
                        KnowledgeGroupViewHolder knowledgeGroupViewHolder = (KnowledgeGroupViewHolder) holder;
                        if (currentComponent.isHideTitle()) {
                            knowledgeGroupViewHolder.groupTitle.setVisibility(View.GONE);
                            knowledgeGroupViewHolder.groupMore.setVisibility(View.GONE);
                        } else {
                            knowledgeGroupViewHolder.groupTitle.setText(currentComponent.getTitle());
                            knowledgeGroupViewHolder.groupMore.setText(currentComponent.getDesc());
                            knowledgeGroupViewHolder.groupMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // 跳转到更多课程的页面
                                    String groupId = currentComponent.getGroupId();
                                    Intent intent = new Intent(mContext, CourseMoreActivity.class);
                                    intent.putExtra("groupId", groupId);
                                    mContext.startActivity(intent);
                                }
                            });
                        }
                        GridLayoutManager lm = new GridLayoutManager(mContext, 2);
                        knowledgeGroupRecycler = knowledgeGroupViewHolder.groupRecyclerView;
                        knowledgeGroupViewHolder.groupRecyclerView.setLayoutManager(lm);
                        // recyclerView 取消滑动
                        knowledgeGroupViewHolder.groupRecyclerView.setNestedScrollingEnabled(false);
                        if (currentComponent.isNeedDecorate()) {
                            knowledgeGroupViewHolder.groupRecyclerView.addItemDecoration(new KnowledgeGroupRecyclerItemDecoration(Dp2Px2SpUtil.dp2px(mContext, 16), 2));
                            currentComponent.setNeedDecorate(false);
                        }
                        KnowledgeGroupRecyclerAdapter groupAdapter = new KnowledgeGroupRecyclerAdapter(mContext, currentComponent.getKnowledgeCommodityItemList());
                        groupAdapter.setOnItemClickWithKnowledgeListener(this);
                        knowledgeGroupViewHolder.groupRecyclerView.setAdapter(groupAdapter);
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
                graphicNavRecyclerAdapter.setOnItemClickWithNavItemListener(this);
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
                        Intent intent = new Intent(mContext, SearchActivity.class);
                        mContext.startActivity(intent);
                    }
                });
                break;
            default:
                break;
        }
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

    @Override
    public void onKnowledgeItemClick(View view, KnowledgeCommodityItem knowledgeCommodityItem) {
        Intent intent = null;
        // 知识商品分组形式
        if (view.getParent() == knowledgeGroupRecycler) {
            switch (knowledgeCommodityItem.getSrcType()) {
                case DecorateEntityType.IMAGE_TEXT: // 图文 -- resourceType 为 1，resourceId 需要取
                    intent = new Intent(mContext, CourseImageTextActivity.class);
                    intent.putExtra("imgUrl", knowledgeCommodityItem.getItemImg());
                    intent.putExtra("resourceId", knowledgeCommodityItem.getResourceId());
                    mContext.startActivity(intent);
                    break;
                case DecorateEntityType.AUDIO: // 音频
                    break;
                case DecorateEntityType.VIDEO: // 视频
                    intent = new Intent(mContext, VideoActivity.class);
                    mContext.startActivity(intent);
                    break;
                case DecorateEntityType.COLUMN: // 专栏
                    jumpColumn(knowledgeCommodityItem.getResourceId(), false, knowledgeCommodityItem.getItemImg());
                    break;
                case DecorateEntityType.TOPIC: // 大专栏
                    jumpColumn(knowledgeCommodityItem.getResourceId(), true, knowledgeCommodityItem.getItemImg());
                    break;
            }
        }
    }

    @Override
    public void onNavItemClick(View view, GraphicNavItem graphicNavItem) {
        String pageTitle = graphicNavItem.getNavContent();
        Intent intent = null;
        String resourceId = graphicNavItem.getNavResourceId();
        String resourceType = graphicNavItem.getNavResourceType();
        switch (resourceType) {
            case DecorateEntityType.IMAGE_TEXT: // 图文
                intent = new Intent(mContext, CourseImageTextActivity.class);
                intent.putExtra("imgUrl", "");
                intent.putExtra("resourceId", resourceId);
                mContext.startActivity(intent);
                break;
            case DecorateEntityType.AUDIO: // 音频
                break;
            case DecorateEntityType.VIDEO: // 视频
                break;
            case DecorateEntityType.COLUMN: // 专栏
                jumpColumn(resourceId, false, "");
                break;
            case DecorateEntityType.TOPIC: // 大专栏
                jumpColumn(resourceId, true, "");
                break;
            case DecorateEntityType.RESOURCE_TAG: // 商品分组
                intent = new Intent(mContext, NavigateDetailActivity.class);
                intent.putExtra("pageTitle", pageTitle);
                intent.putExtra("resourceId", graphicNavItem.getNavResourceId());
                intent.putExtra("resourceType", graphicNavItem.getNavResourceType());
                mContext.startActivity(intent);
                break;
        }
    }

    private void jumpColumn(String resId, boolean isBigColumn, String imageUrl){
        Intent intent = new Intent(mContext, ColumnActivity.class);
        intent.putExtra("resource_id", resId);
        intent.putExtra("isBigColumn", isBigColumn);
        if(!TextUtils.isEmpty(imageUrl)){
            intent.putExtra("column_image_url", imageUrl);
        }
        mContext.startActivity(intent);
    }
}
