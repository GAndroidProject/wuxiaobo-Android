package xiaoe.com.shop.business.comment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

import xiaoe.com.common.entitys.CommentEntity;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.CommentListRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.SendCommentRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.comment.CommentListAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.comment.presenter.CommentPresenter;
import xiaoe.com.shop.interfaces.OnClickSendCommentListener;
import xiaoe.com.shop.widget.CommentView;
import xiaoe.com.shop.widget.StatusPagerView;

public class CommentActivity extends XiaoeActivity implements OnClickSendCommentListener {
    private static final String TAG = "CommentActivity";
    private RecyclerView commentRecyclerView;
    private CommentListAdapter commentAdapter;
    private CommentView commentView;
    private CommentPresenter commentPresenter;
    private Intent mIntent;
    private int pageSize = 20;
    private String resourceId;
    private int resourceType;
    private StatusPagerView statusPagerView;
    private TextView commentCountView;
    private String resourceTitle;
    private CommentEntity sendComment;
    private boolean sending = false;
    private int mCommentCount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentPresenter = new CommentPresenter(this);
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        commentRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.HORIZONTAL));
        commentRecyclerView.setLayoutManager(layoutManager);
        commentAdapter = new CommentListAdapter(this);
        commentRecyclerView.setAdapter(commentAdapter);

        commentView = (CommentView) findViewById(R.id.comment_view);
        commentView.setActivityRootView(activityRootView);
        commentView.setSendListener(this);

        statusPagerView = (StatusPagerView) findViewById(R.id.status_pager);
        setPagerState(-1);
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
                toastCustom(getResources().getString(R.string.send_comment_fail));
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
                toastCustom(getResources().getString(R.string.send_comment_fail));
            }
            return;
        }
        if(iRequest instanceof CommentListRequest){
            JSONObject data = (JSONObject) dataObject;
            commentListRequest(data);
        }else if(iRequest instanceof SendCommentRequest){
            getDialog().dismissDialog();
            sending = true;
            JSONObject data = (JSONObject) dataObject;
            sendCommentRequest(data);
        }
    }

    private void sendCommentRequest(JSONObject data) {
        mCommentCount++;
        commentCountView.setText("评论 "+mCommentCount+" 条");
        int commentId = data.getIntValue("comment_id");
        sendComment.setComment_id(commentId);
        commentAdapter.addPosition(sendComment, 0);
    }

    private void commentListRequest(JSONObject dataObject) {
        int count = dataObject.getIntValue("total_count");
        if(count <= 0){
            setPagerState(-2);
            return;
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
        commentPresenter.sendComment(resourceId, resourceType, resourceTitle, content, -1);
    }
}
