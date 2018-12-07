package com.xiaoe.shop.wxb.business.main.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.entitys.ChangeLoginIdentityEvent;
import com.xiaoe.common.entitys.ScholarshipEntity;
import com.xiaoe.common.entitys.ScholarshipRangeItem;
import com.xiaoe.common.entitys.TaskDetailIdEvent;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.MeasureUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.ScholarshipReceiveRequest;
import com.xiaoe.network.requests.ScholarshipRequest;
import com.xiaoe.network.requests.ScholarshipTaskStateRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.main.presenter.ScholarshipPresenter;
import com.xiaoe.shop.wxb.business.main.presenter.ScholarshipRangeAdapter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.interfaces.OnCustomScrollChangedListener;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.CustomScrollView;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;

public class NewScholarshipFragment extends BaseFragment implements OnRefreshListener, View.OnClickListener, OnCustomScrollChangedListener {

    private static final String TAG = "ScholarshipFragment";

    private Unbinder unbinder;
    private Context mContext;

    @BindView(R.id.scholarship_fresh_new)
    SmartRefreshLayout scholarshipNewRefresh;
    @BindView(R.id.scholarship_scroller_new)
    CustomScrollView scholarshipNewScroller;
    @BindView(R.id.scholarship_title_wrap_new)
    LinearLayout scholarshipNewTitleWrap;
    @BindView(R.id.scholarship_title_new)
    TextView scholarshipNewTitle;
    @BindView(R.id.scholarship_rule_new)
    TextView scholarshipNewRule;
    @BindView(R.id.scholarship_progress_new)
    ImageView scholarshipNewProgress;
    @BindView(R.id.scholarship_divide_new)
    TextView scholarshipNewDivide;
    @BindView(R.id.scholarship_real_range_new)
    TextView scholarshipNewRealRange;
    @BindView(R.id.scholarship_all_range_new)
    TextView scholarshipNewAllRange;
    @BindView(R.id.real_range_list_new)
    ListView scholarshipNewRealList;
    @BindView(R.id.all_range_list_new)
    ListView scholarshipNewAllList;
    @BindView(R.id.scholarship_loading_new)
    StatusPagerView scholarshipNewLoading;

    protected static final String RULE = "rule";
    protected static final String GO_BUY = "go_buy";

    MainActivity mainActivity;
    TouristDialog touristDialog;

    Dialog dialog;

    protected List<ScholarshipRangeItem> realList; // 实时榜
    protected List<ScholarshipRangeItem> allList; // 总榜

    ScholarshipPresenter scholarshipPresenter;

    boolean hasBuy = false;
    boolean isSuperVip = false;
    boolean hasEarnMoney = false;
    String taskDetailId; // 提交任务成功后的 id

    Handler handler = new Handler();
    Runnable runnable;
    String amount; // 拿到的奖学金或者积分
    private boolean showDataByDB = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scholarship_new, null, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();

        if (!mainActivity.isFormalUser) { // 不是正式用户
            touristDialog = new TouristDialog(getActivity());
            touristDialog.setDialogCloseClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    touristDialog.dismissDialog();
                }
            });
            touristDialog.setDialogConfirmClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    touristDialog.dismissDialog();
                    JumpDetail.jumpLogin(getActivity(), true);
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scholarshipNewLoading.setLoadingState(View.VISIBLE);
        scholarshipNewRefresh.setEnableLoadMore(false);
        scholarshipNewRefresh.setEnableOverScrollBounce(false);
        initListener();
    }

    private void initListener() {
        scholarshipNewRefresh.setOnRefreshListener(this);
        scholarshipNewRule.setOnClickListener(this);
        scholarshipNewDivide.setOnClickListener(this);
        scholarshipNewRealRange.setOnClickListener(this);
        scholarshipNewAllRange.setOnClickListener(this);
        scholarshipNewScroller.setScrollChanged(this);
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        setDataByDB();
        scholarshipPresenter = new ScholarshipPresenter(this);
        scholarshipPresenter.requestTaskList(true);
        if (ScholarshipEntity.getInstance().getIssueState() == ScholarshipEntity.SCHOLARSHIP_PROCESSING) { // 本来是处理中的话，重新请求拿到的结果
            if (scholarshipPresenter == null) {
                scholarshipPresenter = new ScholarshipPresenter(this);
            }
            scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), ScholarshipEntity.getInstance().getTaskDetailId());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        showDataByDB = false;
        if (scholarshipPresenter == null) {
            scholarshipPresenter = new ScholarshipPresenter(this);
        }
        scholarshipPresenter.requestTaskList(true);
        if (ScholarshipEntity.getInstance().getIssueState() == ScholarshipEntity.SCHOLARSHIP_PROCESSING) { // 本来是处理中的话，重新请求拿到的结果
            if (scholarshipPresenter == null) {
                scholarshipPresenter = new ScholarshipPresenter(this);
            }
            scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), ScholarshipEntity.getInstance().getTaskDetailId());
        }
    }

    private void setDataByDB(){
        SharedPreferencesUtil.getInstance(XiaoeApplication.getmContext(), SharedPreferencesUtil.FILE_NAME);
        String taskId = (String) SharedPreferencesUtil.getData(SharedPreferencesUtil.KEY_SCHOLARSHIP_ID, "");
        if(TextUtils.isEmpty(taskId)){
            return;
        }
        SQLiteUtil sqLiteUtil = SQLiteUtil.init(getContext(), new CacheDataUtil());
        //任务状态
        String sqlState = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
                +"' and resource_id='"+taskId+"_state' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
        List<CacheData> stateCacheData = sqLiteUtil.query(CacheDataUtil.TABLE_NAME, sqlState, null);
        if(stateCacheData != null && stateCacheData.size() > 0){
            JSONObject result = JSONObject.parseObject(stateCacheData.get(0).getContent()).getJSONObject("data");
            initTaskState(result);
            scholarshipNewRefresh.finishRefresh();
        }
        //排行榜
        String sqlRanking = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
                +"' and resource_id='"+taskId+"_ranking' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
        List<CacheData> rankingCacheData = sqLiteUtil.query(CacheDataUtil.TABLE_NAME, sqlRanking, null);
        if(rankingCacheData != null && rankingCacheData.size() > 0){
            JSONObject result = JSONObject.parseObject(rankingCacheData.get(0).getContent()).getJSONObject("data");
            initRange(result);
        }
        if(stateCacheData != null && stateCacheData.size() > 0
                && rankingCacheData != null && rankingCacheData.size() > 0){
            showDataByDB = true;
        }else{
            showDataByDB = false;
        }
        //奖学金金额
        String sqlMoney = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
                +"' and resource_id='"+taskId+"_money' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
        List<CacheData> moneyCacheData = sqLiteUtil.query(CacheDataUtil.TABLE_NAME, sqlMoney, null);
        if(moneyCacheData != null && moneyCacheData.size() > 0){
            String money = moneyCacheData.get(0).getContent();
            ScholarshipEntity.getInstance().setTaskTotalMoney(money);
            String htmlMoney = completeScholarshipTitle();
            scholarshipNewTitle.setText(Html.fromHtml(htmlMoney));
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.scholarship_rule_new:
                showDialogByType(RULE);
                break;
            case R.id.scholarship_divide_new:
                if (mainActivity.isFormalUser) {
                    if (!hasBuy) { // 没买
                        showDialogByType(GO_BUY);
                    } else { // 买了，根据领取状态进行不同操作
                        switch (ScholarshipEntity.getInstance().getIssueState()) {
                            case ScholarshipEntity.SCHOLARSHIP_FAIL: // 未发放
                                // TODO: 失败的操作
                                break;
                            case ScholarshipEntity.SCHOLARSHIP_PROCESSING: // 处理中
                                Toast.makeText(mainActivity, getString(R.string.scholarship_in_progress), Toast.LENGTH_SHORT).show();
                                break;
                            case ScholarshipEntity.SCHOLARSHIP_ISSUED: // 领取成功
                                showEarnDialog();
                                break;
                            default: // 没有给状态赋值，那就去分享
                                JumpDetail.jumpBoughtList(mContext, ScholarshipEntity.getInstance().getTaskId(), isSuperVip);
                                break;
                        }
                    }
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.scholarship_real_range_new:
                scholarshipNewRealList.setVisibility(View.VISIBLE);
                scholarshipNewAllList.setVisibility(View.GONE);
                scholarshipNewRealRange.setBackground(getActivity().getResources().getDrawable(R.drawable.recent_update_btn_pressed));
                scholarshipNewAllRange.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_column_menu));
                break;
            case R.id.scholarship_all_range_new:
                scholarshipNewRealList.setVisibility(View.GONE);
                scholarshipNewAllList.setVisibility(View.VISIBLE);
                scholarshipNewRealRange.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_column_menu));
                scholarshipNewAllRange.setBackground(getActivity().getResources().getDrawable(R.drawable.recent_update_btn_pressed));
                break;
            default:
                break;
        }
    }

    // 根据不同类型显示对话框
    private void showDialogByType(String type) {
        Window window;
        View view;
        ImageView dialogClose;
        TextView dialogTitle;
        TextView dialogContent;
        final TextView dialogBtn;
        switch (type) {
            case RULE:
                dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
                window = dialog.getWindow();
                view = getActivity().getLayoutInflater().inflate(R.layout.radius_dialog, null);

                dialogClose = (ImageView) view.findViewById(R.id.radius_dialog_close);
                dialogTitle = (TextView) view.findViewById(R.id.radius_dialog_title);
                dialogContent = (TextView) view.findViewById(R.id.radius_dialog_content);
                dialogBtn = (TextView) view.findViewById(R.id.radius_dialog_btn);

                dialogClose.setVisibility(View.GONE);
                dialogContent.setTextSize(14);
                dialogContent.setTextColor(getActivity().getResources().getColor(R.color.recent_list_color));
                dialogTitle.setText(mContext.getResources().getString(R.string.scholarship_dialog_rule_title));
                dialogContent.setText(mContext.getResources().getString(R.string.scholarship_dialog_rule_content));
                dialogBtn.setText(mContext.getResources().getString(R.string.scholarship_dialog_rule_btn));
                dialogBtn.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        dialog.dismiss();
                    }
                });
                // 先 show 后才会有宽高
                dialog.show();

                if (window != null) {
                    window.setGravity(Gravity.CENTER);
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    Point point = Global.g().getDisplayPixel();
                    layoutParams.width = (int) (point.x * 0.9);
                    window.setAttributes(layoutParams);
                }
                dialog.setContentView(view);
                break;
            case GO_BUY:
                dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
                window = dialog.getWindow();
                view = getActivity().getLayoutInflater().inflate(R.layout.radius_dialog, null);

                dialogClose = (ImageView) view.findViewById(R.id.radius_dialog_close);
                dialogTitle = (TextView) view.findViewById(R.id.radius_dialog_title);
                dialogContent = (TextView) view.findViewById(R.id.radius_dialog_content);
                dialogBtn = (TextView) view.findViewById(R.id.radius_dialog_btn);

                dialogClose.setVisibility(View.VISIBLE);
                dialogTitle.setVisibility(View.GONE);
                dialogContent.setText(getActivity().getResources().getString(R.string.scholarship_dialog_go_buy_content));
                dialogContent.setTextSize(16);
                dialogContent.setTextColor(getActivity().getResources().getColor(R.color.main_title_color));
                dialogBtn.setText(getActivity().getResources().getString(R.string.scholarship_dialog_go_buy_btn));
                dialogClose.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialogBtn.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        ((MainActivity) getActivity()).replaceFragment(1);
                        dialog.dismiss();
                    }
                });
                dialog.show();

                if (window != null) {
                    window.setGravity(Gravity.CENTER);
                    WindowManager.LayoutParams layoutParams = window.getAttributes();
                    Point point = Global.g().getDisplayPixel();
                    layoutParams.width = (int) (point.x * 0.8);
                    window.setAttributes(layoutParams);
                }
                dialog.setContentView(view);
                break;
            default:
                break;
        }
    }

    @Override
    public void onScrollChanged(int l, int t, int oldl, int oldt) {

    }

    @Override
    public void onLoadState(int state) {

    }

    @Subscribe
    public void obtainEvent(TaskDetailIdEvent taskDetailIdEvent) {
        if (taskDetailIdEvent != null) {
            taskDetailId = taskDetailIdEvent.getTaskDetailId();
            ScholarshipEntity.getInstance().setTaskDetailId(taskDetailId);
            scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), taskDetailId);
        }
    }

    @Subscribe
    public void obtainEvent(ChangeLoginIdentityEvent changeLoginIdentityEvent) {
        if (changeLoginIdentityEvent != null && changeLoginIdentityEvent.isChangeSuccess()) {
            if (scholarshipPresenter == null) {
                scholarshipPresenter = new ScholarshipPresenter(this);
            }
            scholarshipPresenter.requestTaskList(true);
            if (ScholarshipEntity.getInstance().getIssueState() == ScholarshipEntity.SCHOLARSHIP_PROCESSING) { // 本来是处理中的话，重新请求拿到的结果
                if (scholarshipPresenter == null) {
                    scholarshipPresenter = new ScholarshipPresenter(this);
                }
                scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), ScholarshipEntity.getInstance().getTaskDetailId());
            }
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        if (success) {
            JSONObject data = (JSONObject) entity;
            if (iRequest instanceof ScholarshipRequest) {
                int code = data.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject result = (JSONObject) data.get("data");
                    initRange(result);
                } else if (code == NetworkCodes.CODE_REQUEST_ERROR) {
                    Log.d(TAG, "onMainThreadResponse: 获取奖学金列表失败...");
                    if(!showDataByDB){
                        scholarshipNewLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                    }
                }
            } else if (iRequest instanceof ScholarshipTaskStateRequest) {
                int code = data.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject result = (JSONObject) data.get("data");
                    initTaskState(result);
                    scholarshipNewRefresh.finishRefresh();
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取奖学金任务状态失败...");
                    scholarshipNewRefresh.finishRefresh();
                    if(!showDataByDB){
                        scholarshipNewLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                    }
                }
            } else if (iRequest instanceof ScholarshipReceiveRequest) {
                int code = data.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    // 获取成功之后查询领取结果接口
                    JSONObject result = (JSONObject) data.get("data");
                    updatePageState(result);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取奖学金失败...");
                    scholarshipNewLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                }
            }
        } else {
            scholarshipNewRefresh.finishRefresh();
            if(!showDataByDB){
                if (TextUtils.isEmpty(ScholarshipEntity.getInstance().getTaskTotalMoney())) { // 为空，不显示页面
                    scholarshipNewLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                } else { // 不为空
                    scholarshipNewLoading.setLoadingFinish();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error_text), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 初始化排行榜
    private void initRange(JSONObject data) {
        JSONArray realTime = (JSONArray) data.get("real_time");
        JSONArray allTime = (JSONArray) data.get("all_time");

        realList = new ArrayList<>();
        allList = new ArrayList<>();

        initRangeData(realTime, true);
        initRangeData(allTime, false);

        ScholarshipRangeAdapter realRangeAdapter = new ScholarshipRangeAdapter(mContext, realList);
        ScholarshipRangeAdapter allRangeAdapter = new ScholarshipRangeAdapter(mContext, allList);

        scholarshipNewRealList.setAdapter(realRangeAdapter);
        scholarshipNewAllList.setAdapter(allRangeAdapter);

        MeasureUtil.setListViewHeightBasedOnChildren(scholarshipNewRealList);
        MeasureUtil.setListViewHeightBasedOnChildren(scholarshipNewAllList);

        // 初始化的时候默认显示实时榜
        scholarshipNewRealRange.setBackground(getActivity().getResources().getDrawable(R.drawable.recent_update_btn_pressed));
        scholarshipNewAllRange.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_column_menu));
        scholarshipNewRealList.setVisibility(View.VISIBLE);
        scholarshipNewAllList.setVisibility(View.GONE);
    }

    /**
     * 初始化数据
     * @param dataArray 返回的数据
     * @param isReal 是否实时
     */
    private void initRangeData(JSONArray dataArray, boolean isReal) {

        for (int i = 0; i < dataArray.size(); i++) {
            JSONObject itemJson = (JSONObject) dataArray.get(i);

            ScholarshipRangeItem scholarshipRangeItem = new ScholarshipRangeItem();
            String userId = itemJson.getString("user_id");
            String wxNickname = itemJson.getString("wx_nickname");
            String wxAvatar = itemJson.getString("wx_avatar");
            isSuperVip = itemJson.getBoolean("is_supermember") == null ? false : itemJson.getBoolean("is_supermember");
            int money = itemJson.getInteger("all_money");
            String range = String.valueOf(i + 1);
            String scholarship = String.format("%.2f%s",money / 100f,"元");

            scholarshipRangeItem.setItemRange(range);
            scholarshipRangeItem.setItemAvatar(wxAvatar);
            scholarshipRangeItem.setItemName(wxNickname);
            scholarshipRangeItem.setItemScholarship(scholarship);
            scholarshipRangeItem.setItemUserId(userId);
            scholarshipRangeItem.setSuperVip(isSuperVip);

            if (isReal) {
                realList.add(scholarshipRangeItem);
            } else {
                allList.add(scholarshipRangeItem);
            }
        }
    }

    // 初始化任务状态
    private void initTaskState(JSONObject data) {
        JSONArray node = (JSONArray) data.get("node");
        int status = data.getInteger("status");
        if (node != null) {
            for (Object item : node) {
                if (item.equals("is_share")) {
                    // 已经分享
                    Log.d(TAG, "initTaskState: is_share");
                } else if (item.equals("is_pay")) {
                    // 已经购买了
                    hasBuy = true;
                }
            }
        }
        switch (status) {
            case 1: // 已领取 -- 显示明日再来
                ScholarshipEntity.getInstance().setTaskState(ScholarshipEntity.TASK_RECEIVED);
                scholarshipNewDivide.setText("明日再来");
                scholarshipNewDivide.setAlpha(0.8f);
                scholarshipNewDivide.setClickable(false);
                // 三个步骤都执行完成
                Glide.with(this).load(R.mipmap.scholarship_process_three).into(scholarshipNewProgress);
                break;
            case 2: // 当前任务不满足领取条件 -- 判断是否已经购买
                ScholarshipEntity.getInstance().setTaskState(ScholarshipEntity.TASK_UNFINISHED);
                if (hasBuy) { // 买了
                    Glide.with(this).load(R.mipmap.scholarship_process_one);
                } else { // 没买
                    Glide.with(this).load(R.mipmap.scholarship_process);
                }
                break;
            case 3: // 未领取 -- 已经购买了
                ScholarshipEntity.getInstance().setTaskState(ScholarshipEntity.TASK_NOT_RECEIVED);
                Glide.with(this).load(R.mipmap.scholarship_process_one);
                break;
            default:
                break;
        }
        String money = completeScholarshipTitle();
        scholarshipNewTitle.setText(Html.fromHtml(money));
        scholarshipNewLoading.setLoadingFinish();
    }

    private String completeScholarshipTitle() {
        String htmlTextStart = "<font color='#FDC200'><big>";
        String htmlTextEnd = "</big></font>";
        if (TextUtils.isEmpty(ScholarshipEntity.getInstance().getTaskTotalMoney())) {
            return "";
        } else {
            return String.format("每天瓜分 %s%s%s 元现金", htmlTextStart, ScholarshipEntity.getInstance().getTaskTotalMoney(), htmlTextEnd);
        }
    }

    // 显示收益对话框
    private void showEarnDialog() {
        dialog = new Dialog(mContext, R.style.ActionSheetDialogStyle);
        Window window = dialog.getWindow();
        View view = getActivity().getLayoutInflater().inflate(R.layout.scholarship_dialog, null);
        ImageView earnClose = (ImageView) view.findViewById(R.id.scholarship_dialog_close);
        LinearLayout earnWrap = (LinearLayout) view.findViewById(R.id.scholarship_content_wrap);
        TextView earnTitle = (TextView) view.findViewById(R.id.scholarship_dialog_title);
        TextView earnContent = (TextView) view.findViewById(R.id.scholarship_dialog_content);
        TextView earnContentTail = (TextView) view.findViewById(R.id.scholarship_dialog_content_tail);
        TextView earnTip = (TextView) view.findViewById(R.id.scholarship_dialog_tip);
        TextView earnSubmit = (TextView) view.findViewById(R.id.scholarship_dialog_submit);

        if (hasEarnMoney) { // 拿到钱了
            earnTitle.setText("恭喜你，获得奖学金");
            earnWrap.setBackground(getActivity().getResources().getDrawable(R.mipmap.scholarship_popup_bg));
            earnContent.setText(amount);
            earnContentTail.setVisibility(View.VISIBLE);
            earnTip.setVisibility(View.GONE);
            earnSubmit.setText(getString(R.string.scholarship_earn_btn));
            earnSubmit.setOnClickListener(new DebouncingOnClickListener() {
                @Override
                public void doClick(View v) {
                    JumpDetail.jumpScholarshipActivity(getActivity());
                    dialog.dismiss();
                }
            });
        } else {
            earnTitle.setText("差一点就瓜分到了");
            earnContentTail.setVisibility(View.GONE);
            earnWrap.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            earnSubmit.setText(getString(R.string.scholarship_earn_integral));
            if (CommonUserInfo.isIsSuperVip()) { // 超级会员
                if (amount == null) {
                    amount = "20";
                }
                earnContent.setText(String.format(getString(R.string.giving_you_credits), amount));
                earnTip.setVisibility(View.GONE);
            } else {
                if (amount == null) {
                    amount = "10";
                }
                earnContent.setText(String.format(getString(R.string.giving_you_credits), amount));
                earnContent.setTextSize(20);
                earnContent.setTextColor(getActivity().getResources().getColor(R.color.scholarship_btn_press));
                earnTip.setVisibility(View.GONE);
            }
            earnSubmit.setOnClickListener(new DebouncingOnClickListener() {
                @Override
                public void doClick(View v) {
                    if (hasEarnMoney) {
                        JumpDetail.jumpScholarshipActivity(getActivity());
                    } else {
                        JumpDetail.jumpIntegralActivity(getActivity());
                    }
                    dialog.dismiss();
                }
            });
        }
        earnClose.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                dialog.dismiss();
            }
        });
        // 先 show 后才会有宽高
        dialog.show();

        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            Point point = Global.g().getDisplayPixel();
            layoutParams.width = (int) (point.x * 0.8);
            window.setAttributes(layoutParams);
        }
        dialog.setContentView(view);
    }

    // 更新页面状态
    private void updatePageState(JSONObject data) {
        int status = data.getInteger("status");
        JSONObject reward = (JSONObject) data.get("reward");
        if (status == 3 && reward != null) { // 已经完成并拿到数据
            ScholarshipEntity.getInstance().setIssueState(ScholarshipEntity.SCHOLARSHIP_ISSUED);

            scholarshipNewDivide.setText("明日再来");
            scholarshipNewDivide.setAlpha(0.8f);
            scholarshipNewDivide.setClickable(false);
            Glide.with(this).load(R.mipmap.scholarship_process_three).into(scholarshipNewProgress);

            // TODO: 禁止点击事件
            int type = reward.getInteger("type");
            int amount = reward.getInteger("amount");
            if (type == 1) { // 拿到钱
                hasEarnMoney = true;
                BigDecimal priceTop = new BigDecimal(amount);
                BigDecimal priceBottom = new BigDecimal(100);
                this.amount = priceTop.divide(priceBottom, 2, BigDecimal.ROUND_HALF_UP).toPlainString();
            } else if (type == 2) { // 拿到积分
                hasEarnMoney = false;
                this.amount = String.valueOf(amount);
            }
            showEarnDialog();
        } else if (status == 2) { // 处理中
            ScholarshipEntity.getInstance().setIssueState(ScholarshipEntity.SCHOLARSHIP_PROCESSING);
            scholarshipNewDivide.setText("瓜分中...");

            Glide.with(this).load(R.mipmap.scholarship_process_three).into(scholarshipNewProgress);

            runnable = () -> scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), ScholarshipEntity.getInstance().getTaskDetailId());
            handler.postDelayed(runnable, 3000);
        } else if (status == 1) {
            ScholarshipEntity.getInstance().setIssueState(ScholarshipEntity.SCHOLARSHIP_FAIL);
            Toast.makeText(getActivity(), "领取失败，请重试", Toast.LENGTH_SHORT).show();
            Glide.with(this).load(R.mipmap.scholarship_process_one).into(scholarshipNewProgress);
        }
    }
}
