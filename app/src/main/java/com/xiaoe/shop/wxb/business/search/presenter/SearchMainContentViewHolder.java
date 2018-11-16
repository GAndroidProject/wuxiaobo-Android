package com.xiaoe.shop.wxb.business.search.presenter;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.common.interfaces.OnItemClickWithPosListener;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

public class SearchMainContentViewHolder extends BaseViewHolder {

    @BindView(R.id.search_main_item)
    TextView content;

    public SearchMainContentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    private OnItemClickWithPosListener itemClickListener;
}
