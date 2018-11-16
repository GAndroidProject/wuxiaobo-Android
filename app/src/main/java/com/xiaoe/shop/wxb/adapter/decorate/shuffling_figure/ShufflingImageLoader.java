package com.xiaoe.shop.wxb.adapter.decorate.shuffling_figure;

import android.content.Context;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.loader.ImageLoader;

import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;


public class ShufflingImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)imageView;
        SetImageUriUtil.setImgURI(simpleDraweeView, path.toString(), Dp2Px2SpUtil.dp2px(context, 375), Dp2Px2SpUtil.dp2px(context, 160));
    }

    @Override
    public ImageView createImageView(Context context) {
        return new SimpleDraweeView(context);
    }
}