package com.xiaoe.shop.wxb.business.mine.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.common.utils.MeasureUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.business.mine.presenter.MineEquityListAdapter;

public class MineVipCard extends RelativeLayout {

    private Context mContext;

    // 续费按钮
    private TextView card_renewal;
    // 更多权益
//    private TextView card_equity_more;
    // 有效期
    private TextView card_deadline;
    // 权益列表
    private ListView card_equity_list;
    // 容器
    private RelativeLayout card_container;

    public MineVipCard(Context context) {
        super(context);
        init(context);
    }

    public MineVipCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MineVipCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MineVipCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.mine_vip_card, this);
        card_renewal = (TextView) view.findViewById(R.id.card_renewal);
//        card_equity_more = (TextView) view.findViewById(R.id.card_equity_more);
        card_deadline = (TextView) view.findViewById(R.id.card_deadline);
        card_equity_list = (ListView) view.findViewById(R.id.card_equity_list);
        card_container = (RelativeLayout) view.findViewById(R.id.card_container);
    }

    public void setBtnRenewalClickListener(OnClickListener listener) {
        card_renewal.setOnClickListener(listener);
    }

//    public void setMoreEquityClickListener(OnClickListener listener) {
//        card_equity_more.setOnClickListener(listener);
//    }

    public void setDeadLine(String deadLine) {
        card_deadline.setText(deadLine);
    }

    public void setEquityListAdapter(MineEquityListAdapter adapter) {
        card_equity_list.setAdapter(adapter);
        MeasureUtil.setListViewHeightBasedOnChildren(card_equity_list);
    }

    public void setCardContainerClickListener(OnClickListener listener) {
        card_container.setOnClickListener(listener);
    }
}
