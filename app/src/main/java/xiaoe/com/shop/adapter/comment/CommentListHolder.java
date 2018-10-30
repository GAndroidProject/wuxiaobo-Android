package xiaoe.com.shop.adapter.comment;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.common.entitys.CommentEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.utils.NumberFormat;

public class CommentListHolder extends BaseViewHolder {
    private static final String TAG = "CommentListHolder";
    private View rootView;
    private SimpleDraweeView userAvatar;
    private TextView userName;
    private TextView commentContent;
    private TextView sendCommentDate;
    private TextView likeCount;
    private ImageView likeIcon;
    private TextView srcCommentContent;
    private TextView btnReplyComment;

    public CommentListHolder(View itemView) {
        super(itemView);
        rootView = itemView;
        initViews();
    }

    private void initViews() {
        userAvatar = (SimpleDraweeView) rootView.findViewById(R.id.column_user_avatar);
        userName = (TextView) rootView.findViewById(R.id.column_user_name);
        commentContent = (TextView) rootView.findViewById(R.id.comment_content);
        sendCommentDate = (TextView) rootView.findViewById(R.id.send_comment_date);
        likeCount = (TextView) rootView.findViewById(R.id.comment_like_count);
        likeIcon = (ImageView) rootView.findViewById(R.id.comment_like_icon);
        srcCommentContent = (TextView) rootView.findViewById(R.id.src_comment_content);
        btnReplyComment = (TextView) rootView.findViewById(R.id.btn_reply_comment);
    }

    public void bindView(CommentEntity commentEntity, int position){
        userAvatar.setImageURI(commentEntity.getUser_avatar());
        userName.setText(commentEntity.getUser_nickname());
        commentContent.setText(commentEntity.getContent());
        likeCount.setText(NumberFormat.viewCountToString(commentEntity.getLike_num()));
        sendCommentDate.setText(commentEntity.getComment_at());
        if(commentEntity.isIs_praise()){
            likeIcon.setImageResource(R.mipmap.comment_liked);
        }else{
            likeIcon.setImageResource(R.mipmap.comment_like);
        }
        if(TextUtils.isEmpty(commentEntity.getSrc_content())){
            srcCommentContent.setVisibility(View.GONE);
        }else{
            srcCommentContent.setVisibility(View.VISIBLE);
            srcCommentContent.setText(commentEntity.getSrc_content());
        }
//        btnReplyComment.setOnClickListener(this);
    }
}
