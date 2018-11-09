package xiaoe.com.shop.utils;

import android.net.Uri;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;

public class SetImageUriUtil {

    public static DraweeController setImgURI(SimpleDraweeView simpleDraweeView, String url) {
        Uri uri = Uri.fromFile(new File(url));

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                //根据View的尺寸放缩图片
                .setResizeOptions(new ResizeOptions(simpleDraweeView.getWidth(), simpleDraweeView.getHeight()))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setOldController(simpleDraweeView.getController())
                .setImageRequest(request)
                .setCallerContext(uri)
                .build();

        return controller;
    }
}
