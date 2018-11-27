package com.xiaoe.shop.wxb.adapter.decorate.shuffling_figure;

import android.content.Context;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

import static android.widget.ImageView.ScaleType.FIT_XY;

/**
 * Date: 2018/11/26 14:39
 * Author: hansyang
 * Description:
 */
public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //Glide 加载图片简单用法
        imageView.setScaleType(FIT_XY);
        Glide.with(context)
                .load(path)
                .transform(new GlideRoundTransform(context, 3))
                .into(imageView);
    }
}
