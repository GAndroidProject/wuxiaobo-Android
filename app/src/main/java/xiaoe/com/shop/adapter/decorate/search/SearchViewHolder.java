package xiaoe.com.shop.adapter.decorate.search;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class SearchViewHolder extends BaseViewHolder {

    @BindView(R.id.search_wxb)
    public TextView searchWxb;
    @BindView(R.id.search_title)
    public TextView searchTitle;
    @BindView(R.id.search_icon)
    public SimpleDraweeView searchIcon;

    public SearchViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
