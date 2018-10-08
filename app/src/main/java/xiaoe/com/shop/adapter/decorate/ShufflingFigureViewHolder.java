package xiaoe.com.shop.adapter.decorate;

import android.view.View;

import com.bigkoo.convenientbanner.ConvenientBanner;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class ShufflingFigureViewHolder extends BaseViewHolder {

    @BindView(R.id.shuffling_figure)
    ConvenientBanner convenientBanner;

    public ShufflingFigureViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
