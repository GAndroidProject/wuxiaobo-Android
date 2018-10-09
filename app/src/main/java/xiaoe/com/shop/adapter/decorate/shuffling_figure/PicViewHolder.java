package xiaoe.com.shop.adapter.decorate.shuffling_figure;

import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;

public class PicViewHolder extends Holder<String> {

    private SimpleDraweeView sdView;

    public PicViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    protected void initView(View itemView) {
        sdView = (SimpleDraweeView) itemView;
        sdView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    public void updateUI(String data) {
        sdView.setImageURI(data);
    }
}
