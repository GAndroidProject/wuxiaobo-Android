package xiaoe.com.shop.utils;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

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
}
