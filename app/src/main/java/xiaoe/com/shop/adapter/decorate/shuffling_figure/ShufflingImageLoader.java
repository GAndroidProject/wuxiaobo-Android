package xiaoe.com.shop.adapter.decorate.shuffling_figure;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.youth.banner.loader.ImageLoader;


public class ShufflingImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        SimpleDraweeView simpleDraweeView = (SimpleDraweeView)imageView;
        simpleDraweeView.setImageURI(path.toString());
    }

    @Override
    public ImageView createImageView(Context context) {
        return new SimpleDraweeView(context);
    }
}