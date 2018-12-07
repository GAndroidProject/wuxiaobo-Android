package com.xiaoe.shop.wxb.adapter.comment;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.entitys.CommentEntity;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.interfaces.OnClickCommentListener;
import com.xiaoe.shop.wxb.utils.NumberFormat;
import com.xiaoe.shop.wxb.widget.CommentView;

public class CommentListHolder extends BaseViewHolder implements View.OnClickListener {
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
    private CommentEntity mCommentEntity;
    private OnClickCommentListener commentListener;
    private LinearLayout btnLikeComment;
    private ImageView btnCommentDelete;
    private final Context mContext;
    private int mPosition;

    public CommentListHolder(Context context, View itemView) {
        super(itemView);
        mContext = context;
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
        //被回复评论内容
        srcCommentContent = (TextView) rootView.findViewById(R.id.src_comment_content);
        //回复评论按钮
        btnReplyComment = (TextView) rootView.findViewById(R.id.btn_reply_comment);
        //点赞
        btnLikeComment = (LinearLayout) rootView.findViewById(R.id.btn_like_comment);
        //删除
        btnCommentDelete = (ImageView) rootView.findViewById(R.id.btn_comment_delete);
    }

    public void bindView(CommentEntity commentEntity, int position, OnClickCommentListener listener){
        mPosition = position;
        mCommentEntity = commentEntity;
        commentListener = listener;
        userAvatar.setImageURI(commentEntity.getUser_avatar());
        userName.setText(commentEntity.getUser_nickname());
        commentContent.setText(commentEntity.getContent());
        likeCount.setText(NumberFormat.viewCountToString(mContext, commentEntity.getLike_num()));
        sendCommentDate.setText(commentEntity.getComment_at());
        if(commentEntity.isIs_praise()){
            likeIcon.setImageResource(R.mipmap.comment_liked);
            likeCount.setTextColor(mContext.getResources().getColor(R.color.high_title_color));
        }else{
            likeIcon.setImageResource(R.mipmap.comment_like);
            likeCount.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
        }
        if(TextUtils.isEmpty(commentEntity.getSrc_content())){
            srcCommentContent.setVisibility(View.GONE);
        }else{
            srcCommentContent.setVisibility(View.VISIBLE);
            srcCommentContent.setText(commentEntity.getSrc_content());
        }
        btnReplyComment.setOnClickListener(this);
        btnLikeComment.setOnClickListener(this);
        if(!TextUtils.isEmpty(CommonUserInfo.getUserId()) && commentEntity.getUser_id().equals(CommonUserInfo.getUserId())){
            btnCommentDelete.setVisibility(View.VISIBLE);
            btnCommentDelete.setOnClickListener(this);
        }else{
            btnCommentDelete.setVisibility(View.GONE);
            btnCommentDelete.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_reply_comment:
                clickComment(CommentView.TYPE_REPLY);
                break;
            case R.id.btn_like_comment:
                clickComment(CommentView.TYPE_LIKE);
                break;
            case R.id.btn_comment_delete:
                clickComment(CommentView.TYPE_DELETE);
                break;
            default:
                break;
        }
    }
    private void clickComment(int type){
        if(commentListener != null){
            commentListener.onClickComment(mCommentEntity, type, mPosition);
        }
    }
}
