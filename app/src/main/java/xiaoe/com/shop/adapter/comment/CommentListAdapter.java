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
import xiaoe.com.shop.widget.ListBottomLoadMoreView;

public class CommentListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "CommentListAdapter";
    private List<CommentEntity> commentList;
    private Context mContext;
    private OnClickCommentListener commentListener;
    private CommentLoadMoreHolder loadMoreHolder;
    private int loadMoreState = ListBottomLoadMoreView.STATE_NOT_LOAD;
    public CommentListAdapter(Context context, OnClickCommentListener listener) {
        this.mContext = context;
        commentList = new ArrayList<CommentEntity>();
        commentListener = listener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1){
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_comment, parent, false);
            return new CommentListHolder(mContext, view);
        }else{
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_comment_load_more, parent, false);
            loadMoreHolder = new CommentLoadMoreHolder(view);
            return loadMoreHolder;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(holder instanceof CommentListHolder){
            CommentListHolder viewHolder = (CommentListHolder) holder;
            viewHolder.bindView(commentList.get(position), position, commentListener);
        }else{
            CommentLoadMoreHolder loadMoreHolder = (CommentLoadMoreHolder) holder;
            loadMoreHolder.bindView(loadMoreState);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == getItemCount() - 1){
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
//        if(finished){
//            return  commentList.size();
//        }
        //+1是为了做上拉到最后一个加载更多
        return commentList.size() + 1;
    }
    public void addAll(List<CommentEntity> list){
        int count = commentList.size();
        commentList.addAll(list);
        notifyDataSetChanged();
//        notifyItemRangeInserted(count,getItemCount());
    }
    public void addPosition(CommentEntity comment, int position){
        commentList.add(position, comment);
        notifyItemInserted(position);
    }

    public List<CommentEntity> getData(){
        return commentList;
    }


    public CommentLoadMoreHolder getLoadMoreHolder() {
        return loadMoreHolder;
    }

    public void setLoadMoreState(int loadMoreState) {
        this.loadMoreState = loadMoreState;
    }
}
