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
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.GetSuperMemberSuccessEvent;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.entitys.MineMoneyItemInfo;
import com.xiaoe.common.interfaces.OnItemClickWithMoneyItemListener;
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

public class MineFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, OnItemClickWithMoneyItemListener, OnRefreshListener {

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
    List<MineMoneyItemInfo> itemInfoList;
    boolean isScholarshipFinish; // 奖学金请求完成
    boolean isIntegralFinish;    // 积分请求完成
    boolean isMineLearningFinish; // 我正在学请求完成

    TouristDialog touristDialog;
    String mineLearningId;
    String mineLearningType;
    boolean hasDecorate;

    MoneyWrapRecyclerAdapter moneyWrapRecyclerAdapter; // 金钱适配器
    MineLearningListAdapter learningListAdapter; // 学习记录适配器

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        mineLoading.setLoadingState(View.VISIBLE);
        view.setPadding(0, StatusBarUtil.getStatusBarHeight(mContext), 0, 0);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        // 网络请求数据代码
//        MinePresenter minePresenter = new MinePresenter(this);
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
                // 游客登录
                mineMsgView.setNickName("点击登录");
                mineMsgView.setAvatar("res:///" + R.mipmap.default_avatar);
                mineVipCard.setVisibility(View.GONE);
                mineMsgView.setBuyVipVisibility(View.VISIBLE);
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

            initListener();
        }
    }

    private void initData() {
        // 会员权益假数据 -- 开始
        List<String> contentList = new ArrayList<>();
        contentList.add("所有课程免费学习");
        contentList.add("分享赢双倍积分");
        MineEquityListAdapter mineEquityListAdapter = new MineEquityListAdapter(mContext, contentList);
        mineVipCard.setEquityListAdapter(mineEquityListAdapter);
        mineVipCard.setDeadLine("2019/10/15");
        // 会员权益假数据 -- 结束
        if (CommonUserInfo.isIsSuperVipAvailable() && !CommonUserInfo.isIsSuperVip()) { // 店铺是否有超级会员并且不是超级会员才显示
            mineMsgView.setBuyVipVisibility(View.VISIBLE);
        } else {
            mineMsgView.setBuyVipVisibility(View.GONE);
        }
        if (CommonUserInfo.isIsSuperVip()) { // 是超级会员，显示卡片
            mineVipCard.setVisibility(View.VISIBLE);
            mineVipCard.setDeadLine(mainActivity.expireAt);
        } else {
            mineVipCard.setVisibility(View.GONE);
        }
        if (mineVipCard.getVisibility() == View.VISIBLE) { // 超级会员卡片存在
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = Dp2Px2SpUtil.dp2px(mContext, 20);
            int top = Dp2Px2SpUtil.dp2px(mContext, 16);
            int right = Dp2Px2SpUtil.dp2px(mContext, 20);
            layoutParams.setMargins(left,top,right,0);
            mineMoneyWrapView.setLayoutParams(layoutParams);
        } else if (mineVipCard.getVisibility() == View.GONE) { // 超级会员卡片不存在
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = Dp2Px2SpUtil.dp2px(mContext, 20);
            int top = Dp2Px2SpUtil.dp2px(mContext, -36);
            int right = Dp2Px2SpUtil.dp2px(mContext, 20);
            layoutParams.setMargins(left, top, right ,0);
            mineMoneyWrapView.setLayoutParams(layoutParams);
        }
        // 金钱容器
        itemInfoList = new ArrayList<>();
        // 我正在学
        mineLearningWrapView.setLearningLoginDescVisibility(View.GONE);
    }

    private void initListener() {
        mineRefresh.setOnRefreshListener(this);
        mineTitleView.setMsgClickListener(this);
        mineTitleView.setSettingListener(this);
        mineMsgView.setBuyVipClickListener(this);
        mineMsgView.setNicknameOnClickListener(this);
        mineMsgView.setAvatarClickListener(this);
        mineVipCard.setBtnRenewalClickListener(this);
//        mineVipCard.setMoreEquityClickListener(this);
        if (mineLearningWrapView.getLearningContainerVisibility() == View.VISIBLE) { // 可见就设置点击事件
            mineLearningWrapView.setLearningContainerClickListener(this);
        }
        mineLearningWrapView.setLearningMoreClickListener(this);
        mineLearningWrapView.setLearningListItemClickListener(this);
        mineVipCard.setCardContainerClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
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
                    mainActivity.initSuperVipMsg(data);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 获取超级会员信息失败...");
                    mineLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                }
            } else if (iRequest instanceof EarningRequest) {
                String assetType = (String) iRequest.getFormBody().get("asset_type");
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
            } else if (iRequest instanceof MineLearningRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
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
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求我正在学失败");
                    mineLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                    mineRefresh.finishRefresh();
                }
            } else if (iRequest instanceof SuperVipBuyInfoRequest) {
                int code = result.getInteger("code");
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
        } else {
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

    @Subscribe
    public void onEventMainThread(GetSuperMemberSuccessEvent event) {
        if (event != null && event.isRefresh){//兑换码到超级会员成功后，刷新我的页面UI
            // 兑换到超级会员之后请求超级会员的接口
            SuperVipPresenter superVipPresenter = new SuperVipPresenter(this);
            superVipPresenter.requestSuperVip();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_title_msg: // 信息
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpHistoryMessage(mContext);
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.title_nickname: // 昵称
                if (!mainActivity.isFormalUser) {
                    touristDialog.showDialog();
                }
                break;
            case R.id.mine_title_setting: // 设置
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpAccount(mContext);
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.title_avatar: // 头像
                if (mainActivity.isFormalUser) {
                    String avatar = CommonUserInfo.getWxAvatar();
                    JumpDetail.jumpMineMsg(mContext, avatar);
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.title_buy_vip: // 超级会员
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpSuperVip(mContext);
                } else {
                    touristDialog.showDialog();
                }
                break;
//            case R.id.card_equity_more: // 更多权益
//                Toast.makeText(mContext, "点击更多权益", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.card_renewal: // 续费
                if (mainActivity.isFormalUser) {
                    SuperVipPresenter superVipPresenter = new SuperVipPresenter(this);
                    superVipPresenter.requestSuperVipBuyInfo();
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.learning_more: // 我正在学 -> 查看更多
                if (mainActivity.isFormalUser) {
                    JumpDetail.jumpMineLearning(mContext, "我正在学");
                } else {
                    touristDialog.showDialog();
                }
                break;
            case R.id.learning_item_container: // 我正在学的详情页
                if (mainActivity.isFormalUser) {
                    // TODO: 跳转详情页
                    switch (mineLearningType) {
                        case DecorateEntityType.IMAGE_TEXT:
                            JumpDetail.jumpImageText(mContext, mineLearningId, "");
                            break;
                        case DecorateEntityType.AUDIO:
                            JumpDetail.jumpAudio(mContext, mineLearningId, 1);
                            break;
                        case DecorateEntityType.VIDEO:
                            JumpDetail.jumpVideo(mContext, mineLearningId, "", false);
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
                break;
            case R.id.card_container: // 超级会员卡片
                if (mainActivity.isFormalUser) {
                    Toast.makeText(mContext, "超级会员卡片", Toast.LENGTH_SHORT).show();
                } else {
                    touristDialog.showDialog();
                }
                break;
            default:
                break;
        }
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
    }
}
