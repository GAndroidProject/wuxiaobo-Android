package com.xiaoe.shop.wxb.adapter.decorate;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.GraphicNavItem;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.common.entitys.ShufflingItem;
import com.xiaoe.common.interfaces.OnItemClickWithKnowledgeListener;
import com.xiaoe.common.interfaces.OnItemClickWithNavItemListener;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.MeasureUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.decorate.flow_info.FlowInfoRecyclerAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.flow_info.FlowInfoViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation.GraphicNavItemDecoration;
import com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation.GraphicNavRecyclerAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation.GraphicNavViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeGroupRecyclerAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeGroupRecyclerItemDecoration;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeGroupViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeListAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity.KnowledgeListViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.recent_update.RecentUpdateListAdapter;
import com.xiaoe.shop.wxb.adapter.decorate.recent_update.RecentUpdateViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.search.SearchViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.shuffling_figure.ShufflingFigureViewHolder;
import com.xiaoe.shop.wxb.adapter.decorate.shuffling_figure.ShufflingImageLoader;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.business.course_more.ui.CourseMoreActivity;
import com.xiaoe.shop.wxb.business.mine_learning.ui.MineLearningActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;
import com.xiaoe.shop.wxb.widget.TouristDialog;

/**
 * 店铺装修组件显示列表适配器
 */
public class DecorateRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> implements OnItemClickWithKnowledgeListener, OnItemClickWithNavItemListener {

    private static final String TAG = "DecorateRecyclerAdapter";

    private Context mContext;
    // 组件实体类列表
    private List<ComponentInfo> mComponentList;

    // 知识商品分组形式的 recycler
    private List<RecyclerView> knowledgeGroupRecyclerList;
    private RecentUpdateListAdapter recentUpdateListAdapter;

    public DecorateRecyclerAdapter(Context context, List<ComponentInfo> componentList) {
        this.mContext = context;
        this.mComponentList = componentList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (viewType == -1) {
            return null;
        }
        View view;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
            case DecorateEntityType.KNOWLEDGE_LIST:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_list, null);
                // layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 18), 0, 0);
                // view.setLayoutParams(layoutParams);
                return new KnowledgeListViewHolder(view);
            case DecorateEntityType.KNOWLEDGE_GROUP:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_group, null);
                layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(mContext, 24), 0, Dp2Px2SpUtil.dp2px(mContext, 24));
                view.setLayoutParams(layoutParams);
                knowledgeGroupRecyclerList = new ArrayList<>();
                return new KnowledgeGroupViewHolder(view);
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
        int currentBindPos = holder.getAdapterPosition();
        int itemType = getItemViewType(currentBindPos);
        final ComponentInfo currentBindComponent = mComponentList.get(currentBindPos);
        if (itemType == -1) {
            Log.d(TAG, "onBindViewHolder: error return -1");
            return;
        }
        switch(itemType) {
            case DecorateEntityType.FLOW_INFO:
                FlowInfoViewHolder flowInfoViewHolder = (FlowInfoViewHolder) holder;
                flowInfoViewHolder.flowInfoTitle.setText(currentBindComponent.getTitle());
                if (currentBindComponent.getImgUrl() != null) {
                    SetImageUriUtil.setImgURI(flowInfoViewHolder.flowInfoIcon, currentBindComponent.getImgUrl(), Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 20));
                }
                flowInfoViewHolder.flowInfoDesc.setText(currentBindComponent.getDesc());
                flowInfoViewHolder.flowInfoIconDesc.setText(currentBindComponent.getJoinedDesc());
                LinearLayoutManager llm = new LinearLayoutManager(mContext);
                llm.setOrientation(LinearLayout.VERTICAL);
                flowInfoViewHolder.flowInfoRecycler.setLayoutManager(llm);
                FlowInfoRecyclerAdapter flowInfoRecyclerAdapter = new FlowInfoRecyclerAdapter(mContext, currentBindComponent.getFlowInfoItemList());
                // recyclerView 取消滑动
                flowInfoViewHolder.flowInfoRecycler.setNestedScrollingEnabled(false);
                flowInfoViewHolder.flowInfoRecycler.setAdapter(flowInfoRecyclerAdapter);
                flowInfoViewHolder.flowInfoLearnWrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentBindComponent.isFormUser()) {
                            Intent intent = new Intent(mContext, MineLearningActivity.class);
                            intent.putExtra("pageTitle", "我正在学");
                            mContext.startActivity(intent);
                        } else {
                            showTouristDialog();
                        }
                    }
                });
                break;
            case DecorateEntityType.RECENT_UPDATE:
                RecentUpdateViewHolder recentUpdateViewHolder = (RecentUpdateViewHolder) holder;
                SetImageUriUtil.setImgURI(recentUpdateViewHolder.recentUpdateAvatar, currentBindComponent.getImgUrl(), Dp2Px2SpUtil.dp2px(mContext, 72), Dp2Px2SpUtil.dp2px(mContext, 72));
                recentUpdateViewHolder.recentUpdateSubTitle.setText(currentBindComponent.getTitle());
                recentUpdateViewHolder.recentUpdateSubDesc.setText(currentBindComponent.getDesc());
                if (currentBindComponent.isHideTitle()) { // 隐藏收听全部按钮
                    recentUpdateViewHolder.recentUpdateSubBtn.setVisibility(View.GONE);
                } else {
                    recentUpdateViewHolder.recentUpdateSubBtn.setVisibility(View.VISIBLE);
                    recentUpdateViewHolder.recentUpdateSubBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (currentBindComponent.isFormUser()) {
                                if(!currentBindComponent.isHasBuy()){
                                    Toast.makeText(mContext, "未购买课程", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (!recentUpdateListAdapter.isPlaying) {
                                    recentUpdateViewHolder.recentUpdateSubBtn.setText("暂停全部");
                                    Drawable drawable = mContext.getResources().getDrawable(R.mipmap.class_stopall);
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                    recentUpdateViewHolder.recentUpdateSubBtn.setCompoundDrawables(drawable, null, null, null);
                                    recentUpdateViewHolder.recentUpdateSubBtn.setCompoundDrawablePadding(Dp2Px2SpUtil.dp2px(mContext, 6));
                                    recentUpdateListAdapter.clickPlayAll();
                                } else {
                                    recentUpdateViewHolder.recentUpdateSubBtn.setText("播放全部");
                                    Drawable drawable = mContext.getResources().getDrawable(R.mipmap.class_playall);
                                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                                    recentUpdateViewHolder.recentUpdateSubBtn.setCompoundDrawables(drawable, null, null, null);
                                    recentUpdateViewHolder.recentUpdateSubBtn.setCompoundDrawablePadding(Dp2Px2SpUtil.dp2px(mContext, 6));
                                    recentUpdateListAdapter.stopPlayAll();
                                }
                            } else {
                                showTouristDialog();
                            }
                        }
                    });
                }
                recentUpdateViewHolder.recentUpdateAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpDetail.jumpColumn(mContext, currentBindComponent.getColumnId(), currentBindComponent.getImgUrl(), false);
                    }
                });
                // 加载 ListView 的数据
                recentUpdateListAdapter = new RecentUpdateListAdapter(mContext, currentBindComponent.getSubList(), currentBindComponent.isHasBuy());
                recentUpdateViewHolder.recentUpdateListView.setAdapter(recentUpdateListAdapter);
                MeasureUtil.setListViewHeightBasedOnChildren(recentUpdateViewHolder.recentUpdateListView);
                break;
                case DecorateEntityType.KNOWLEDGE_LIST:
                    KnowledgeListViewHolder knowledgeListViewHolder = (KnowledgeListViewHolder) holder;
                    KnowledgeListAdapter knowledgeListAdapter = new KnowledgeListAdapter(mContext, currentBindComponent.getKnowledgeCommodityItemList());
                    if (currentBindComponent.isHideTitle()) {
                        knowledgeListViewHolder.knowledgeListTitle.setVisibility(View.GONE);
                        // title 没有了需要讲 listView 的 marginTop 设置为 0
                        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, 0, 0);
                        knowledgeListViewHolder.knowledgeListView.setLayoutParams(layoutParams);
                    } else {
                        knowledgeListViewHolder.knowledgeListTitle.setText(currentBindComponent.getTitle());
                    }
                    knowledgeListViewHolder.knowledgeListView.setAdapter(knowledgeListAdapter);
                    MeasureUtil.setListViewHeightBasedOnChildren(knowledgeListViewHolder.knowledgeListView);
                    break;
                case DecorateEntityType.KNOWLEDGE_GROUP:
                    KnowledgeGroupViewHolder knowledgeGroupViewHolder = (KnowledgeGroupViewHolder) holder;
                    if (currentBindComponent.isHideTitle()) {
                        knowledgeGroupViewHolder.groupTitle.setVisibility(View.GONE);
                        knowledgeGroupViewHolder.groupMore.setVisibility(View.GONE);
                    } else {
                        knowledgeGroupViewHolder.groupTitle.setText(currentBindComponent.getTitle());
                        knowledgeGroupViewHolder.groupMore.setText(currentBindComponent.getDesc());
                        knowledgeGroupViewHolder.groupMore.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 跳转到更多课程的页面
                                String groupId = currentBindComponent.getGroupId();
                                Intent intent = new Intent(mContext, CourseMoreActivity.class);
                                intent.putExtra("groupId", groupId);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                    GridLayoutManager lm = new GridLayoutManager(mContext, 2);
                    if (!knowledgeGroupRecyclerList.contains(knowledgeGroupViewHolder.groupRecyclerView)) { // 防止重复添加
                        knowledgeGroupRecyclerList.add(knowledgeGroupViewHolder.groupRecyclerView);
                    }
                    knowledgeGroupViewHolder.groupRecyclerView.setLayoutManager(lm);
                    // recyclerView 取消滑动
                    knowledgeGroupViewHolder.groupRecyclerView.setNestedScrollingEnabled(false);
                    if (currentBindComponent.isNeedDecorate()) {
                        knowledgeGroupViewHolder.groupRecyclerView.addItemDecoration(new KnowledgeGroupRecyclerItemDecoration(Dp2Px2SpUtil.dp2px(mContext, 16), 2));
                        currentBindComponent.setNeedDecorate(false);
                    }
                    KnowledgeGroupRecyclerAdapter groupAdapter = new KnowledgeGroupRecyclerAdapter(mContext, currentBindComponent.getKnowledgeCommodityItemList());
                    groupAdapter.setOnItemClickWithKnowledgeListener(this);
                    knowledgeGroupViewHolder.groupRecyclerView.setAdapter(groupAdapter);
                    break;
            case DecorateEntityType.SHUFFLING_FIGURE:
                final List<ShufflingItem> shufflingList = currentBindComponent.getShufflingList();
                ShufflingFigureViewHolder shufflingFigureViewHolder = (ShufflingFigureViewHolder) holder;
                List<String> imgList = new ArrayList<>();
                for (ShufflingItem item : shufflingList) {
                    imgList.add(item.getImgUrl());
                }
                shufflingFigureViewHolder.banner.setImages(imgList)
                        .setImageLoader(new ShufflingImageLoader())
                        .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                        .setDelayTime(1500)
                        .start();
                shufflingFigureViewHolder.banner.setOnBannerListener(position1 -> {
                    ShufflingItem shufflingItem = shufflingList.get(position1);
                    String resourceType = shufflingItem.getSrcType();
                    String resourceId = shufflingItem.getSrcId();
                    switch (resourceType) {
                        case DecorateEntityType.IMAGE_TEXT:
                            JumpDetail.jumpImageText(mContext, resourceId, "");
                            break;
                        case DecorateEntityType.AUDIO:
                            JumpDetail.jumpImageText(mContext, resourceId, "");
                            break;
                        case DecorateEntityType.VIDEO:
                            JumpDetail.jumpVideo(mContext, resourceId, "", false);
                            break;
                        case DecorateEntityType.COLUMN:
                            JumpDetail.jumpColumn(mContext, resourceId, "", false);
                            break;
                        case DecorateEntityType.TOPIC:
                            JumpDetail.jumpColumn(mContext, resourceId, "", true);
                            break;
                        case "message": // 消息页面 TODO: 跳转到消息页面
                            break;
                        case "my_purchase": // 已购
                            JumpDetail.jumpMineLearning(mContext, "我正在学");
                            break;
                        case "custom": // 外部链接 TODO: 跳转到外部链接
                            break;
                        case "micro_page": // 微页面 TODO: 跳转到微页面
                            break;
                        default:
                            Log.d(TAG, "OnBannerClick: 未知链接");
                            break;
                    }
                });
                break;
            case DecorateEntityType.BOOKCASE:
                break;
            case DecorateEntityType.GRAPHIC_NAVIGATION:
                GraphicNavViewHolder graphicNavViewHolder = (GraphicNavViewHolder) holder;
                GridLayoutManager graphicNavGlm = new GridLayoutManager(mContext, currentBindComponent.getGraphicNavItemList().size());
                GraphicNavRecyclerAdapter graphicNavRecyclerAdapter = new GraphicNavRecyclerAdapter(mContext, currentBindComponent.getGraphicNavItemList());
                graphicNavViewHolder.graphicNavRecycler.setLayoutManager(graphicNavGlm);
                if (currentBindComponent.isNeedDecorate()) {
                    graphicNavViewHolder.graphicNavRecycler.addItemDecoration(new GraphicNavItemDecoration(mContext));
                    currentBindComponent.setNeedDecorate(false);
                }
                graphicNavViewHolder.graphicNavRecycler.setNestedScrollingEnabled(false);
                graphicNavRecyclerAdapter.setOnItemClickWithNavItemListener(this);
                graphicNavViewHolder.graphicNavRecycler.setAdapter(graphicNavRecyclerAdapter);
                break;
            case DecorateEntityType.SEARCH:
                SearchViewHolder searchViewHolder = (SearchViewHolder) holder;
                searchViewHolder.searchTitle.setText(currentBindComponent.getTitle());
                String searchDefault = "res:///" + R.mipmap.search_grey_search;
                SetImageUriUtil.setImgURI(searchViewHolder.searchIcon, searchDefault, Dp2Px2SpUtil.dp2px(mContext, 375), Dp2Px2SpUtil.dp2px(mContext, 250));
                if (!currentBindComponent.isNeedDecorate()) {
                    searchViewHolder.searchWxb.setVisibility(View.GONE);
                }
                searchViewHolder.searchIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpDetail.jumpSearch(mContext);
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
        // 知识商品分组形式
        for (RecyclerView itemRecycler : knowledgeGroupRecyclerList) {
            Log.d(TAG, "onKnowledgeItemClick: execute");
            if (view.getParent() == itemRecycler) {
                switch (knowledgeCommodityItem.getSrcType()) {
                    case DecorateEntityType.IMAGE_TEXT: // 图文 -- resourceType 为 1，resourceId 需要取
                        JumpDetail.jumpImageText(mContext, knowledgeCommodityItem.getResourceId(), knowledgeCommodityItem.getItemImg());
                        break;
                    case DecorateEntityType.AUDIO: // 音频
                        JumpDetail.jumpAudio(mContext, knowledgeCommodityItem.getResourceId(), 0);
                        break;
                    case DecorateEntityType.VIDEO: // 视频
                        JumpDetail.jumpVideo(mContext, knowledgeCommodityItem.getResourceId(), "", false);
                        break;
                    case DecorateEntityType.COLUMN: // 专栏
                        JumpDetail.jumpColumn(mContext, knowledgeCommodityItem.getResourceId(), knowledgeCommodityItem.getItemImg(), false);
                        break;
                    case DecorateEntityType.TOPIC: // 大专栏
                        JumpDetail.jumpColumn(mContext, knowledgeCommodityItem.getResourceId(), knowledgeCommodityItem.getItemImg(), true);
                        break;
                }
            }
        }
    }

    @Override
    public void onNavItemClick(View view, GraphicNavItem graphicNavItem) {
        String pageTitle = graphicNavItem.getNavContent();
        String resourceId = graphicNavItem.getNavResourceId();
        String resourceType = graphicNavItem.getNavResourceType();
        switch (resourceType) {
            case DecorateEntityType.IMAGE_TEXT: // 图文
                JumpDetail.jumpImageText(mContext, resourceId, "");
                break;
            case DecorateEntityType.AUDIO: // 音频
                JumpDetail.jumpAudio(mContext, resourceId, 0);
                break;
            case DecorateEntityType.VIDEO: // 视频
                break;
            case DecorateEntityType.COLUMN: // 专栏
                JumpDetail.jumpColumn(mContext, resourceId, "", false);
                break;
            case DecorateEntityType.TOPIC: // 大专栏
                JumpDetail.jumpColumn(mContext, resourceId, "", true);
                break;
            case DecorateEntityType.RESOURCE_TAG: // 商品分组
                JumpDetail.jumpShopGroup(mContext, pageTitle, graphicNavItem.getNavResourceId());
                break;
        }
    }
    public void notifyDataSetChangedRecentUpdate(){
        if(recentUpdateListAdapter != null){
            recentUpdateListAdapter.notifyDataSetChanged();
        }
    }

    private void showTouristDialog() {
        final TouristDialog touristDialog = new TouristDialog(mContext);
        touristDialog.setDialogCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touristDialog.dismissDialog();
            }
        });
        touristDialog.setDialogConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touristDialog.dismissDialog();
                JumpDetail.jumpLogin(mContext, true);
            }
        });
        touristDialog.showDialog();
    }
}
