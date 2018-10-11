package xiaoe.com.shop.adapter.decorate.shuffling_figure;

import android.view.View;

import com.youth.banner.Banner;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class ShufflingFigureViewHolder extends BaseViewHolder {

    @BindView(R.id.shuffling_figure)
    public Banner banner;

    public ShufflingFigureViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
