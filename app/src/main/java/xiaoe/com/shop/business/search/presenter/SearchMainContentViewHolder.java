package xiaoe.com.shop.business.search.presenter;

import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.interfaces.OnItemClickWithPosListener;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class SearchMainContentViewHolder extends BaseViewHolder {

    @BindView(R.id.search_main_item)
    TextView content;

    public SearchMainContentViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    private OnItemClickWithPosListener itemClickListener;
}
