package com.xiaoe.shop.wxb.adapter.decorate.shuffling_figure;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.ShufflingItem;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShufflingFigureViewHolder extends BaseViewHolder {

    private static final String TAG = "ShufflingFigureVh";

    private Context mContext;
    @BindView(R.id.shuffling_figure)
    public Banner banner;

    public ShufflingFigureViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public ShufflingFigureViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void initViewHolder(ComponentInfo currentBindComponent, int currentBindPos, SparseArray<Banner> bannerArr) {
        if (bannerArr.get(currentBindPos) != null) {
            return;
        }
        final List<ShufflingItem> shufflingList = currentBindComponent.getShufflingList();
        List<String> imgList = new ArrayList<>();
        for (ShufflingItem item : shufflingList) {
            imgList.add(item.getImgUrl());
        }
        banner.setImages(imgList)
                .setImageLoader(new GlideImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setDelayTime(1500)
                .start();
        banner.setBackground(mContext.getResources().getDrawable(R.drawable.banner_shadow));
        banner.setOnBannerListener(position1 -> {
            ShufflingItem shufflingItem = shufflingList.get(position1);
            String resourceType = shufflingItem.getSrcType();
            String resourceId = shufflingItem.getSrcId();
            switch (resourceType) {
                case DecorateEntityType.IMAGE_TEXT:
                    JumpDetail.jumpImageText(mContext, resourceId, "", "");
                    break;
                case DecorateEntityType.AUDIO:
                    JumpDetail.jumpAudio(mContext, resourceId, 0);
                    break;
                case DecorateEntityType.VIDEO:
                    JumpDetail.jumpVideo(mContext, resourceId, "", false, "");
                    break;
                case DecorateEntityType.COLUMN:
                    JumpDetail.jumpColumn(mContext, resourceId, "", 6);
                    break;
                case DecorateEntityType.TOPIC:
                    JumpDetail.jumpColumn(mContext, resourceId, "", 8);
                    break;
                case DecorateEntityType.MEMBER:
                    JumpDetail.jumpColumn(mContext, resourceId, "", 5);
                    break;
                case "message": // 消息页面 TODO: 跳转到消息页面
                    break;
                case "my_purchase": // 已购
                    JumpDetail.jumpMineLearning(mContext, mContext.getString(R.string.learning_tab_title));
                    break;
                case "custom": // 外部链接 TODO: 跳转到外部链接
                    break;
                case "micro_page": // 微页面 TODO: 跳转到微页面
                    break;
                default:
                    Log.d(TAG, "OnBannerClick: 未知链接");
                    break;
            }

            HashMap<String, String> map = new HashMap<>(1);
            map.put(MobclickEvent.INDEX, position1 + "");
            EventReportManager.onEvent(mContext, MobclickEvent.COURSE_BANNER_CLICK, map);
        });
        bannerArr.put(currentBindPos, banner);
    }
}
