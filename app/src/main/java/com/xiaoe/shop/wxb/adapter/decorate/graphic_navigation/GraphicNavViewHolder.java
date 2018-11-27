package com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.GraphicNavItem;
import com.xiaoe.common.interfaces.OnItemClickWithNavItemListener;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.common.JumpDetail;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GraphicNavViewHolder extends BaseViewHolder implements OnItemClickWithNavItemListener {

    Context mContext;

    @BindView(R.id.graphic_nav_recycler)
    public RecyclerView graphicNavRecycler;

    public GraphicNavViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public GraphicNavViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void initViewHolder(ComponentInfo currentBindComponent, int currentBindPos, SparseArray<GraphicNavRecyclerAdapter> graphicNavRecyclerAdapterArr) {
        GraphicNavRecyclerAdapter graphicNavRecyclerAdapter;
        if (graphicNavRecycler.getLayoutManager() == null) {
            GridLayoutManager graphicNavGlm = new GridLayoutManager(mContext, currentBindComponent.getGraphicNavItemList().size());
            graphicNavRecycler.setLayoutManager(graphicNavGlm);
        }
        if (graphicNavRecyclerAdapterArr.get(currentBindPos) == null) {
            graphicNavRecyclerAdapter = new GraphicNavRecyclerAdapter(mContext, currentBindComponent.getGraphicNavItemList());
            graphicNavRecyclerAdapterArr.put(currentBindPos, graphicNavRecyclerAdapter);
        } else {
            graphicNavRecyclerAdapter = graphicNavRecyclerAdapterArr.get(currentBindPos);
        }
        if (currentBindComponent.isNeedDecorate()) {
            graphicNavRecycler.addItemDecoration(new GraphicNavItemDecoration(mContext));
            currentBindComponent.setNeedDecorate(false);
        }
        graphicNavRecycler.setNestedScrollingEnabled(false);
        graphicNavRecyclerAdapter.setOnItemClickWithNavItemListener(this);
        graphicNavRecycler.setAdapter(graphicNavRecyclerAdapter);
    }

    @Override
    public void onNavItemClick(View view, GraphicNavItem graphicNavItem) {
        String pageTitle = graphicNavItem.getNavContent();
        String resourceId = graphicNavItem.getNavResourceId();
        String resourceType = graphicNavItem.getNavResourceType();
        switch (resourceType) {
            case DecorateEntityType.IMAGE_TEXT: // 图文
                JumpDetail.jumpImageText(mContext, resourceId, "", "");
                break;
            case DecorateEntityType.AUDIO: // 音频
                JumpDetail.jumpAudio(mContext, resourceId, 0);
                break;
            case DecorateEntityType.VIDEO: // 视频
                break;
            case DecorateEntityType.COLUMN: // 专栏
                JumpDetail.jumpColumn(mContext, resourceId, "", 6);
                break;
            case DecorateEntityType.TOPIC: // 大专栏
                JumpDetail.jumpColumn(mContext, resourceId, "", 8);
                break;
            case DecorateEntityType.MEMBER: // 会员
                JumpDetail.jumpColumn(mContext, resourceId, "", 5);
                break;
            case DecorateEntityType.RESOURCE_TAG: // 商品分组
                JumpDetail.jumpShopGroup(mContext, pageTitle, graphicNavItem.getNavResourceId());
                break;
        }
    }
}
