package xiaoe.com.shop.adapter.comment;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.CommentEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.OnClickCommentListener;

public class CommentListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "CommentListAdapter";
    private List<CommentEntity> commentList;
    private Context mContext;
    private OnClickCommentListener commentListener;

    public CommentListAdapter(Context context, OnClickCommentListener listener) {
        this.mContext = context;
        commentList = new ArrayList<CommentEntity>();
        commentListener = listener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_comment, parent, false);
        return new CommentListHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        CommentListHolder viewHolder = (CommentListHolder) holder;
        viewHolder.bindView(commentList.get(position), position, commentListener);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }
    public void addAll(List<CommentEntity> list){
        commentList.addAll(list);
        notifyDataSetChanged();
    }
    public void addPosition(CommentEntity comment, int position){
        commentList.add(position, comment);
        notifyItemInserted(position);
    }
}
