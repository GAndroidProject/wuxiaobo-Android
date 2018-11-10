package xiaoe.com.shop.business.mine.presenter;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class MoneyItemViewHolder extends BaseViewHolder {

    @BindView(R.id.money_item_wrap)
    LinearLayout money_item_wrap;
    @BindView(R.id.money_item_title)
    TextView money_item_title;
    @BindView(R.id.money_item_desc)
    TextView money_item_desc;

    MoneyItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
