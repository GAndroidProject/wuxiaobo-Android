package com.xiaoe.shop.wxb.business.earning.presenter;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;

public class WrListViewHolder {

    @BindView(R.id.wr_item_title)
    TextView wrTitle;
    @BindView(R.id.wr_item_money)
    TextView wrMoney;
    @BindView(R.id.wr_item_time)
    TextView wrTime;
    @BindView(R.id.wr_item_state)
    TextView wrState;

    public WrListViewHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}
