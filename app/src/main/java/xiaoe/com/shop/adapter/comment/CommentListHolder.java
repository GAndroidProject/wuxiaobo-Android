package xiaoe.com.shop.adapter.comment;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class CommentListHolder extends BaseViewHolder {
    private static final String TAG = "CommentListHolder";
    private View rootView;
    private SimpleDraweeView userAvatar;
    private TextView userName;
    private TextView commentContent;
    private TextView sendCommentDate;
    private TextView likeCount;

    public CommentListHolder(View itemView) {
        super(itemView);
        rootView = itemView;
        initViews();
    }

    private void initViews() {
        userAvatar = (SimpleDraweeView) rootView.findViewById(R.id.column_user_avatar);
        userAvatar.setImageURI("res:///"+R.mipmap.cash_alipay);
        userName = (TextView) rootView.findViewById(R.id.column_user_name);
        userName.setText("马云");
        commentContent = (TextView) rootView.findViewById(R.id.comment_content);
        commentContent.setText("帮内容寻找精准流量 帮流量匹配优质内容!真的很棒很棒");
        sendCommentDate = (TextView) rootView.findViewById(R.id.send_comment_date);
        sendCommentDate.setText("2018-10-20 16:26:23");
        likeCount = (TextView) rootView.findViewById(R.id.comment_like_count);
        likeCount.setText("125");
    }

    public void bindView(){

    }
}
