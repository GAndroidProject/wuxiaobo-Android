package com.xiaoe.shop.wxb.business.login.presenter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.bumptech.glide.load.engine.Resource;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.shop.wxb.base.XiaoeActivity;

import java.text.NumberFormat;

public class BitmapCut {

    /**
      * 裁切图片, 如果参数设置的截图起始点超过原图，则会原图返回，其他情况会按比例截图返回
      *
      * @param bitmap 需要切的图
      * @return rect 需要切图的位置
     */
    public static Bitmap ImageCropWithRect(Bitmap bitmap, Rect rect) {
        if (bitmap == null) {
            return null;
        }

        // 获取图片的宽，高
        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();

        // 由于可能截图大于真图大小，最后真正截图的大小
        int width = 0;
        int height = 0;

        if (rect.left >= imageWidth || rect.top >= imageHeight) {
            // 如果截图的起始超过原图，则返回原图
            return bitmap;
        } else if ((rect.left + rect.width() <= imageWidth) && (rect.top + rect.height() <= imageHeight)) {
            // 如果起始位置加宽高小于原图，则直接切图

            // 比较需要切图的起始位置和大小是否超过原图
            width = rect.width();
            height = rect.height();
        } else {
            // 此下面计算是为了截图不失真

            // 计算原图的宽高比
            Float imageScale = new Float(imageWidth) / imageHeight;
            // 计算需要截图的宽高比
            Float rectScale = new Float(rect.width()) / rect.height();

            if (imageScale > rectScale) {
                // 相对于需要的图，需要的宽高比更小
                height = imageHeight - rect.top;
                Float a = new Float(rect.width())/rect.height();
                width = (int)(a * height);
                // 如果高度加起点大于原图则返回原图
                if (width + rect.left >= imageHeight ) {
                    return bitmap;
                }
            } else if (imageScale < rectScale){
                // 相对于需要的图，原图的宽高比更小
                width = imageWidth - rect.left;
                Float a = new Float(rect.height())/rect.width();
                height = (int)(a * width);
                // 如果高度加起点大于原图则返回原图
                if (height + rect.top >= imageHeight ) {
                    return bitmap;
                }
            } else {
                // 宽高比一样
                width = imageWidth - rect.left;
                height = imageHeight - rect.height();
            }
        }

        // 下面这句是关键
        Bitmap bmp = Bitmap.createBitmap(bitmap,rect.left,rect.top,width,height , null,false);
        if (!bitmap.equals(bmp) && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bmp;
    }
}
