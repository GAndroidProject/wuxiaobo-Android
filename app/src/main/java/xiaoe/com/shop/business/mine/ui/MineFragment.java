package xiaoe.com.shop.business.mine.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.GetSuperMemberSuccessEvent;
import xiaoe.com.common.entitys.LoginUser;
import xiaoe.com.common.entitys.MineMoneyItemInfo;
import xiaoe.com.common.interfaces.OnItemClickWithMoneyItemListener;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.EarningRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.IntegralRequest;
import xiaoe.com.network.requests.IsSuperVipRequest;
import xiaoe.com.network.requests.MineLearningRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.cdkey.ui.CdKeyActivity;
import xiaoe.com.shop.business.coupon.ui.CouponActivity;
import xiaoe.com.shop.business.download.ui.OffLineCacheActivity;
import xiaoe.com.shop.business.earning.presenter.EarningPresenter;
import xiaoe.com.shop.business.main.ui.MainActivity;
import xiaoe.com.shop.business.mine.presenter.MineEquityListAdapter;
import xiaoe.com.shop.business.mine.presenter.MineLearningListAdapter;
import xiaoe.com.shop.business.mine.presenter.MoneyWrapRecyclerAdapter;
import xiaoe.com.shop.business.mine_learning.presenter.MineLearningPresenter;
import xiaoe.com.shop.business.super_vip.presenter.SuperVipPresenter;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.utils.StatusBarUtil;
import xiaoe.com.shop.widget.StatusPagerView;
import xiaoe.com.shop.widget.TouristDialog;

public class MineFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener, OnItemClickWithMoneyItemListener {

    private static final String TAG = "MineFragment";

    private Unbinder unbinder;
    private Context mContext;

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
    String integral;

    MineMoneyItemInfo item_1; // 奖学金
    MineMoneyItemInfo item_2; // 积分
    List<MineMoneyItemInfo> itemInfoList;
    boolean isScholarshipFinish; // 奖学金请求完成
    boolean isIntegralFinish;    // 积分请求完成
    boolean isMineLearningFinish; // 我正在学请求完成

    TouristDialog touristDialog;
    String mineLearningId;
    String mineLearningType;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        mainActivity = (MainActivity) getActivity();
        mineLoading.setVisibility(View.VISIBLE);
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
        earningPresenter.requestDetailData(1, 1, 1);
        earningPresenter.requestIntegralData(1, 1, 1);
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
                    getActivity().finish();
                    JumpDetail.jumpLogin(getActivity());
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        List<LoginUser> loginMsg = getLoginUserList();
        if (loginMsg.size() == 1) {
            initMineMsg();
            initData();
        } else {
            // 游客登录
            mineMsgView.setNickName("点击登录");
            mineMsgView.setAvatar("res:///" + R.mipmap.default_avatar);
            mineVipCard.setVisibility(View.GONE);
            mineMsgView.setBuyVipVisibility(View.GONE);
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

    private void initData() {
        // 会员权益假数据 -- 开始
        List<String> contentList = new ArrayList<>();
        contentList.add("四大体系课程免费听");
        contentList.add("分享赢双倍奖学金");
        MineEquityListAdapter mineEquityListAdapter = new MineEquityListAdapter(mContext, contentList);
        mineVipCard.setEquityListAdapter(mineEquityListAdapter);
        mineVipCard.setDeadLine("2019/10/15");
        // 会员权益假数据 -- 结束
        if (CommonUserInfo.isIsSuperVipAvailable()) { // 店铺是否有超级会员
            mineMsgView.setBuyVipVisibility(View.VISIBLE);
        } else {
            mineMsgView.setBuyVipVisibility(View.GONE);
        }
        if (CommonUserInfo.isIsSuperVip()) { // 是超级会员，显示卡片
            mineVipCard.setVisibility(View.VISIBLE);
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
                }
            } else if (iRequest instanceof EarningRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    int scholarship = data.getInteger("balance");
                    balance = "￥" + String.valueOf(scholarship / 100);
                    item_1 = new MineMoneyItemInfo();
                    item_1.setItemTitle(balance);
                    item_1.setItemDesc("奖学金");
                    isScholarshipFinish = true;
                    initPageData();
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求奖学金总额失败");
                }
            } else if (iRequest instanceof IntegralRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    int integral = data.getInteger("balance");
                    item_2 = new MineMoneyItemInfo();
                    item_2.setItemTitle(String.valueOf(integral));
                    item_2.setItemDesc("积分");
                    isIntegralFinish = true;
                    initPageData();
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求积分总额失败");
                }
            } else if (iRequest instanceof MineLearningRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    JSONArray goodsList = (JSONArray) data.get("goods_list");
                    JSONObject listItem = (JSONObject) goodsList.get(0);
                    mineLearningId = listItem.getString("resource_id");
                    mineLearningType = convertInt2Str(listItem.getInteger("resource_type"));
                    JSONObject item = (JSONObject) listItem.get("info");
                    mineLearningWrapView.setLearningIconURI(item.getString("img_url"));
                    mineLearningWrapView.setLearningTitle(item.getString("title"));
                    int updateCount = item.getInteger("periodical_count") == null ? 0 : item.getInteger("periodical_count");
                    if (updateCount > 0) {
                        mineLearningWrapView.setLearningUpdate("已更新至" + updateCount + "期");
                    } else {
                        mineLearningWrapView.setLearningUpdate("已购");
                    }
                    isMineLearningFinish = true;
                    initPageData();
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求我正在学失败");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
        }
    }

    // 初始化页面数据
    private void initPageData() {
        if (isIntegralFinish && isScholarshipFinish && isMineLearningFinish) { // 奖学金和积分和我正在学都请求完后再执行
            if (itemInfoList == null) {
                mineLoading.setLoadingState(View.GONE);
                mineLoading.setVisibility(View.GONE);
                return;
            }
            itemInfoList.add(item_1);
            itemInfoList.add(item_2);
            MoneyWrapRecyclerAdapter moneyWrapRecyclerAdapter = new MoneyWrapRecyclerAdapter(mContext, itemInfoList);
            moneyWrapRecyclerAdapter.setOnItemClickWithMoneyItemListener(this);
            mineMoneyWrapView.setMoneyRecyclerAdapter(moneyWrapRecyclerAdapter);

            MineLearningListAdapter learningListAdapter = new MineLearningListAdapter(mContext);
            mineLearningWrapView.setLearningListAdapter(learningListAdapter);

            mineLoading.setLoadingState(View.GONE);
            mineLoading.setVisibility(View.GONE);
        } else {
            mineLoading.setVisibility(View.VISIBLE);
            mineLoading.setLoadingState(View.VISIBLE);
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
                    Toast.makeText(mContext, "点击信息按钮", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mContext, "点击续费按钮", Toast.LENGTH_SHORT).show();
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
                            JumpDetail.jumpColumn(mContext, mineLearningId, "", false);
                            break;
                        case DecorateEntityType.TOPIC:
                            JumpDetail.jumpColumn(mContext, mineLearningId, "", true);
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
}
