package xiaoe.com.shop.business.mine.presenter;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;

public class EquityItemViewHolder {

    @BindView(R.id.vip_item_content)
    TextView vip_item_content;

    EquityItemViewHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}
