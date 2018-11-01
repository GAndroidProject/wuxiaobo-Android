package xiaoe.com.shop.adapter.comment;

import android.view.View;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.widget.ListBottomLoadMoreView;

public class CommentLoadMoreHolder extends BaseViewHolder {

    private final ListBottomLoadMoreView loadMoreView;

    public CommentLoadMoreHolder(View itemView) {
        super(itemView);
        loadMoreView = (ListBottomLoadMoreView) itemView.findViewById(R.id.comment_load_more);
    }
    public void bindView(int state){
        setLoadState(state);
    }

    public void setLoadState(int state){
        loadMoreView.setLoadState(state);
    }

    public int getLoadState(){
        return loadMoreView.getLoadState();
    }
}
