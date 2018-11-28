package com.xiaoe.shop.wxb.business.mine.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.CacheData;
import com.xiaoe.common.entitys.ChangeLoginIdentityEvent;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.GetSuperMemberSuccessEvent;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.entitys.MineMoneyItemInfo;
import com.xiaoe.common.entitys.UpdateMineMsgEvent;
import com.xiaoe.common.interfaces.OnItemClickWithMoneyItemListener;
import com.xiaoe.common.utils.CacheDataUtil;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
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
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.events.OnUnreadMsgEvent;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.widget.StatusPagerView;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

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
    @BindView(R.id.mine_money_view)
    MineMoneyWrapView mineMoneyWrapView;
    @BindView(R.id.mine_learning_view)
    MineLearningWrapView mineLearningWrapView;
    @BindView(R.id.mine_loading)
    StatusPagerView mineLoading;

    MainActivity mainActivity;

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
    String mineLearningId;
    String mineLearningType;
    boolean hasDecorate;

    MoneyWrapRecyclerAdapter moneyWrapRecyclerAdapter; // 金钱适配器
    MineLearningListAdapter learningListAdapter; // 学习记录适配器
    private boolean showDataByDB = false;
    SuperVipPresenter superVipPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        // 网络请求数据代码
//        MinePresenter minePresenter = new MinePresenter(this);
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
        setUnreadMsg(0);
        mainActivity.getUnreadMsg();
    }

    @Override
    public void onPause() {
        super.onPause();
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
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        if (earningPresenter == null) {
            earningPresenter = new EarningPresenter(this);
        }
        if (mineLearningPresenter == null) {
            mineLearningPresenter = new MineLearningPresenter(this);
        }
        isScholarshipFinish = false;
        isIntegralFinish = false;
        isMineLearningFinish = false;
        earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
        earningPresenter.requestLaundryList(Constants.INTEGRAL_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
        mineLearningPresenter.requestLearningData(1, 1);
        if (!mainActivity.isFormalUser) {

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
        //先初始化缓存中的数据
        setDataByDB();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            List<LoginUser> loginMsg = getLoginUserList();
            if (loginMsg.size() == 1) {
                initMineMsg();
                initData();
            } else {
                initTouristData();
            }

//            initListener();
        }
    }

    private void initTouristData() {
        // 游客登录
        mineMsgView.setNickName("点击登录");
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
        mineMoneyWrapView.setLayoutParams(layoutParams);
        // 金钱容器（未登录状态）
        List<MineMoneyItemInfo> tempList = new ArrayList<>();
        MineMoneyItemInfo item_1_temp = new MineMoneyItemInfo();
        item_1_temp.setItemTitle("￥0.00");
        item_1_temp.setItemDesc("奖学金");
        MineMoneyItemInfo item_2_temp = new MineMoneyItemInfo();
        item_2_temp.setItemTitle("0");
        item_2_temp.setItemDesc("积分");
        tempList.add(item_1_temp);
        tempList.add(item_2_temp);
        MoneyWrapRecyclerAdapter moneyWrapRecyclerAdapter = new MoneyWrapRecyclerAdapter(mContext, tempList);
        moneyWrapRecyclerAdapter.setOnItemClickWithMoneyItemListener(this);
        mineMoneyWrapView.setMoneyRecyclerAdapter(moneyWrapRecyclerAdapter);
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
                contentList.add("所有课程免费学习");
                contentList.add("分享赢双倍积分");
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
            mineMoneyWrapView.setLayoutParams(layoutParams);
        } else { // 超级会员卡片不存在
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = Dp2Px2SpUtil.dp2px(mContext, 0);
            int top = Dp2Px2SpUtil.dp2px(mContext, 0);
            int right = Dp2Px2SpUtil.dp2px(mContext, 0);
            layoutParams.setMargins(left,top,right,0);
            mineMoneyWrapView.setLayoutParams(layoutParams);
        }
        // 金钱容器
        if (itemInfoList == null) {
            itemInfoList = new ArrayList<>();
        }
        // 我正在学
        mineLearningWrapView.setLearningLoginDescVisibility(View.GONE);
    }

    private void initListener() {
        mineRefresh.setOnRefreshListener(this);
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
                    } else { // 是超级会员
                        JumpDetail.jumpSuperVip(mContext, true);
                    }
                } else {
                    touristDialog.showDialog();
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
        if (mineLearningWrapView.getLearningContainerVisibility() == View.VISIBLE) { // 可见就设置点击事件
            mineLearningWrapView.setLearningContainerClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
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
                }
            });
        }
        mineLearningWrapView.setLearningMoreClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpMineLearning(mContext, "我正在学");
                } else {
                    touristDialog.showDialog();
                }
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
        mineLoading.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mineLoading.getCurrentLoadingStatus() == StatusPagerView.FAIL) { // 页面错误点击再请求、
                    mineLoading.setPagerState(StatusPagerView.LOADING, "", 0);

                    if (mineLearningPresenter == null) {
                        mineLearningPresenter = new MineLearningPresenter(MineFragment.this);
                    }
                    if (earningPresenter == null) {
                        earningPresenter = new EarningPresenter(MineFragment.this);
                    }

                    isScholarshipFinish = false;
                    isIntegralFinish = false;
                    isMineLearningFinish = false;
                    earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
                    earningPresenter.requestLaundryList(Constants.INTEGRAL_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
                    mineLearningPresenter.requestLearningData(1, 1);
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
                String assetType = (String) iRequest.getFormBody().get("asset_type");
                updateMoney(result, assetType);
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
            mineLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
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

    // 更新奖学金和积分
    private void updateMoney(JSONObject result, String assetType) {
        if (Constants.SCHOLARSHIP_ASSET_TYPE.equals(assetType)){ // 奖学金类型
            int code = result.getInteger("code");
            if (code == NetworkCodes.CODE_SUCCEED) {
                JSONObject data = (JSONObject) result.get("data");
                int scholarship = data.getInteger("balance");
                balance = "￥" + String.format("%.2f", scholarship / 100f);
                if (item_1 == null) {
                    item_1 = new MineMoneyItemInfo();
                    item_1.setItemTitle(balance);
                    item_1.setItemDesc("奖学金");
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
                    item_2.setItemDesc("积分");
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
            mineLearningWrapView.setLearningLoginDesc("你当前暂无正在学的课程哦");
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
            mineLearningWrapView.setLearningLoginDesc("你当前暂无正在学的课程哦");
            return;
        }
        mineLearningId = listItem.getString("resource_id");
        mineLearningType = convertInt2Str(listItem.getInteger("resource_type"));
        JSONObject item = (JSONObject) listItem.get("info");
        mineLearningWrapView.setLearningIconURI(item.getString("img_url"));
        mineLearningWrapView.setLearningTitle(item.getString("title"));
        int updateCount = item.getInteger("periodical_count") == null ? 0 : item.getInteger("periodical_count");
        if (updateCount > 0) {
            mineLearningWrapView.setLearningUpdate("已更新至" + updateCount + "期");
        }
        isMineLearningFinish = true;
        initPageData();
        mineRefresh.finishRefresh();
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
                    "超级会员", price, expireAtStart + "-" + expireAtEnd);
        } else {
            Log.d(TAG, "onMainThreadResponse: 超级会员购买信息请求失败");
            mineLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
        }
    }

    // 初始化页面数据
    private void initPageData() {
        if (isIntegralFinish && isScholarshipFinish && isMineLearningFinish) { // 奖学金和积分和我正在学都请求完后再执行
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
                mineMoneyWrapView.setMoneyRecyclerAdapter(moneyWrapRecyclerAdapter);

                learningListAdapter = new MineLearningListAdapter(mContext);
                mineLearningWrapView.setLearningListAdapter(learningListAdapter);
                hasDecorate = true;
            } else {
                moneyWrapRecyclerAdapter.notifyDataSetChanged();
                learningListAdapter.notifyDataSetChanged();
            }

            mineLoading.setLoadingFinish();
        }
    }

    // 初始化我的信息
    private void initMineMsg() {
        String wxNickname = CommonUserInfo.getWxNickname();
        String wxAvatar = CommonUserInfo.getWxAvatar();
        // 设置界面信息
        if ("".equals(wxNickname) || "null".equals(wxNickname)) {
            // 微信昵称为空
            mineMsgView.setNickName("请设置昵称");
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
            if (earningPresenter == null) {
                earningPresenter = new EarningPresenter(this);
            }
            if (mineLearningPresenter == null) {
                mineLearningPresenter = new MineLearningPresenter(this);
            }
            if (superVipPresenter == null) {
                superVipPresenter = new SuperVipPresenter(this);
            }
            isScholarshipFinish = false;
            isIntegralFinish = false;
            isMineLearningFinish = false;
            earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
            earningPresenter.requestLaundryList(Constants.INTEGRAL_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
            mineLearningPresenter.requestLearningData(1, 1);
            superVipPresenter.requestSuperVip();
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
        setUnreadMsg(onUnreadMsgEvent.getUnreadCount());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: // 我的收藏
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpMineLearning(mContext, "我的收藏");
                } else {
                    touristDialog.showDialog();
                }
                break;
            case 1: // 离线缓存
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpOffLine(mContext);
                } else {
                    touristDialog.showDialog();
                }
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
                break;
            case 3: // 兑换码
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpCdKey(mContext);
                } else {
                    touristDialog.showDialog();
                }
                break;
            default:
                //TODO: 异常判断
                break;
        }
    }

    @Override
    public void onMineMoneyItemInfoClickListener(View view, MineMoneyItemInfo mineMoneyItemInfo) {
        String desc = mineMoneyItemInfo.getItemDesc();
        switch (desc) {
            case "奖学金":
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpScholarshipActivity(mContext);
                } else {
                    touristDialog.showDialog();
                }
                break;
            case "积分":
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpIntegralActivity(mContext);
                } else {
                    touristDialog.showDialog();
                }
                break;
            default:
                break;
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
                return null;
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
        earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
        earningPresenter.requestLaundryList(Constants.INTEGRAL_ASSET_TYPE, Constants.NO_NEED_FLOW, Constants.EARNING_FLOW_TYPE, 1, 1);
        mineLearningPresenter.requestLearningData(1, 1);
        if (superVipPresenter == null) {
            superVipPresenter = new SuperVipPresenter(this);
        }
        superVipPresenter.requestSuperVip();
    }

    private void setUnreadMsg(int unreadCount) {
        int saveCount = (int) SharedPreferencesUtil.getData(SharedPreferencesUtil.KEY_UNREAD_MSG_COUNT, 0);

        int newCount = saveCount + unreadCount;
        SharedPreferencesUtil.putData(SharedPreferencesUtil.KEY_UNREAD_MSG_COUNT, newCount);

        mineTitleView.setUnreadMsgVisible(newCount > 0);

        Log.d(TAG, "setUnreadMsg: count " + newCount);
    }

    private void setDataByDB(){

        SQLiteUtil sqLiteUtil = SQLiteUtil.init(getContext(), new CacheDataUtil());
        //积分
        String integralSQL = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()+"' and resource_id='"+Constants.INTEGRAL_ASSET_TYPE+"'";
        List<CacheData> integralCacheDataList =  sqLiteUtil.query(CacheDataUtil.TABLE_NAME, integralSQL, null);
        if(integralCacheDataList != null && integralCacheDataList.size() > 0){
            String content = integralCacheDataList.get(0).getContent();
            JSONObject data = (JSONObject) JSONObject.parseObject(content).get("data");
            int integral = data.getInteger("balance");
            if (item_2 == null) {
                item_2 = new MineMoneyItemInfo();
                item_2.setItemTitle(String.valueOf(integral));
                item_2.setItemDesc("积分");
            } else {
                item_2.setItemTitle(String.valueOf(integral));
            }
            isIntegralFinish = true;
            initPageData();
        }
        //奖学金
        String scholarshipSQL = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()+"' and resource_id='"+Constants.SCHOLARSHIP_ASSET_TYPE+"'";
        List<CacheData> scholarshipCacheDataList =  sqLiteUtil.query(CacheDataUtil.TABLE_NAME, scholarshipSQL, null);
        if(scholarshipCacheDataList != null && scholarshipCacheDataList.size() > 0){
            String content = scholarshipCacheDataList.get(0).getContent();
            JSONObject data = (JSONObject) JSONObject.parseObject(content).get("data");
            int scholarship = data.getInteger("balance");
            balance = "￥" + String.format("%.2f", scholarship / 100f);
            if (item_1 == null) {
                item_1 = new MineMoneyItemInfo();
                item_1.setItemTitle(balance);
                item_1.setItemDesc("奖学金");
            } else {
                item_1.setItemTitle(balance);
            }
            isScholarshipFinish = true;
            initPageData();
        }
        //正在学习
        String learningSQL = "select * from "+CacheDataUtil.TABLE_NAME+" where app_id='"+Constants.getAppId()+"' and resource_id='learning'";
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
                mineLearningWrapView.setLearningLoginDesc("你当前暂无正在学的课程哦");
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
                mineLearningWrapView.setLearningLoginDesc("你当前暂无正在学的课程哦");
                return;
            }
            mineLearningId = listItem.getString("resource_id");
            mineLearningType = convertInt2Str(listItem.getInteger("resource_type"));
            JSONObject item = (JSONObject) listItem.get("info");
            mineLearningWrapView.setLearningIconURI(item.getString("img_url"));
            mineLearningWrapView.setLearningTitle(item.getString("title"));
            int updateCount = item.getInteger("periodical_count") == null ? 0 : item.getInteger("periodical_count");
            if (updateCount > 0) {
                mineLearningWrapView.setLearningUpdate("已更新至" + updateCount + "期");
            }
            isMineLearningFinish = true;
            initPageData();
            mineRefresh.finishRefresh();
        }
        if(integralCacheDataList != null && integralCacheDataList.size() > 0
                && scholarshipCacheDataList != null && scholarshipCacheDataList.size() > 0
                && learningCacheDataList != null && learningCacheDataList.size() > 0){
            showDataByDB = true;
        } else {
            showDataByDB = false;
        }
    }
}
