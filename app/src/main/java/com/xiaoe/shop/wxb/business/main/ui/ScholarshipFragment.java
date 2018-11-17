package com.xiaoe.shop.wxb.business.main.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.ScholarshipEntity;
import com.xiaoe.common.entitys.ScholarshipRangeItem;
import com.xiaoe.common.entitys.TaskDetailIdEvent;
import com.xiaoe.common.utils.MeasureUtil;
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
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.ListBottomLoadMoreView;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

public class ScholarshipFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "ScholarshipFragment";

    private Unbinder unbinder;
    private Context mContext;

    @BindView(R.id.scholarship_rule)
    TextView scholarshipRule;
    @BindView(R.id.scholarship_step_one)
    ImageView scholarshipStepOne;
    @BindView(R.id.scholarship_step_two)
    ImageView scholarshipStepTwo;
    @BindView(R.id.scholarship_step_three)
    ImageView scholarshipStepThree;
    @BindView(R.id.scholarship_divide)
    Button scholarshipDivide;
    @BindView(R.id.scholarship_real_range)
    TextView scholarshipRealRange;
    @BindView(R.id.scholarship_all_range)
    TextView scholarshipAllRange;
    @BindView(R.id.real_range_list)
    ListView scholarshipRealList;
    @BindView(R.id.all_range_list)
    ListView scholarshipAllList;
    @BindView(R.id.scholarship_bottom_load_more)
    ListBottomLoadMoreView scholarshipLoadMore;
    @BindView(R.id.scholarship_loading)
    StatusPagerView scholarshipLoading;

    protected static final String RULE = "rule";
    protected static final String GO_BUY = "go_buy";

    Dialog dialog;

    protected List<ScholarshipRangeItem> realList; // 实时榜
    protected List<ScholarshipRangeItem> allList; // 总榜

    ScholarshipPresenter scholarshipPresenter;

    boolean hasBuy = false;
    boolean isSuperVip = false;
    boolean hasEarnMoney = false;
    String taskDetailId; // 提交任务成功后的 id

    MainActivity mainActivity;
    TouristDialog touristDialog;

    Handler handler = new Handler();
    Runnable runnable;
    String amount; // 拿到的奖学金或者积分

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scholarship, null, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();

        if (!mainActivity.isFormalUser) { // 不是正式用户
            touristDialog = new TouristDialog(getActivity());
            touristDialog.setDialogCloseClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                }
            });
            touristDialog.setDialogConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                    JumpDetail.jumpLogin(getActivity(), true);
                }
            });
        }

        view.setPadding(0, StatusBarUtil.getStatusBarHeight(mContext), 0, 0);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        scholarshipLoading.setVisibility(View.VISIBLE);
        scholarshipLoading.setLoadingState(View.VISIBLE);
        initListener();
    }

    private void initListener() {
        scholarshipRule.setOnClickListener(this);
        scholarshipDivide.setOnClickListener(this);
        scholarshipRealRange.setOnClickListener(this);
        scholarshipAllRange.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.scholarship_rule:
                showDialogByType(RULE);
                break;
            case R.id.scholarship_divide:
                if (mainActivity.isFormalUser) {
                    if (!hasBuy) { // 没买
                        showDialogByType(GO_BUY);
                    } else { // 买了，根据领取状态进行不同操作
                        switch (ScholarshipEntity.getInstance().getIssueState()) {
                            case ScholarshipEntity.SCHOLARSHIP_FAIL: // 领取失败
                                // TODO: 失败的操作
                                break;
                            case ScholarshipEntity.SCHOLARSHIP_PROCESSING: // 处理中
                                Toast.makeText(mainActivity, "奖学金发放中", Toast.LENGTH_SHORT).show();
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
            case R.id.scholarship_real_range:
                scholarshipRealList.setVisibility(View.VISIBLE);
                scholarshipAllList.setVisibility(View.GONE);
                scholarshipRealRange.setBackground(getActivity().getResources().getDrawable(R.drawable.recent_update_btn_pressed));
                scholarshipAllRange.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_column_menu));
                break;
            case R.id.scholarship_all_range:
                scholarshipRealList.setVisibility(View.GONE);
                scholarshipAllList.setVisibility(View.VISIBLE);
                scholarshipRealRange.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_column_menu));
                scholarshipAllRange.setBackground(getActivity().getResources().getDrawable(R.drawable.recent_update_btn_pressed));
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
                dialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                dialogClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialogBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        scholarshipPresenter = new ScholarshipPresenter(this);
        scholarshipPresenter.requestTaskList(true);
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
                    Log.d(TAG, "onMainThreadResponse: 请求失败...");
                }
            } else if (iRequest instanceof ScholarshipTaskStateRequest) {
                int code = data.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject result = (JSONObject) data.get("data");
                    initTaskState(result);
                } else {
                    Log.d(TAG, "onMainThreadResponse: request fail...");
                }
            } else if (iRequest instanceof ScholarshipReceiveRequest) {
                int code = data.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    // 获取成功之后查询领取结果接口
                    JSONObject result = (JSONObject) data.get("data");
                    updatePageState(result);
                } else {
                    Log.d(TAG, "onMainThreadResponse: request fail...");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail, params error error maybe...");
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

        scholarshipRealList.setAdapter(realRangeAdapter);
        scholarshipAllList.setAdapter(allRangeAdapter);

        MeasureUtil.setListViewHeightBasedOnChildren(scholarshipRealList);
        MeasureUtil.setListViewHeightBasedOnChildren(scholarshipAllList);

        // 初始化的时候默认显示实时榜
        scholarshipRealRange.setBackground(getActivity().getResources().getDrawable(R.drawable.recent_update_btn_pressed));
        scholarshipAllRange.setBackground(getActivity().getResources().getDrawable(R.drawable.btn_column_menu));
        scholarshipRealList.setVisibility(View.VISIBLE);
        scholarshipAllList.setVisibility(View.GONE);
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
            String scholarship = String.valueOf(money / 100);

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
                scholarshipDivide.setText("明日再来");
                scholarshipDivide.setBackground(getActivity().getResources().getDrawable(R.drawable.divide_btn_content_bg));
                scholarshipDivide.setAlpha(0.8f);
                scholarshipDivide.setClickable(false);
                // 三个步骤都执行完成
                scholarshipStepOne.setVisibility(View.VISIBLE);
                scholarshipStepTwo.setVisibility(View.VISIBLE);
                scholarshipStepThree.setVisibility(View.VISIBLE);
                break;
            case 2: // 当前任务不满足领取条件 -- 判断是否已经购买
                ScholarshipEntity.getInstance().setTaskState(ScholarshipEntity.TASK_UNFINISHED);
                if (hasBuy) { // 买了
                    scholarshipStepOne.setVisibility(View.VISIBLE);
                    scholarshipStepTwo.setVisibility(View.GONE);
                    scholarshipStepThree.setVisibility(View.GONE);
                } else { // 没买
                    scholarshipStepOne.setVisibility(View.GONE);
                    scholarshipStepTwo.setVisibility(View.GONE);
                    scholarshipStepThree.setVisibility(View.GONE);
                }
                break;
            case 3: // 未领取 -- 已经购买了
                ScholarshipEntity.getInstance().setTaskState(ScholarshipEntity.TASK_NOT_RECEIVED);
                scholarshipStepOne.setVisibility(View.GONE);
                scholarshipStepTwo.setVisibility(View.GONE);
                scholarshipStepThree.setVisibility(View.GONE);
                break;
        }
        scholarshipLoading.setLoadingState(View.GONE);
        scholarshipLoading.setVisibility(View.GONE);
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
            earnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpDetail.jumpScholarshipActivity(getActivity());
                    dialog.dismiss();
                }
            });
        } else {
            earnTitle.setText("差一点就瓜分到了");
            earnContentTail.setVisibility(View.GONE);
            earnWrap.setBackgroundColor(getActivity().getResources().getColor(R.color.white));
            if (isSuperVip) { // 超级会员
                String content = "送你" + amount + "积分";
                earnContent.setText(content);
                earnTip.setVisibility(View.VISIBLE);
            } else {
                String content = "送你" + amount + "积分";
                earnContent.setText(content);
                earnContent.setTextSize(20);
                earnContent.setTextColor(getActivity().getResources().getColor(R.color.scholarship_btn_press));
                earnTip.setVisibility(View.GONE);
            }
            earnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpDetail.jumpScholarshipActivity(getActivity());
                    dialog.dismiss();
                }
            });
        }
        earnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Subscribe
    public void obtainTaskDetailId(TaskDetailIdEvent taskDetailIdEvent) {
        if (taskDetailIdEvent != null) {
            taskDetailId = taskDetailIdEvent.getTaskDetailId();
            ScholarshipEntity.getInstance().setTaskDetailId(taskDetailId);
            scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), taskDetailId);
        }
    }

    // 更新页面状态
    private void updatePageState(JSONObject data) {
        int status = data.getInteger("status");
        JSONObject reward = (JSONObject) data.get("reward");
        if (status == 3 && reward != null) { // 已经完成并拿到数据
            ScholarshipEntity.getInstance().setIssueState(ScholarshipEntity.SCHOLARSHIP_ISSUED);

            scholarshipDivide.setText("明日再来");
            scholarshipDivide.setAlpha(0.8f);
            scholarshipStepOne.setVisibility(View.VISIBLE);
            scholarshipStepTwo.setVisibility(View.VISIBLE);
            scholarshipStepThree.setVisibility(View.VISIBLE);

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
            scholarshipDivide.setText("瓜分中...");

            scholarshipStepOne.setVisibility(View.VISIBLE);
            scholarshipStepTwo.setVisibility(View.VISIBLE);
            scholarshipStepThree.setVisibility(View.VISIBLE);

            runnable = () -> scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), ScholarshipEntity.getInstance().getTaskDetailId());
            handler.postDelayed(runnable, 3000);
        } else if (status == 1) {
            ScholarshipEntity.getInstance().setIssueState(ScholarshipEntity.SCHOLARSHIP_FAIL);
            Toast.makeText(getActivity(), "领取失败，请重试", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) { // 隐藏
            if (handler != null && runnable != null) {
                handler.removeCallbacks(runnable);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) { // 可见的时候
            if (ScholarshipEntity.getInstance().getIssueState() == ScholarshipEntity.SCHOLARSHIP_PROCESSING) { // 本来是处理中的话，重新请求拿到的结果
                if (scholarshipPresenter == null) {
                    scholarshipPresenter = new ScholarshipPresenter(this);
                }
                scholarshipPresenter.queryReceiveResult(ScholarshipEntity.getInstance().getTaskId(), ScholarshipEntity.getInstance().getTaskDetailId());
            }
        }
    }
}
