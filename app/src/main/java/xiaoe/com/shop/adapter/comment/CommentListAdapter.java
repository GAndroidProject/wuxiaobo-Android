package xiaoe.com.shop.adapter.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class CommentListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "CommentListAdapter";

    private Context mContext;

    public CommentListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_comment, parent, false);
        return new CommentListHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 20;
    }
}
