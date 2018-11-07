package xiaoe.com.shop.business.mine.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.entitys.GetSuperMemberSuccessEvent;
import xiaoe.com.common.entitys.LoginUser;
import xiaoe.com.common.entitys.MineMoneyItemInfo;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.cdkey.ui.CdKeyActivity;
import xiaoe.com.shop.business.coupon.ui.CouponActivity;
import xiaoe.com.shop.business.download.ui.OffLineCacheActivity;
import xiaoe.com.shop.business.mine.presenter.MineEquityListAdapter;
import xiaoe.com.shop.business.mine.presenter.MineLearningListAdapter;
import xiaoe.com.shop.business.mine.presenter.MoneyWrapRecyclerAdapter;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.utils.StatusBarUtil;

public class MineFragment extends BaseFragment implements View.OnClickListener, AdapterView.OnItemClickListener {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        view.setPadding(0, StatusBarUtil.getStatusBarHeight(mContext), 0, 0);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void initData() {
        // 会员权益假数据
        List<String> contentList = new ArrayList<>();
        contentList.add("四大体系课程免费听");
        contentList.add("分享赢双倍奖学金");
        MineEquityListAdapter mineEquityListAdapter = new MineEquityListAdapter(mContext, contentList);
        mineVipCard.setEquityListAdapter(mineEquityListAdapter);
        mineVipCard.setDeadLine("2019/10/15");
        // TODO: 判断是否为超级会员显示超级会员卡片
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
        // 金钱容器假数据
        List<MineMoneyItemInfo> itemInfoList = new ArrayList<>();
        MineMoneyItemInfo item_1 = new MineMoneyItemInfo();
        item_1.setItemTitle("￥160.7");
        item_1.setItemDesc("奖学金");
        MineMoneyItemInfo item_2 = new MineMoneyItemInfo();
        item_2.setItemTitle("6张");
        item_2.setItemDesc("优惠券");
        itemInfoList.add(item_1);
        itemInfoList.add(item_2);
        MoneyWrapRecyclerAdapter moneyWrapRecyclerAdapter = new MoneyWrapRecyclerAdapter(mContext, itemInfoList);
        mineMoneyWrapView.setMoneyRecyclerAdapter(moneyWrapRecyclerAdapter);
        // 我正在学假数据
        // TODO: 根据是否登录显示正在学的 item
        // mineLearningWrap.setLearningContainerVisibility(View.GONE);
        mineLearningWrapView.setLearningIconURI("http://pic13.nipic.com/20110331/3032951_224550202000_2.jpg");
        mineLearningWrapView.setLearningTitle("我的财富计划");
        mineLearningWrapView.setLearningUpdate("已更新至07-22期");
        // TODO: 如果没有登录或者登录了没有在学课程就需要将登录描述显示出来否则隐藏
        mineLearningWrapView.setLearningLoginDescVisibility(View.GONE);
        MineLearningListAdapter learningListAdapter = new MineLearningListAdapter(mContext);
        mineLearningWrapView.setLearningListAdapter(learningListAdapter);
    }

    private void initListener() {
        mineTitleView.setMsgClickListener(this);
        mineTitleView.setSettingListener(this);
        mineMsgView.setBuyVipClickListener(this);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        // 网络请求数据代码
//        MinePresenter minePresenter = new MinePresenter(this);
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
            // 超级会员隐藏后奖学金的位置
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = Dp2Px2SpUtil.dp2px(mContext, 20);
            int top = Dp2Px2SpUtil.dp2px(mContext, -36);
            int right = Dp2Px2SpUtil.dp2px(mContext, 20);
            layoutParams.setMargins(left, top, right ,0);
            mineMoneyWrapView.setLayoutParams(layoutParams);
            // 金钱容器假数据
            List<MineMoneyItemInfo> itemInfoList = new ArrayList<>();
            MineMoneyItemInfo item_1 = new MineMoneyItemInfo();
            item_1.setItemTitle("￥0.00");
            item_1.setItemDesc("奖学金");
            MineMoneyItemInfo item_2 = new MineMoneyItemInfo();
            item_2.setItemTitle("0张");
            item_2.setItemDesc("优惠券");
            itemInfoList.add(item_1);
            itemInfoList.add(item_2);
            MoneyWrapRecyclerAdapter moneyWrapRecyclerAdapter = new MoneyWrapRecyclerAdapter(mContext, itemInfoList);
            mineMoneyWrapView.setMoneyRecyclerAdapter(moneyWrapRecyclerAdapter);
            // 我正在学假数据
            // TODO: 根据是否登录显示正在学的 item
            // mineLearningWrap.setLearningContainerVisibility(View.GONE);
            mineLearningWrapView.setLearningIconURI("http://pic13.nipic.com/20110331/3032951_224550202000_2.jpg");
            mineLearningWrapView.setLearningTitle("我的财富计划");
            mineLearningWrapView.setLearningUpdate("已更新至07-22期");
            // TODO: 如果没有登录或者登录了没有在学课程就需要将登录描述显示出来否则隐藏
            mineLearningWrapView.setLearningLoginDescVisibility(View.GONE);
            MineLearningListAdapter learningListAdapter = new MineLearningListAdapter(mContext);
            mineLearningWrapView.setLearningListAdapter(learningListAdapter);
        }

        initListener();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
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

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_title_msg: // 信息
                Toast.makeText(mContext, "点击信息按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mine_title_setting: // 设置
                JumpDetail.jumpAccount(mContext);
                break;
            case R.id.title_avatar: // 头像
                String avatar = CommonUserInfo.getWxAvatar();
                JumpDetail.jumpMineMsg(mContext, avatar);
                break;
            case R.id.title_buy_vip: // 超级会员
                JumpDetail.jumpSuperVip(mContext);
                break;
//            case R.id.card_equity_more: // 更多权益
//                Toast.makeText(mContext, "点击更多权益", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.card_renewal: // 续费
                Toast.makeText(mContext, "点击续费按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.learning_more: // 我正在学 -> 查看更多
                JumpDetail.jumpMineLearning(mContext, "我正在学");
                break;
            case R.id.learning_item_container: // 我正在学的详情页
                // TODO: 根据不同类型跳转到对应的详情页
                Toast.makeText(mContext, "我正在学", Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_container: // 超级会员卡片
                Toast.makeText(mContext, "超级会员卡片", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        switch (position) {
            case 0: // 我的任务
                Toast.makeText(mContext, "我的任务", Toast.LENGTH_SHORT).show();
                break;
            case 1: // 优惠券
                intent = new Intent(mContext, CouponActivity.class);
                startActivity(intent);
                break;
            case 2: // 我的收藏
                JumpDetail.jumpMineLearning(mContext, "我的收藏");
                break;
            case 3: // 离线缓存
                intent = new Intent(mContext, OffLineCacheActivity.class);
                startActivity(intent);
                break;
            case 4: // 兑换码
                intent = new Intent(mContext, CdKeyActivity.class);
                startActivity(intent);
                break;
            default:
                //TODO: 异常判断
                break;
        }
    }
}
