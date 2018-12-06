package com.xiaoe.shop.wxb.adapter.decorate.graphic_navigation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

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
        String jumpUrl = graphicNavItem.getNavJumpUrl();
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
            case DecorateEntityType.EXTERNAL_LINKS: // 外部链接
                if (!TextUtils.isEmpty(jumpUrl)) {
                    openWebClient(jumpUrl);
                } else { // 若外部链接为空，默认跳转到咱们的官网
                    openWebClient("https://www.xiaoe-tech.com/");
                }
                break;
        }
    }

    // 打开浏览器选择器
    private void openWebClient(String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(mContext.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(mContext.getPackageManager());
            Log.d("GraphicNavViewHolder", "openWebClient: " + componentName);
            mContext.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(mContext, "没有匹配的程序", Toast.LENGTH_SHORT).show();
        }
    }
}
