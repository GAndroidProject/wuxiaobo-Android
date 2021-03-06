package com.xiaoe.shop.wxb.business.historymessage.ui;

import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.HistoryMessageEntity;
import com.xiaoe.common.entitys.HistoryMessageReq;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.GetHistoryMessageRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.historymessage.HistoryMessageAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.historymessage.presenter.HistoryMessagePresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;
import com.xiaoe.shop.wxb.widget.EmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flynnWang
 * @date 2018/11/13
 * <p>
 * 描述：我的-历史消息页面
 */
public class HistoryMessageActivity extends XiaoeActivity {

    @BindView(R.id.setting_account_edit_back)
    ImageView settingAccountEditBack;
    @BindView(R.id.setting_account_edit_title)
    TextView settingAccountEditTitle;
    @BindView(R.id.account_toolbar)
    Toolbar accountToolbar;
    @BindView(R.id.rv_all_message)
    RecyclerView rvAllMessage;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private EmptyView mEmptyView;

    private static final String TAG = "HistoryMessageActivity";

    private List<HistoryMessageEntity.ListBean> messageList = new ArrayList<>();
    private HistoryMessageAdapter historyMessageAdapter;

    private HistoryMessagePresenter historyMessagePresenter;
    private int firstStart = 0;
    private String messageLastId = "-1";
    private String commentLastId = "-1";
    private String praiseLastId = "-1";
    private String pageSize = "10";

    private boolean isRequesting;

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        isRequesting = false;
        refreshLayout.setRefreshing(false);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof GetHistoryMessageRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {

                    JSONObject data = (JSONObject) result.get("data");
                    List<HistoryMessageEntity.ListBean> listBeans = JSONObject.parseArray(data.getString("list"), HistoryMessageEntity.ListBean.class);

                    if (firstStart == 0) {
                        historyMessageAdapter.setNewData(listBeans);
                        messageList = listBeans;
                    } else {
                        if (listBeans != null) {
                            historyMessageAdapter.addData(listBeans);
                            messageList.addAll(listBeans);
                        }
                        historyMessageAdapter.loadMoreComplete();
                    }
                    if (listBeans == null || listBeans.size() == 0) {
                        historyMessageAdapter.loadMoreEnd();
                        mEmptyView.setStatus(EmptyView.STATUS_NO_DATA);
                    } else {
                        firstStart++;
                        messageLastId = data.getString("message_last_id");
                        commentLastId = data.getString("comment_last_id");
                        praiseLastId = data.getString("praise_last_id");
                    }
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取历史消息失败...");
                    ToastUtils.show(mContext, (String) result.get("msg"));
                    if (firstStart != 0) {
                        historyMessageAdapter.loadMoreFail();
                    }
                    if (messageList.size() == 0) {
                        mEmptyView.setStatus(EmptyView.STATUS_NO_DATA);
                    }
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
            if (messageList.size() == 0) {
                mEmptyView.setStatus(EmptyView.STATUS_NO_DATA);
            }
            if (firstStart != 0) {
                historyMessageAdapter.loadMoreFail();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStatusBar();
        setContentView(R.layout.activity_history_message);
        ButterKnife.bind(this);

        initActionBar();
        initView();
        initData();
    }

    private void initActionBar() {
        // 状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
//        accountToolbar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        FrameLayout.LayoutParams toolbarParams = (FrameLayout.LayoutParams) accountToolbar.getLayoutParams();
        toolbarParams.height = Dp2Px2SpUtil.dp2px(this, 44);
        accountToolbar.setLayoutParams(toolbarParams);
        settingAccountEditTitle.setText(mContext.getString(R.string.message_notice_text));
    }

    private void initView() {
        rvAllMessage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        // 设置分割线
        final int padding = Dp2Px2SpUtil.dp2px(this, 10);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL) {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = padding * 2;
                } else {
                    outRect.top = padding;
                }
                if (parent.getChildAdapterPosition(view) == parent.getChildCount()) {
                    outRect.bottom = padding * 2;
                } else {
                    outRect.bottom = padding;
                }
                outRect.left = padding * 2;
                outRect.right = padding * 2;
            }
        };
        itemDecoration.setDrawable(new ColorDrawable(Color.TRANSPARENT));
        rvAllMessage.addItemDecoration(itemDecoration);

        historyMessageAdapter = new HistoryMessageAdapter(this);

        mEmptyView = new EmptyView(this);
        mEmptyView.setNoDataTip(R.string.no_data_text);
        mEmptyView.setOnClickListener(v -> loadMessages());
        historyMessageAdapter.setEmptyView(mEmptyView);
        historyMessageAdapter.setOnLoadMoreListener(() -> {
            if (refreshLayout.isRefreshing() || isRequesting) {
                return;
            }
            loadMessages();
        }, rvAllMessage);
        historyMessageAdapter.setOnItemClickListener((adapter, view, position) -> {
            jumpDetail((HistoryMessageEntity.ListBean) adapter.getData().get(position));
        });
        rvAllMessage.setAdapter(historyMessageAdapter);

        refreshLayout.setColorSchemeColors(Color.parseColor("#ff1094ff"));
        refreshLayout.setOnRefreshListener(() -> {
            if (isRequesting) {
                return;
            }
            firstStart = 0;
            isRequesting = true;
            updateMessages();
        });

        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_UNREAD_MSG_COUNT, 0);
    }

    private void jumpDetail(HistoryMessageEntity.ListBean listBean) {
        if (listBean == null) {
            return;
        }
        switch (listBean.getMessage_type()) {
            case 0:
                skipAction(listBean);
                break;
            case 1:
            case 2:
                JumpDetail.jumpComment(mContext, listBean.getRecord_id(), listBean.getType(), listBean.getRecord_title());
                break;
            default:
                break;
        }
    }

    private void skipAction(HistoryMessageEntity.ListBean listBean) {
        if (listBean == null) {
            return;
        }
        /*
         * 跳转类型：0-不跳转，1-图文，2-音频，3-视频，4-社群，5-跳转url，6-专栏，7-直播间内，8-分答问题详情页，9-活动，16-打卡，21-电子书
         */
        switch (listBean.getSkip_type()) {
            case 0:
                break;
            case 1:
                JumpDetail.jumpImageText(mContext, listBean.getSkip_target(), "", "");
                break;
            case 2:
                JumpDetail.jumpAudio(mContext, listBean.getSkip_target(), 0);
                break;
            case 3:
                JumpDetail.jumpVideo(mContext, listBean.getSkip_target(), "", false, "");
                break;
            case 5:
                JumpDetail.jumpAppBrowser(mContext, listBean.getSkip_target(), "");
                break;
            case 6:
                JumpDetail.jumpColumn(mContext, listBean.getSkip_target(), "", listBean.getSub_skip_type());
                break;
            case 4:
            case 7:
            case 8:
            case 9:
            case 16:
            case 21:
            default:
                ToastUtils.show(mContext, R.string.Jump_not_text);
                break;
        }
    }

    private List<HistoryMessageEntity.ListBean> getMessageList() {
        for (int i = 0; i < 20; i++) {
            HistoryMessageEntity.ListBean listBean = new HistoryMessageEntity.ListBean();
            listBean.setApp_id("appiOW1KfWe9943");
            listBean.setSend_nick_name("launch " + (i + 1));
            listBean.setCreated_at("13小时前");
            listBean.setTitle("回复了你的评论");
            listBean.setContent("百万级内容SKU，多矩阵流量渠道，帮好内容匹配精准流量，帮流量轻松入局内容付费，人人…");

            messageList.add(listBean);
        }

        return messageList;
    }

    private void initData() {
        historyMessagePresenter = new HistoryMessagePresenter(this);
        loadMessages();
    }

    private void loadMessages() {
        HistoryMessageReq historyMessageReq = new HistoryMessageReq();
        HistoryMessageReq.ListBean buzData = new HistoryMessageReq.ListBean();
        buzData.setPage_size(pageSize);
        buzData.setMessage_last_id(messageLastId);
        buzData.setComment_last_id(commentLastId);
        buzData.setPraise_last_id(praiseLastId);
        historyMessageReq.setBuz_data(buzData);

        historyMessagePresenter.requestHistoryMessage(historyMessageReq);
    }

    private void updateMessages() {
        firstStart = 0;
        messageLastId = "-1";
        commentLastId = "-1";
        praiseLastId = "-1";

        loadMessages();
    }

}
