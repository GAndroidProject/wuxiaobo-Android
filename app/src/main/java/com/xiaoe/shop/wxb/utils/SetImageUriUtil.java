package com.xiaoe.shop.wxb.utils;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ImageDecodeOptionsBuilder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

// SimpleDraweeView 设置图片工具类（按比例缩放图片）
public class SetImageUriUtil {

    /**
     * 设置 simpleDraweeView 图片，由于 simpleDraweeView 获取不了宽高，所以必须传入图片的宽高
     * 使用时使用 Dp2pxUtil 将单位统一
     * @param simpleDraweeView simpleDraweeView
     * @param url              图片 url
     * @param width 图片的宽度
     * @param height 图片的高度
     */
    public static void setImgURI(SimpleDraweeView simpleDraweeView, String url, int width, int height) {

        if (url == null) {
            return;
        }

        Uri uri = Uri.parse(url);

        //根据View的尺寸放缩图片
        if (!"".equals(uri.toString())) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDraweeView.getController())
                    .setImageRequest(request)
                    .setCallerContext(uri)
                    .build();

            simpleDraweeView.setController(controller);
        } else {
            // 如果没有图片的话就设置为空
            simpleDraweeView.setImageURI("");
        }
    }

    /**
     * 设置 gif 图
     * @param simpleDraweeView simpleDraweeView
     * @param url              图片 url
     * @param width            宽度
     * @param height           高度
     */
    public static void setGifURI(SimpleDraweeView simpleDraweeView, String url, int width, int height) {

        if (url == null) {
            return;
        }

        Uri uri = Uri.parse(url);

        //根据View的尺寸放缩图片
        if (!"".equals(uri.toString())) {
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(new ResizeOptions(width, height))
                    .build();

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDraweeView.getController())
                    .setImageRequest(request)
                    .setAutoPlayAnimations(true)
                    .setCallerContext(uri)
                    .build();

            simpleDraweeView.setController(controller);
        } else {
            // 如果没有图片的话就设置为空
            simpleDraweeView.setImageURI("");
        }

    }

    /**
     * 代码处理未圆形图
     * @param uri
     */
    public static void setRoundAsCircle(SimpleDraweeView audioRing, Uri uri){
        audioRing.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);

        ImageDecodeOptions imageDecodeOptions = new ImageDecodeOptionsBuilder()
                .setForceStaticImage(true)
                .build();

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
//                .setResizeOptions(new ResizeOptions(100, 100))
                .setCacheChoice(ImageRequest.CacheChoice.SMALL)
                .setImageDecodeOptions(imageDecodeOptions)
                .build();

        PipelineDraweeControllerBuilder builder = Fresco.getDraweeControllerBuilderSupplier().get()
                .setOldController(audioRing.getController())
                .setImageRequest(request);
        audioRing.setController(builder.build());
    }

    public static boolean isGif(String url) {
        return "gif".equals(url.substring(url.lastIndexOf(".") + 1)) || "GIF".equals(url.substring(url.lastIndexOf(".") + 1));
    }
}
