package xiaoe.com.shop.business.historymessage.ui;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.HistoryMessageEntity;
import xiaoe.com.common.entitys.HistoryMessageReq;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.GetHistoryMessageRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.historymessage.HistoryMessageAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.historymessage.presenter.HistoryMessagePresenter;
import xiaoe.com.shop.utils.StatusBarUtil;

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
                        }
                        historyMessageAdapter.loadMoreComplete();
                    }
                    if (listBeans == null || listBeans.size() == 0) {
                        historyMessageAdapter.loadMoreEnd();
                    } else {
                        firstStart++;
                        for (HistoryMessageEntity.ListBean listBean : listBeans) {
                            switch (listBean.getMessage_type()) {
                                case 0:
                                    messageLastId = listBean.getCreated_at();
                                    break;
                                case 1:
                                    commentLastId = listBean.getId() + "";
                                    break;
                                case 2:
                                    praiseLastId = listBean.getId() + "";
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取历史消息失败...");
                    Toast((String) result.get("msg"));
                    if (firstStart != 0) {
                        historyMessageAdapter.loadMoreFail();
                    }
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
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
        accountToolbar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
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
        mEmptyView.setNoDataTip(R.string.NoResult);
        mEmptyView.setOnClickListener(v -> loadMessages());
        historyMessageAdapter.setEmptyView(mEmptyView);
        historyMessageAdapter.setOnLoadMoreListener(() -> {
            if (refreshLayout.isRefreshing() || isRequesting) {
                return;
            }
            loadMessages();
        }, rvAllMessage);
        historyMessageAdapter.setOnItemClickListener((adapter, view, position) -> {
//                TransOrder transOrder = (TransOrder) adapter.getData().get(position);
//                String state = transOrder.getState();
//                if (!TextUtils.isEmpty(state)) {
//                    if (TransOrder.ORDER_STATE.FILLED.equals(state)) {
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable(MarketOrderDetailsFragment.PARAM_TRANS_ORDER, transOrder);
//                        presentFragment(new MarketOrderDetailsFragment(bundle));
//                    }
//                }
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
    }

    private List<HistoryMessageEntity.ListBean> getMessageList() {
        for (int i = 0; i < 20; i++) {
            HistoryMessageEntity.ListBean listBean = new HistoryMessageEntity.ListBean();
            listBean.setApp_id("appiOW1KfWe9943");
            listBean.setSend_nick_name("吴晓波频道 " + (i + 1));
            listBean.setCreated_at("13小时前");
            listBean.setTitle("回复了你的评论");
            listBean.setContent("百万级内容SKU，多矩阵流量渠道，帮好内容匹配精准流量，帮流量轻松入局内容付费，人人…");

            messageList.add(listBean);
        }

        return messageList;
    }

    private void initData() {
        if (messageList != null) {
            historyMessageAdapter.setNewData(messageList);
        }
        historyMessagePresenter = new HistoryMessagePresenter(this);
        loadMessages();
    }

    private void loadMessages() {
        HistoryMessageReq historyMessageReq = new HistoryMessageReq();
        HistoryMessageReq.ListBean buzData = new HistoryMessageReq.ListBean();
        buzData.setPage_size("20");
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
