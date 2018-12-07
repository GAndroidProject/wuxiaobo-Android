package com.xiaoe.shop.wxb.adapter.decorate.shuffling_figure;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.shop.wxb.R;
import com.youth.banner.loader.ImageLoader;

import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

@Deprecated
public class ShufflingImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)imageView;
        simpleDraweeView.getHierarchy().setRoundingParams(RoundingParams.fromCornersRadii(8, 8, 8, 8));
        simpleDraweeView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
        SetImageUriUtil.setImgURI(simpleDraweeView, path.toString(), Dp2Px2SpUtil.dp2px(context, 375), Dp2Px2SpUtil.dp2px(context, 160));
    }

    @Override
    public ImageView createImageView(Context context) {
        return new SimpleDraweeView(context);
    }
}