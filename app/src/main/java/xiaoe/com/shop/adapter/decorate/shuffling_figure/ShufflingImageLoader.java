package xiaoe.com.shop.adapter.decorate.shuffling_figure;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.loader.ImageLoader;

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.utils.SetImageUriUtil;


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