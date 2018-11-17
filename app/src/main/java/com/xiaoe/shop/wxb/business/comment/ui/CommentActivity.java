package com.xiaoe.shop.wxb.business.comment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.entitys.CommentEntity;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.CommentLikeRequest;
import com.xiaoe.network.requests.CommentListRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.SendCommentRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.comment.CommentListAdapter;
import com.xiaoe.shop.wxb.adapter.comment.CommentLoadMoreHolder;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.comment.presenter.CommentPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.interfaces.OnClickCommentListener;
import com.xiaoe.shop.wxb.interfaces.OnClickSendCommentListener;
import com.xiaoe.shop.wxb.interfaces.OnConfirmListener;
import com.xiaoe.shop.wxb.widget.CommentView;
import com.xiaoe.shop.wxb.widget.ListBottomLoadMoreView;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import java.util.List;

public class CommentActivity extends XiaoeActivity implements View.OnClickListener, OnClickSendCommentListener,
        OnClickCommentListener {
    private static final String TAG = "CommentActivity";
    private final int DIALOG_TAG_DELETE_COMMENT = 90001;
    private RecyclerView commentRecyclerView;
    private CommentListAdapter commentAdapter;
    private CommentView commentView;
    private CommentPresenter commentPresenter;
    private Intent mIntent;
    private final int pageSize = 10;
    private String resourceId;
    private int resourceType;
    private StatusPagerView statusPagerView;
    private TextView commentCountView;
    private String resourceTitle;
    private CommentEntity sendComment;
    private boolean sending = false;
    private int mCommentCount = 0;
    private CommentEntity replyCommentEntity = null; //被回复的评论id
    private ImageView btnBack;
    private int mPosition;
    private int lastVisibleItemPosition = -1;
    private boolean isCommentFinished = false;

    List<LoginUser> loginUserList;
    TouristDialog touristDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentPresenter = new CommentPresenter(this);
        loginUserList = getLoginUserList();

        if (loginUserList.size() == 0) {
            touristDialog = new TouristDialog(this);
            touristDialog.setDialogCloseClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                }
            });
            touristDialog.setDialogConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpDetail.jumpLogin(CommentActivity.this, true);
                }
            });
        }

        mIntent = getIntent();
        initView();
        initData();
    }

    private void initData() {
        resourceId = mIntent.getStringExtra("resourceId");
        resourceType = mIntent.getIntExtra("resourceType", -1);
        resourceTitle = mIntent.getStringExtra("resourceTitle");
        commentPresenter.requestCommentList(resourceId, resourceType, pageSize, -1, null);
    }

    private void initView() {
        RelativeLayout activityRootView = (RelativeLayout) findViewById(R.id.comment_pager);
        commentCountView = (TextView) findViewById(R.id.comment_count);
        commentCountView.setText("评论");
        commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        commentRecyclerView.setItemAnimator(null);
        commentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                if ((visibleItemCount > 0 && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                    onLoadMore(recyclerView);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if(layoutManager != null){
                    lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        commentRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.HORIZONTAL));
        commentRecyclerView.setLayoutManager(layoutManager);
        commentAdapter = new CommentListAdapter(this, this);
        commentRecyclerView.setAdapter(commentAdapter);

        commentView = (CommentView) findViewById(R.id.comment_view);
        commentView.setActivityRootView(activityRootView);
        commentView.setSendListener(this);
        commentView.setIsTourists(loginUserList.size() != 1);

        statusPagerView = (StatusPagerView) findViewById(R.id.status_pager);
        setPagerState(-1);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
    }


    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject jsonObject = (JSONObject) entity;
        if(entity == null || !success){
            if(iRequest instanceof CommentListRequest){
                setPagerState(-3);
            }else if(iRequest instanceof SendCommentRequest){
                getDialog().dismissDialog();
                sending = false;
                if(commentView.isReply()){
                    toastCustom(getResources().getString(R.string.send_reply_comment_fail));
                    commentView.setReply(false);
                }else{
                    toastCustom(getResources().getString(R.string.send_comment_fail));
                }
            }
            return;
        }
        Object dataObject = jsonObject.get("data");
        if(jsonObject.getIntValue("code") != NetworkCodes.CODE_SUCCEED || dataObject == null ){
            if(iRequest instanceof CommentListRequest){
                setPagerState(-3);
            }else if(iRequest instanceof SendCommentRequest){
                getDialog().dismissDialog();
                sending = false;
                if(commentView.isReply()){
                    toastCustom(getResources().getString(R.string.send_reply_comment_fail));
                    commentView.setReply(false);
                }else{
                    toastCustom(getResources().getString(R.string.send_comment_fail));
                }
            }
            return;
        }
        if(iRequest instanceof CommentListRequest){
            JSONObject data = (JSONObject) dataObject;
            commentListRequest(data);
        }else if(iRequest instanceof SendCommentRequest){
            getDialog().dismissDialog();
            sending = false;
            JSONObject data = (JSONObject) dataObject;
            sendCommentRequest(data);
        }else if(iRequest instanceof CommentLikeRequest){
            commentLikeRequest(null);
        }
    }

    private void commentLikeRequest(JSONObject data) {
        CommentEntity commentEntity = commentAdapter.getData().get(mPosition);
        commentAdapter.notifyItemChanged(mPosition);
    }

    private void sendCommentRequest(JSONObject data) {
        if(mCommentCount == 0){
            setPagerState(0);
        }
        mCommentCount++;
        commentCountView.setText("评论 "+mCommentCount+" 条");
        int commentId = data.getIntValue("comment_id");
        sendComment.setComment_id(commentId);
        commentAdapter.addPosition(sendComment, 0);
        commentRecyclerView.scrollToPosition(0);
        if(commentView.isReply()){
            toastCustom(getResources().getString(R.string.send_reply_comment_succeed));
            commentView.setReply(false);
        }else{
            toastCustom(getResources().getString(R.string.send_comment_succeed));
        }
    }

    private void commentListRequest(JSONObject dataObject) {
        int count = dataObject.getIntValue("total_count");
        if(count <= 0){
            isCommentFinished = true;
            commentAdapter.setLoadMoreState(ListBottomLoadMoreView.STATE_ALL_FINISH);
            commentAdapter.notifyDataSetChanged();
            setPagerState(-2);
            return;
        }
        isCommentFinished = dataObject.getBoolean("is_finished");
        if(isCommentFinished){
            commentAdapter.setLoadMoreState(ListBottomLoadMoreView.STATE_ALL_FINISH);
        }else{
            commentAdapter.setLoadMoreState(ListBottomLoadMoreView.STATE_NOT_LOAD);
        }
        mCommentCount = count;
        commentCountView.setText("评论 "+count+" 条");
        JSONArray comments = dataObject.getJSONArray("comments");
        List<CommentEntity> commentList = comments.toJavaList(CommentEntity.class);
        commentAdapter.addAll(commentList);
        setPagerState(0);
    }

    private void setPagerState(int state){
        if(state == -1){
            //评论列表加载中
            statusPagerView.setHintStateVisibility(View.GONE);
            statusPagerView.setLoadingState(View.VISIBLE);
        }else if(state == -2){
            //没有评论列表
            statusPagerView.setHintStateVisibility(View.VISIBLE);
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setStateImage(R.mipmap.detail_none);
            statusPagerView.setStateText(getResources().getString(R.string.empty_comment));
        }else if(state == -3){
            //获取评论列表失败
            statusPagerView.setHintStateVisibility(View.VISIBLE);
            statusPagerView.setLoadingState(View.GONE);
            statusPagerView.setStateImage(R.mipmap.network_none);
            statusPagerView.setStateText(getResources().getString(R.string.get_comment_fail));
        }
        statusPagerView.setVisibility(state >= 0 ? View.GONE : View.VISIBLE);
        commentView.setVisibility(state == -2 || state >= 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onSend(String content) {
        if(sending){
            //模拟防重复点击
            return;
        }
        if(TextUtils.isEmpty(content)){
            toastCustom(getResources().getString(R.string.send_empty_comment));
            return;
        }
        sending = true;
        getDialog().showLoadDialog(false);
        sendComment = new CommentEntity();
        sendComment.setContent(content);
        sendComment.setUser_avatar(CommonUserInfo.getWxAvatar());
        sendComment.setUser_id(CommonUserInfo.getUserId());
        sendComment.setUser_nickname(CommonUserInfo.getWxNickname());
        CommentEntity tempReplyCommentEntity = null;
        if(commentView.isReply()){
            tempReplyCommentEntity = replyCommentEntity;
            sendComment.setSrc_comment_id(replyCommentEntity.getComment_id());
            sendComment.setSrc_content(replyCommentEntity.getContent());
        }
        commentPresenter.sendComment(resourceId, resourceType, resourceTitle, content, tempReplyCommentEntity);
    }

    @Override
    public void onClickComment(CommentEntity commentEntity, int type, int position) {
        if(type == CommentView.TYPE_REPLY){
            //回复
            if (loginUserList.size() == 1) {
                commentView.setSrcCommentHint(commentEntity.getUser_nickname());
                replyCommentEntity = commentEntity;
            } else {
                touristDialog.showDialog();
            }
        }else if(type == CommentView.TYPE_LIKE){
            //点赞
            if (loginUserList.size() == 1) {
                mPosition = position;
                boolean praise = !commentEntity.isIs_praise();
                commentEntity.setIs_praise(praise);
                int likeCount = commentEntity.getLike_num();
                if(praise){
                    likeCount++;
                }else{
                    likeCount--;
                }
                commentEntity.setLike_num(likeCount);
                commentPresenter.likeComment(resourceId, resourceType, commentEntity.getComment_id(), commentEntity.getUser_id(), commentEntity.getContent(), praise);
            } else {
                touristDialog.showDialog();
            }
        }else if(type == CommentView.TYPE_DELETE){
            //删除
            if (loginUserList.size() == 1) {
                String userId = TextUtils.isEmpty(CommonUserInfo.getUserId()) ? "" : CommonUserInfo.getUserId();
                if(commentEntity.isDelete() || userId.equals(commentEntity.getUser_id())){
                    return;
                }
                getDialog().getTitleView().setGravity(Gravity.START);
                getDialog().getTitleView().setPadding(Dp2Px2SpUtil.dp2px(this, 20), 0, 0, 0);
                getDialog().setTitle(getString(R.string.delete_comment_hint));
                getDialog().setHideCancelButton(false);
                getDialog().setConfirmListener(new OnConfirmListener() {
                    @Override
                    public void onClickConfirm(View view, int tag) {
                        if(DIALOG_TAG_DELETE_COMMENT == tag){
                            mCommentCount--;
                            commentCountView.setText("评论 "+mCommentCount+" 条");
                            commentEntity.setDelete(true);
                            commentPresenter.deleteComment(resourceId, resourceType, commentEntity.getComment_id());
                            commentAdapter.getData().remove(position);
                            commentAdapter.notifyItemRangeChanged(0, commentAdapter.getItemCount());
                        }
                    }
                });
                getDialog().showDialog(DIALOG_TAG_DELETE_COMMENT);
            } else {
                touristDialog.showDialog();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_back){
            finish();
        }
    }

    private void onLoadMore(RecyclerView recyclerView) {
        CommentLoadMoreHolder loadMoreHolder = commentAdapter.getLoadMoreHolder();
        if(loadMoreHolder != null && loadMoreHolder.getLoadState() == ListBottomLoadMoreView.STATE_NOT_LOAD  && !isCommentFinished){
            loadMoreHolder.setLoadState(ListBottomLoadMoreView.STATE_LOADING);
            commentAdapter.setLoadMoreState(ListBottomLoadMoreView.STATE_LOADING);
            CommentEntity commentEntity = commentAdapter.getData().get(commentAdapter.getItemCount() - 2);
            commentPresenter.requestCommentList(resourceId, resourceType, pageSize, commentEntity.getComment_id(), null);
        }
    }
}
