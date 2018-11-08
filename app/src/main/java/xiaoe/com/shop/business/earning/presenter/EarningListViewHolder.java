package xiaoe.com.shop.business.earning.presenter;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;

public class EarningListViewHolder {

    @BindView(R.id.earning_item_title)
    TextView listItemTitle;
    @BindView(R.id.earning_item_time)
    TextView listItemContent;
    @BindView(R.id.earning_item_money)
    TextView listItemMoney;

    public EarningListViewHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}
