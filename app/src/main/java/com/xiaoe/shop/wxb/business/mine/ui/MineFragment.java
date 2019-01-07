package com.xiaoe.shop.wxb.business.mine.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.db.LrSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.entitys.ChangeLoginIdentityEvent;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.GetSuperMemberSuccessEvent;
import com.xiaoe.common.entitys.LearningRecord;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.entitys.LrEntity;
import com.xiaoe.common.entitys.MineMoneyItemInfo;
import com.xiaoe.common.entitys.UpdateMineMsgEvent;
import com.xiaoe.common.interfaces.OnItemClickWithMoneyItemListener;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.EarningRequest;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.IsSuperVipRequest;
import com.xiaoe.network.requests.MineLearningRequest;
import com.xiaoe.network.requests.SuperVipBuyInfoRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.earning.presenter.EarningPresenter;
import com.xiaoe.shop.wxb.business.main.ui.MainActivity;
import com.xiaoe.shop.wxb.business.mine.presenter.MineEquityListAdapter;
import com.xiaoe.shop.wxb.business.mine.presenter.MineLearningListAdapter;
import com.xiaoe.shop.wxb.business.mine.presenter.MoneyWrapRecyclerAdapter;
import com.xiaoe.shop.wxb.business.mine_learning.presenter.MineLearningPresenter;
import com.xiaoe.shop.wxb.business.super_vip.presenter.SuperVipPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.events.OnUnreadMsgEvent;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.UpdateLearningUtils;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;

public class MineFragment extends BaseFragment implements AdapterView.OnItemClickListener, OnItemClickWithMoneyItemListener, OnRefreshListener {

    private static final String TAG = "MineFragment";

    private Unbinder unbinder;
    private Context mContext;

    @BindView(R.id.mine_refresh)
    SmartRefreshLayout mineRefresh;
    @BindView(R.id.mine_title_wrap)
    LinearLayout mineWrap;
    @BindView(R.id.mine_title_view)
    MineTitleView mineTitleView;
    @BindView(R.id.mine_msg_view)
    MineMsgView mineMsgView;
    @BindView(R.id.mine_vip_view)
    MineVipCard mineVipCard;
//    @BindView(R.id.mine_money_view)
//    MineMoneyWrapView mineMoneyWrapView;
    @BindView(R.id.mine_learning_view)
    MineLearningWrapView mineLearningWrapView;
    @BindView(R.id.mine_loading)
    StatusPagerView mineLoading;
    @BindView(R.id.mine_bo_bi_wrap)
    LinearLayout mineBoBiWrap;
    @BindView(R.id.mine_bo_bi_amount)
    TextView mineBoBiAmount;

    MainActivity mainActivity;

    // 去掉奖学金与积分的请求接口（重新放开，用于请求账户波豆余额）
    EarningPresenter earningPresenter;
    MineLearningPresenter mineLearningPresenter;

    String balance;

    MineMoneyItemInfo item_1; // 奖学金
    MineMoneyItemInfo item_2; // 积分
    List<MineMoneyItemInfo> itemInfoList; // 奖学金集合
    boolean isScholarshipFinish; // 奖学金请求完成
    boolean isIntegralFinish;    // 积分请求完成
    boolean isMineLearningFinish; // 我正在学请求完成

    List<String> contentList;   // 权益集合
    MineEquityListAdapter mineEquityListAdapter; // 权益适配器

    TouristDialog touristDialog;
    boolean hasDecorate;

    MoneyWrapRecyclerAdapter moneyWrapRecyclerAdapter; // 金钱适配器
    MineLearningListAdapter learningListAdapter; // 学习记录适配器
    private boolean showDataByDB = false;
    SuperVipPresenter superVipPresenter;
    String[] vipTips;
    /**
     * 未读消息数
     */
    private int saveCount;
    /**
     * 页面停留时长
     */
    private long pageDuration;
    /**
     * 在主页面中的索引位置
     */
    private int tabIndex;

    String mineLearningId; // 我正在学资源 id
    String mineLearningType; // 我正在学资源类型
    String mineLearningTitle; // 我正在学标题
    String mineLearningDesc; // 我正在学描述
    String mineLearningImgUrl; // 我正在学图片链接

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            tabIndex = bundle.getInt("tabIndex");
        }
        EventBus.getDefault().register(this);
        // 网络请求数据代码
//        MinePresenter minePresenter = new MinePresenter(this);
        vipTips = new String[]{getString(R.string.free_admission), getString(R.string.free_friends_and_relatives), getString(R.string.activity_priority)};
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        mineLoading.setLoadingState(View.VISIBLE);
        view.setPadding(0, StatusBarUtil.getStatusBarHeight(mContext), 0, 0);
        initListener();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainActivity.getUnreadMsg();
        pageDuration = System.currentTimeMillis();
//        Log.e("EventReportManager", "onResume: pageDuration " + pageDuration);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (tabIndex == mainActivity.getCurrentPosition()) {
            eventReportDuration();
        }
//        Log.e("EventReportManager", "onPause: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void eventReportDuration() {
        EventReportManager.onEventValue(mContext, MobclickEvent.MINE_PAGEVIEW_DURATION, (int) (System.currentTimeMillis() - pageDuration));
    }

    @Override
    public void refreshReportDuration() {
        pageDuration = System.currentTimeMillis();
//        Log.e("EventReportManager", "refresh 我的: pageDuration " + pageDuration);
    }

    public static MineFragment newInstance(int tabIndex) {
        MineFragment fragment = new MineFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("tabIndex", tabIndex);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        if (earningPresenter == null) {
            earningPresenter = new EarningPresenter(this);
        }
        isScholarshipFinish = false;
        isIntegralFinish = false;
        isMineLearningFinish = false;
//        earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
//        earningPresenter.requestLaundryList(Constants.INTEGRAL_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
        earningPresenter.requestAccountBalance();
        if (!mainActivity.isFormalUser) {

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
        //先初始化缓存中的数据
        setDataByDB();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            List<LoginUser> loginMsg = getLoginUserList();

            SQLiteUtil sqLiteUtil = SQLiteUtil.init(getActivity(), new LrSQLiteCallback());
            if (!sqLiteUtil.tabIsExist(LrSQLiteCallback.TABLE_NAME_LR)) {
                sqLiteUtil.execSQL(LrSQLiteCallback.TABLE_SCHEMA_LR);
            }
            List<LearningRecord> lrList =  sqLiteUtil.query(LrSQLiteCallback.TABLE_NAME_LR, "select * from " + LrSQLiteCallback.TABLE_NAME_LR, null);
            if (lrList.size() == 1) {
                LearningRecord lr = lrList.get(0);
                mineLearningId = lr.getLrId();
                mineLearningType = lr.getLrType();
                mineLearningTitle = lr.getLrTitle();
                mineLearningDesc = lr.getLrDesc();
                mineLearningImgUrl = lr.getLrImg();
            } else {
                if (mineLearningPresenter == null) {
                    mineLearningPresenter = new MineLearningPresenter(this);
                }
                mineLearningPresenter.requestLearningData(1, 1);
            }

            if (loginMsg.size() == 1) {
                initMineMsg();
                initData();
            } else {
                initTouristData();
            }
        }
    }

    private void initTouristData() {
        // 游客登录
        mineMsgView.setNickName(getString(R.string.click_login));
        mineMsgView.setAvatar("res:///" + R.mipmap.default_avatar);
        mineVipCard.setVisibility(View.GONE);
        mineMsgView.setBuyVipVisibility(View.VISIBLE);
        mineMsgView.setBuyVipCommon();
        // 超级会员隐藏后奖学金的位置
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int left = Dp2Px2SpUtil.dp2px(mContext, 20);
        int top = Dp2Px2SpUtil.dp2px(mContext, -36);
        int right = Dp2Px2SpUtil.dp2px(mContext, 20);
        layoutParams.setMargins(left, top, right ,0);
//        mineMoneyWrapView.setLayoutParams(layoutParams);
        // 金钱容器（未登录状态）
        List<MineMoneyItemInfo> tempList = new ArrayList<>();
        MineMoneyItemInfo item_1_temp = new MineMoneyItemInfo();
        item_1_temp.setItemTitle("0.00");
        item_1_temp.setItemDesc(getString(R.string.scholarship_title));
        MineMoneyItemInfo item_2_temp = new MineMoneyItemInfo();
        item_2_temp.setItemTitle("0");
        item_2_temp.setItemDesc(getString(R.string.integral));
        tempList.add(item_1_temp);
        tempList.add(item_2_temp);
        MoneyWrapRecyclerAdapter moneyWrapRecyclerAdapter = new MoneyWrapRecyclerAdapter(mContext, tempList);
        moneyWrapRecyclerAdapter.setOnItemClickWithMoneyItemListener(this);
//        mineMoneyWrapView.setMoneyRecyclerAdapter(moneyWrapRecyclerAdapter);
        MineLearningListAdapter learningListAdapter = new MineLearningListAdapter(getActivity());
        mineLearningWrapView.setLearningListAdapter(learningListAdapter);
        mineLearningWrapView.setLearningContainerVisibility(View.GONE);
        mineLearningWrapView.setLearningLoginDescVisibility(View.VISIBLE);
    }

    private void initData() {
        if (CommonUserInfo.isIsSuperVip()) { // 是超级会员
            mineMsgView.setBuyVipVisibility(View.VISIBLE);
            mineMsgView.setBuyVipTag();
        } else {
            if (CommonUserInfo.isIsSuperVipAvailable()) { // 超级会员可购
                mineMsgView.setBuyVipVisibility(View.VISIBLE);
                mineMsgView.setBuyVipCommon();
            } else {
                mineMsgView.setBuyVipVisibility(View.GONE);
            }
        }
        if (CommonUserInfo.isIsSuperVip()) { // 是超级会员，显示卡片
            // 会员权益假数据 -- 开始
            if (contentList == null) {
                contentList = new ArrayList<>();
                contentList.addAll(Arrays.asList(vipTips));
                mineEquityListAdapter = new MineEquityListAdapter(mContext, contentList);
                mineVipCard.setEquityListAdapter(mineEquityListAdapter);
            }
            // 会员权益假数据 -- 结束
            mineVipCard.setVisibility(View.VISIBLE);
            if (CommonUserInfo.isIsSuperVipAvailable()) { // 超级会员可购
                mineVipCard.setBtnRenewalVisibility(View.VISIBLE);
            } else {
                mineVipCard.setBtnRenewalVisibility(View.GONE);
            }
            mineVipCard.setDeadLine(mainActivity.expireAt);
        } else {
            mineVipCard.setVisibility(View.GONE);
        }
        if (mineVipCard.getVisibility() == View.VISIBLE) { // 超级会员卡片存在
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = Dp2Px2SpUtil.dp2px(mContext, 0);
            int top = Dp2Px2SpUtil.dp2px(mContext, 16);
            int right = Dp2Px2SpUtil.dp2px(mContext, 0);
            layoutParams.setMargins(left,top,right,0);
//            mineMoneyWrapView.setLayoutParams(layoutParams);
        } else { // 超级会员卡片不存在
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = Dp2Px2SpUtil.dp2px(mContext, 0);
            int top = Dp2Px2SpUtil.dp2px(mContext, 0);
            int right = Dp2Px2SpUtil.dp2px(mContext, 0);
            layoutParams.setMargins(left,top,right,0);
//            mineMoneyWrapView.setLayoutParams(layoutParams);
        }
        // 金钱容器
        if (itemInfoList == null) {
            itemInfoList = new ArrayList<>();
        }
        // 我正在学
        if (!TextUtils.isEmpty(mineLearningId)) {
            mineLearningWrapView.setLearningLoginDescVisibility(View.GONE);
            mineLearningWrapView.setLearningContainerVisibility(View.VISIBLE);
            mineLearningWrapView.setLearningMoreVisibility(View.VISIBLE);
            mineLearningWrapView.setLearningTitle(mineLearningTitle);
            mineLearningWrapView.setLearningIconURI(mineLearningImgUrl, mineLearningType);
            mineLearningWrapView.setLearningUpdate(mineLearningDesc);
        } else {
            mineLearningWrapView.setLearningLoginDescVisibility(View.VISIBLE);
            mineLearningWrapView.setLearningLoginDesc(getString(R.string.none_learning_course));
            mineLearningWrapView.setLearningContainerVisibility(View.GONE);
            mineLearningWrapView.setLearningMoreVisibility(View.GONE);
        }
        initPageData();
    }

    private void initListener() {
        mineRefresh.setOnRefreshListener(this);
        mineRefresh.setEnableOverScrollBounce(false);
        mineTitleView.setMsgClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpHistoryMessage(mContext);
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        mineTitleView.setSettingListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpAccount(mContext);
                    saveCount = 0;
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        mineMsgView.setBuyVipClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mainActivity.isFormalUser) {
                    if (!CommonUserInfo.isIsSuperVip()) { // 不是超级会员
                        JumpDetail.jumpSuperVip(mContext);

                        EventReportManager.onEvent(mContext, MobclickEvent.MINE_OPENMEMBERSHIP_BTN_CLICK);
                    } else { // 是超级会员
                        JumpDetail.jumpSuperVip(mContext, true);

                        EventReportManager.onEvent(mContext, MobclickEvent.MINE_SUPERMEMBERVIEW_VIEW_COUNT);
                    }
                } else {
                    touristDialog.showDialog();

                    EventReportManager.onEvent(mContext, MobclickEvent.MINE_OPENMEMBERSHIP_BTN_CLICK);
                }
            }
        });
        mineMsgView.setNicknameOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (!mainActivity.isFormalUser) {
                    touristDialog.showDialog();
                }
            }
        });
        mineMsgView.setAvatarClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mainActivity.isFormalUser) {
                    String avatar = CommonUserInfo.getWxAvatar();
                    JumpDetail.jumpMineMsg(mContext, avatar);
                } else {
                    touristDialog.showDialog();
                }
            }
        });
        mineVipCard.setBtnRenewalClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mainActivity.isFormalUser) {
                    if (superVipPresenter == null) {
                        superVipPresenter = new SuperVipPresenter(MineFragment.this);
                    }
                    superVipPresenter.requestSuperVipBuyInfo();
                } else {
                    touristDialog.showDialog();
                }
            }
        });
//        mineVipCard.setMoreEquityClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
//            @Override
//            public void singleClick(View v) {
//                Toast.makeText(mContext, "点击更多权益", Toast.LENGTH_SHORT).show();
//            }
//        });

        mineLearningWrapView.setLearningContainerClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mineLearningWrapView.getLearningContainerVisibility() == View.VISIBLE) { // 可见就设置点击事件
                    if (mainActivity.isFormalUser) {
                        // TODO: 跳转详情页
                        switch (mineLearningType) {
                            case DecorateEntityType.IMAGE_TEXT:
                                JumpDetail.jumpImageText(mContext, mineLearningId, "", "");
                                break;
                            case DecorateEntityType.AUDIO:
                                JumpDetail.jumpAudio(mContext, mineLearningId, 1);
                                break;
                            case DecorateEntityType.VIDEO:
                                JumpDetail.jumpVideo(mContext, mineLearningId, "", false, "");
                                break;
                            case DecorateEntityType.COLUMN:
                                JumpDetail.jumpColumn(mContext, mineLearningId, "", 6);
                                break;
                            case DecorateEntityType.TOPIC:
                                JumpDetail.jumpColumn(mContext, mineLearningId, "", 8);
                                break;
                            case DecorateEntityType.MEMBER:
                                JumpDetail.jumpColumn(mContext, mineLearningId, "", 5);
                                break;
                            default:
                                break;
                        }
                    } else {
                        touristDialog.showDialog();
                    }
                    EventReportManager.onEvent(mContext, MobclickEvent.MINE_PURCHASED_COURSE_CLICK);
                }
            }
        });
        mineLearningWrapView.setLearningMoreClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpMineLearning(mContext, getString(R.string.learning_tab_title));
                } else {
                    touristDialog.showDialog();
                }

                EventReportManager.onEvent(mContext, MobclickEvent.MINE_PURCHASED_ALL_CLICK);
            }
        });
        mineLearningWrapView.setLearningListItemClickListener(this);
        mineVipCard.setCardContainerClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (!mainActivity.isFormalUser) {
                    touristDialog.showDialog();
                }
            }
        });
        mineBoBiWrap.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpBobi(mContext);
                } else {
                    touristDialog.showDialog();
                }
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof IsSuperVipRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED || code == NetworkCodes.CODE_SUPER_VIP) { // 返回的 code 有 0 和 3011
                    JSONObject data = (JSONObject) result.get("data");
                    updateVipMsg(data);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取超级会员信息失败...");
                    mineLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                }
            } else if (iRequest instanceof EarningRequest) {
                if (iRequest.getRequestTag().equals(EarningPresenter.BALANCE_TAG)) {
                    updateBalance(result);
                } else {
                    String assetType = (String) iRequest.getFormBody().get("asset_type");
                    updateMoney(result, assetType);
                }
            } else if (iRequest instanceof MineLearningRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                     updateMineLearning(result);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求我正在学失败");
                    mineLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                    mineRefresh.finishRefresh();
                }
            } else if (iRequest instanceof SuperVipBuyInfoRequest) {
                int code = result.getInteger("code");
                obtainVipBuyInfo(code, result);
            }
        } else {
            // iRequest 为空是登录被挤情况
            mineRefresh.finishRefresh();
            if (iRequest != null) {
                mineLoading.setLoadingFinish();
            }
        }
    }

    // 更新超级会员信息
    private void updateVipMsg(JSONObject data) {
        mainActivity.initSuperVipMsg(data);
        if (mineVipCard != null) {
            if (CommonUserInfo.isIsSuperVip()) {
                initData();
            } else {
                mineVipCard.setVisibility(View.GONE);
                if (CommonUserInfo.isIsSuperVipAvailable()) { // 可以买超级会员
                    mineMsgView.setBuyVipVisibility(View.VISIBLE);
                    mineMsgView.setBuyVipCommon();
                } else { // 不可以买超级会员
                    mineMsgView.setBuyVipVisibility(View.GONE);
                }
            }
        }
        mineRefresh.finishRefresh();
    }

    /**
     * 更新波豆余额
     * @param result 返回数据对象
     */
    private void updateBalance(JSONObject result) {
        JSONObject data = (JSONObject) result.get("data");
        JSONObject balance = (JSONObject) data.get("balance");
        String realBalance = String.format("%1.2f",((float) balance.getInteger("2")) / 100);
        mineBoBiAmount.setText(realBalance);
    }

    // 更新奖学金和积分
    private void updateMoney(JSONObject result, String assetType) {
        if (Constants.SCHOLARSHIP_ASSET_TYPE.equals(assetType)){ // 奖学金类型
            int code = result.getInteger("code");
            if (code == NetworkCodes.CODE_SUCCEED) {
                JSONObject data = (JSONObject) result.get("data");
                int scholarship = data.getInteger("balance");
                balance = String.format(getString(R.string.price_decimal), scholarship / 100f);
                if (item_1 == null) {
                    item_1 = new MineMoneyItemInfo();
                    item_1.setItemTitle(balance);
                    item_1.setItemDesc(getString(R.string.scholarship_title));
                } else {
                    item_1.setItemTitle(balance);
                }
                isScholarshipFinish = true;
                initPageData();
            } else {
                Log.d(TAG, "onMainThreadResponse: 请求奖学金总额失败");
            }
        } else if (Constants.INTEGRAL_ASSET_TYPE.equals(assetType)) { // 积分类型
            int code = result.getInteger("code");
            if (code == NetworkCodes.CODE_SUCCEED) {
                JSONObject data = (JSONObject) result.get("data");
                int integral = data.getInteger("balance");
                if (item_2 == null) {
                    item_2 = new MineMoneyItemInfo();
                    item_2.setItemTitle(String.valueOf(integral));
                    item_2.setItemDesc(getString(R.string.integral));
                } else {
                    item_2.setItemTitle(String.valueOf(integral));
                }
                isIntegralFinish = true;
                initPageData();
            } else {
                Log.d(TAG, "onMainThreadResponse: 请求积分总额失败");
                mineLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: 取账号类型失败");
            mineLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
        }
    }

    // 更新我正在学
    private void updateMineLearning(JSONObject result) {
        JSONObject data;
        try {
            data = (JSONObject) result.get("data");
        } catch (Exception e) {
            e.printStackTrace();
            // 学习记录为空
            mineLearningWrapView.setLearningContainerVisibility(View.GONE);
            mineLearningWrapView.setLearningLoginDescVisibility(View.VISIBLE);
            mineLearningWrapView.setLearningLoginDesc(getString(R.string.none_bought_course));
            mineLearningWrapView.setLearningMoreVisibility(View.GONE);
            isMineLearningFinish = true;
            mineRefresh.finishRefresh();
            initPageData();
            return;
        }
        JSONArray goodsList = (JSONArray) data.get("goods_list");
        if (goodsList == null || goodsList.size() == 0) {
            return;
        }
        JSONObject listItem = (JSONObject) goodsList.get(0);
        if (listItem == null) {
            mineLearningWrapView.setLearningContainerVisibility(View.GONE);
            mineLearningWrapView.setLearningLoginDescVisibility(View.VISIBLE);
            mineLearningWrapView.setLearningLoginDesc(getString(R.string.none_bought_course));
            mineLearningWrapView.setLearningMoreVisibility(View.GONE);
            return;
        }
        mineLearningId = listItem.getString("resource_id");
        mineLearningType = convertInt2Str(listItem.getInteger("resource_type"));
        JSONObject item = (JSONObject) listItem.get("info");
        mineLearningWrapView.setLearningIconURI(item.getString("img_url"),mineLearningType);
        mineLearningWrapView.setLearningTitle(item.getString("title"));
        int updateCount = item.getInteger("periodical_count") == null ? 0 : item.getInteger("periodical_count");
        if (updateCount > 0) {
            mineLearningWrapView.setLearningUpdate(String.format(getString(R.string.updated_to_issue), updateCount));
        }
        mineLearningWrapView.setLearningMoreVisibility(View.VISIBLE);
        mineLearningWrapView.setLearningLoginDescVisibility(View.GONE);
        mineLearningWrapView.setLearningContainerVisibility(View.VISIBLE);
        isMineLearningFinish = true;
        initPageData();
        mineRefresh.finishRefresh();
        LearningRecord lr = new LearningRecord();
        lr.setLrId(listItem.getString("resource_id"));
        lr.setLrType(convertInt2Str(listItem.getInteger("resource_type")));
        lr.setLrTitle(item.getString("title"));
        lr.setLrImg(item.getString("img_url"));
        if (updateCount > 0) {
            if (listItem.getInteger("resource_type") == 1 || listItem.getInteger("resource_type") == 2 || listItem.getInteger("resource_type") == 3) {
                lr.setLrDesc(String.format(getString(R.string.learn_count), updateCount));
            } else if ( listItem.getInteger("resource_type") == 5 || listItem.getInteger("resource_type") == 6 || listItem.getInteger("resource_type") == 8) {
                lr.setLrDesc(String.format(getString(R.string.updated_to_issue), updateCount));
            }
        } else {
            lr.setLrDesc("");
        }
        UpdateLearningUtils.saveLr2Local(getActivity(), lr);
    }

    // 获取超级会员购买信息
    private void obtainVipBuyInfo(int code, JSONObject result) {
        if (code == NetworkCodes.CODE_SUCCEED) {
            JSONObject data = (JSONObject) result.get("data");
            String productId = data.getString("svip_id");
            String resourceId = data.getString("resource_id");
            String expireAtStart = data.getString("expire_at_start");
            String expireAtEnd = data.getString("expire_at_end");
            int price = data.getInteger("price");
            JumpDetail.jumpPay(getActivity(), resourceId, productId, 23, "res:///" + R.mipmap.pay_vip_bg,
                    getString(R.string.super_members), price, expireAtStart + "-" + expireAtEnd);
        } else {
            Log.d(TAG, "onMainThreadResponse: 超级会员购买信息请求失败");
            mineLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
        }
    }

    // 初始化页面数据
    private void initPageData() {
        // if (isIntegralFinish && isScholarshipFinish && isMineLearningFinish) { // 奖学金和积分和我正在学都请求完后再执行（去掉奖学金和积分的接口请求）
//        if (isMineLearningFinish) {
            if (itemInfoList != null && itemInfoList.size() > 0) {
                itemInfoList.clear();
            }
            if (itemInfoList == null) {
                mineLoading.setLoadingFinish();
                return;
            }
            itemInfoList.add(item_1);
            itemInfoList.add(item_2);
            if (!hasDecorate) {
                moneyWrapRecyclerAdapter = new MoneyWrapRecyclerAdapter(mContext, itemInfoList);
                moneyWrapRecyclerAdapter.setOnItemClickWithMoneyItemListener(this);
//                mineMoneyWrapView.setMoneyRecyclerAdapter(moneyWrapRecyclerAdapter);

                learningListAdapter = new MineLearningListAdapter(mContext);
                mineLearningWrapView.setLearningListAdapter(learningListAdapter);
                hasDecorate = true;
            } else {
                moneyWrapRecyclerAdapter.notifyDataSetChanged();
                learningListAdapter.notifyDataSetChanged();
            }

            mineLoading.setLoadingFinish();
//        }
    }

    // 初始化我的信息
    private void initMineMsg() {
        String wxNickname = CommonUserInfo.getWxNickname();
        String wxAvatar = CommonUserInfo.getWxAvatar();
        // 设置界面信息
        if ("".equals(wxNickname) || "null".equals(wxNickname)) {
            // 微信昵称为空
            mineMsgView.setNickName(getString(R.string.set_the_nickname));
        } else {
            mineMsgView.setNickName(wxNickname);
        }
        if ("".equals(wxAvatar) || "null".equals(wxAvatar)) {
            mineMsgView.setAvatar("res:///" + R.mipmap.default_avatar);
        } else {
            mineMsgView.setAvatar(wxAvatar);
        }
    }

    @Subscribe
    public void onEventMainThread(GetSuperMemberSuccessEvent event) {
        if (event != null && event.isRefresh){//兑换码到超级会员成功后，刷新我的页面UI
            // 兑换到超级会员之后请求超级会员的接口
            if (superVipPresenter == null) {
                superVipPresenter = new SuperVipPresenter(this);
            }
            superVipPresenter.requestSuperVip();
        }
    }

    @Subscribe
    public void onEventMainThread(ChangeLoginIdentityEvent changeLoginIdentityEvent) {
        if (changeLoginIdentityEvent != null && changeLoginIdentityEvent.isChangeSuccess()) {
            // 成功切换身份，重新请求接口，刷新界面
            initMineMsg();
            if (superVipPresenter == null) {
                superVipPresenter = new SuperVipPresenter(this);
            }
            isScholarshipFinish = false;
            isIntegralFinish = false;
            isMineLearningFinish = false;
            superVipPresenter.requestSuperVip();
        }
        if (changeLoginIdentityEvent != null && changeLoginIdentityEvent.isHasBalanceChange()) {
            // 2019.1.07 身份发生改变事件增加了余额改变字段，若放开奖学金后，需要使用这个字段对奖学金和积分进行刷新
            if (earningPresenter == null) {
                earningPresenter = new EarningPresenter(this);
            }
//            earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
//            earningPresenter.requestLaundryList(Constants.INTEGRAL_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
            earningPresenter.requestAccountBalance();
        }
    }

    @Subscribe
    public void onEventMainThread(UpdateMineMsgEvent updateMineMsgEvent) {
        if (updateMineMsgEvent != null) {
            if (mineMsgView != null) {
                mineMsgView.setNickName(updateMineMsgEvent.getWxNickName());
            }
        }
    }

    @Subscribe
    public void onEventMainThread(OnUnreadMsgEvent onUnreadMsgEvent) {
        if (null == onUnreadMsgEvent) {
            return;
        }
        switch (onUnreadMsgEvent.getMessageOrigin()) {
            case NOTICE:
                saveCount += onUnreadMsgEvent.getUnreadCount();
                break;
            case HTTP:
                saveCount = onUnreadMsgEvent.getUnreadCount();
                break;
            default:
                break;
        }
        setUnreadMsg(saveCount);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: // 我的收藏
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpMineLearning(mContext, getString(R.string.myCollect));
                } else {
                    touristDialog.showDialog();
                }

                EventReportManager.onEvent(mContext, MobclickEvent.MINE_FAV_BTN_CLICK);
                break;
            case 1: // 下载列表
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpOffLine(mContext);
                } else {
                    touristDialog.showDialog();
                }

                EventReportManager.onEvent(mContext, MobclickEvent.MINE_DOWNLOADED_BTN_CLICK);
                break;
            case 2: // 优惠券
                if (mainActivity.isFormalUser) {
                    if (CommonUserInfo.getInstance().isHasUnreadMsg()) {
                        CommonUserInfo.getInstance().setHasUnreadMsg(false);
                        learningListAdapter.notifyDataSetChanged();
                    }
                    JumpDetail.jumpCoupon(mContext);
                } else {
                    touristDialog.showDialog();
                }

                EventReportManager.onEvent(mContext, MobclickEvent.MINE_DISCOUNTS_BTN_CLICK);
                break;
            case 3: // 兑换码
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpCdKey(mContext);
                } else {
                    touristDialog.showDialog();
                }

                EventReportManager.onEvent(mContext, MobclickEvent.MINE_REDEEM_BTN_CLICK);
                break;
            default:
                //TODO: 异常判断
                break;
        }
    }

    @Override
    public void onMineMoneyItemInfoClickListener(View view, MineMoneyItemInfo mineMoneyItemInfo) {
        String desc = mineMoneyItemInfo.getItemDesc();
        if (getString(R.string.scholarship_title).equals(desc)) {
            if (mainActivity.isFormalUser) {
                JumpDetail.jumpScholarshipActivity(mContext);
            } else {
                touristDialog.showDialog();
            }
        } else if (getString(R.string.integral).equals(desc)) {
            if (mainActivity.isFormalUser) {
                JumpDetail.jumpIntegralActivity(mContext);
            } else {
                touristDialog.showDialog();
            }
        }
    }

    /**
     * 资源类型转换 int - str
     * @param resourceType 资源类型
     * @return 资源类型的字符串形式
     */
    protected String convertInt2Str(int resourceType) {
        switch (resourceType) {
            case 1: // 图文
                return DecorateEntityType.IMAGE_TEXT;
            case 2: // 音频
                return DecorateEntityType.AUDIO;
            case 3: // 视频
                return DecorateEntityType.VIDEO;
            case 5: // 会员
                return DecorateEntityType.MEMBER;
            case 6: // 专栏
                return DecorateEntityType.COLUMN;
            case 8: // 大专栏
                return DecorateEntityType.TOPIC;
            default:
                return "";
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (earningPresenter == null) {
            earningPresenter = new EarningPresenter(this);
        }
        if (mineLearningPresenter == null) {
            mineLearningPresenter = new MineLearningPresenter(this);
        }
//        earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
//        earningPresenter.requestLaundryList(Constants.INTEGRAL_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
        earningPresenter.requestAccountBalance();
        mineLearningPresenter.requestLearningData(1, 1);
        if (superVipPresenter == null) {
            superVipPresenter = new SuperVipPresenter(this);
        }
        superVipPresenter.requestSuperVip();
        mainActivity.getUnreadMsg();
    }

    private void setUnreadMsg(int unreadCount) {
        mineTitleView.setUnreadMsgVisible(unreadCount > 0);
        mineTitleView.setUnreadMsgCount(unreadCount);

        Log.d(TAG, "setUnreadMsg: unreadCount " + unreadCount);
    }

    private void setDataByDB(){

        SQLiteUtil sqLiteUtil = SQLiteUtil.init(getContext(), new CacheDataUtil());
        //积分
//        String integralSQL = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
//                +"' and resource_id='"+Constants.INTEGRAL_ASSET_TYPE+"' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
//        List<CacheData> integralCacheDataList =  sqLiteUtil.query(CacheDataUtil.TABLE_NAME, integralSQL, null);
//        if(integralCacheDataList != null && integralCacheDataList.size() > 0){
//            String content = integralCacheDataList.get(0).getContent();
//            JSONObject data = (JSONObject) JSONObject.parseObject(content).get("data");
//            int integral = data.getInteger("balance");
//            if (item_2 == null) {
//                item_2 = new MineMoneyItemInfo();
//                item_2.setItemTitle(String.valueOf(integral));
//                item_2.setItemDesc(getString(R.string.integral));
//            } else {
//                item_2.setItemTitle(String.valueOf(integral));
//            }
//            isIntegralFinish = true;
//            initPageData();
//        }
        //奖学金
//        String scholarshipSQL = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
//                +"' and resource_id='"+Constants.SCHOLARSHIP_ASSET_TYPE+"' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
//        List<CacheData> scholarshipCacheDataList =  sqLiteUtil.query(CacheDataUtil.TABLE_NAME, scholarshipSQL, null);
//        if(scholarshipCacheDataList != null && scholarshipCacheDataList.size() > 0){
//            String content = scholarshipCacheDataList.get(0).getContent();
//            JSONObject data = (JSONObject) JSONObject.parseObject(content).get("data");
//            int scholarship = data.getInteger("balance");
//            balance = String.format(getString(R.string.price_decimal), scholarship / 100f);
//            if (item_1 == null) {
//                item_1 = new MineMoneyItemInfo();
//                item_1.setItemTitle(balance);
//                item_1.setItemDesc(getString(R.string.scholarship_title));
//            } else {
//                item_1.setItemTitle(balance);
//            }
//            isScholarshipFinish = true;
//            initPageData();
//        }
        //正在学习
        String learningSQL = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()
                +"' and resource_id='learning' and user_id='"+CommonUserInfo.getLoginUserIdOrAnonymousUserId()+"'";
        List<CacheData> learningCacheDataList =  sqLiteUtil.query(CacheDataUtil.TABLE_NAME, learningSQL, null);
        if(learningCacheDataList != null && learningCacheDataList.size() > 0){
            JSONObject data;
            try {
                String content = learningCacheDataList.get(0).getContent();
                data = (JSONObject) JSONObject.parseObject(content).get("data");
            } catch (Exception e) {
                e.printStackTrace();
                // 学习记录为空
                mineLearningWrapView.setLearningContainerVisibility(View.GONE);
                mineLearningWrapView.setLearningLoginDescVisibility(View.VISIBLE);
                mineLearningWrapView.setLearningLoginDesc(getString(R.string.none_bought_course));
                mineLearningWrapView.setLearningMoreVisibility(View.GONE);
                isMineLearningFinish = true;
                return;
            }
            JSONArray goodsList = (JSONArray) data.get("goods_list");
            if (goodsList == null || goodsList.size() == 0) {
                return;
            }
            JSONObject listItem = (JSONObject) goodsList.get(0);
            if (listItem == null) {
                mineLearningWrapView.setLearningContainerVisibility(View.GONE);
                mineLearningWrapView.setLearningLoginDescVisibility(View.VISIBLE);
                mineLearningWrapView.setLearningLoginDesc(getString(R.string.none_bought_course));
                mineLearningWrapView.setLearningMoreVisibility(View.GONE);
                return;
            }
            mineLearningId = listItem.getString("resource_id");
            mineLearningType = convertInt2Str(listItem.getInteger("resource_type"));
            JSONObject item = (JSONObject) listItem.get("info");
            mineLearningWrapView.setLearningIconURI(item.getString("img_url"),mineLearningType);
            mineLearningWrapView.setLearningTitle(item.getString("title"));
            int updateCount = item.getInteger("periodical_count") == null ? 0 : item.getInteger("periodical_count");
            if (updateCount > 0) {
                mineLearningWrapView.setLearningUpdate(String.format(getString(R.string.updated_to_issue), updateCount));
            }
            mineLearningWrapView.setLearningMoreVisibility(View.VISIBLE);
            mineLearningWrapView.setLearningContainerVisibility(View.VISIBLE);
            isMineLearningFinish = true;
            initPageData();
            mineRefresh.finishRefresh();
        }
//        if(integralCacheDataList != null && integralCacheDataList.size() > 0
//                && scholarshipCacheDataList != null && scholarshipCacheDataList.size() > 0
//                && learningCacheDataList != null && learningCacheDataList.size() > 0){
        showDataByDB = learningCacheDataList != null && learningCacheDataList.size() > 0;
    }
}
