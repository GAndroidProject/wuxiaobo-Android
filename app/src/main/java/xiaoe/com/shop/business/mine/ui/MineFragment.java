package xiaoe.com.shop.business.mine.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.MineMoneyItemInfo;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.main.presenter.MinePresenter;
import xiaoe.com.shop.business.mine.presenter.MineEquityListAdapter;
import xiaoe.com.shop.business.mine.presenter.MoneyWrapRecyclerAdapter;

public class MineFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "MineFragment";

    private Unbinder unbinder;
    private Context mContext;

    @BindView(R.id.mine_title_view)
    MineTitleView mineTitleView;
    @BindView(R.id.mine_msg_view)
    MineMsgView mineMsgView;
    @BindView(R.id.mine_vip_view)
    MineVipCard mineVipCard;
    @BindView(R.id.mine_money_view)
    MineMoneyWrap mineMoneyWrap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mine, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initListener();
    }

    private void initData() {
        mineMsgView.setNickName("名字太长了呀哈哈哈");
        mineMsgView.setAvatar("http://pic13.nipic.com/20110331/3032951_224550202000_2.jpg");
        // TODO: 根据登陆态度判断 mineMsgView 的数据展示
        // 会员权益假数据
        List<String> contentList = new ArrayList<>();
        contentList.add("四大体系课程免费听");
        contentList.add("分享赢双倍奖学金");
        MineEquityListAdapter mineEquityListAdapter = new MineEquityListAdapter(getActivity(), contentList);
        mineVipCard.setEquityListAdapter(mineEquityListAdapter);
        mineVipCard.setDeadLine("2019/10/15");
        // TODO: 判断是否为超级会员显示超级会员卡片
        if (mineVipCard.getVisibility() == View.VISIBLE) { // 超级会员卡片存在
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = Dp2Px2SpUtil.dp2px(getActivity(), 20);
            int top = Dp2Px2SpUtil.dp2px(getActivity(), 16);
            int right = Dp2Px2SpUtil.dp2px(getActivity(), 20);
            layoutParams.setMargins(left,top,right,0);
            mineMoneyWrap.setLayoutParams(layoutParams);
        } else if (mineVipCard.getVisibility() == View.GONE) { // 超级会员卡片不存在
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int left = Dp2Px2SpUtil.dp2px(getActivity(), 20);
            int top = Dp2Px2SpUtil.dp2px(getActivity(), -36);
            int right = Dp2Px2SpUtil.dp2px(getActivity(), 20);
            layoutParams.setMargins(left, top, right ,0);
            mineMoneyWrap.setLayoutParams(layoutParams);
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
        MoneyWrapRecyclerAdapter moneyWrapRecyclerAdapter = new MoneyWrapRecyclerAdapter(getActivity(), itemInfoList);
        mineMoneyWrap.setMoneyRecyclerAdapter(moneyWrapRecyclerAdapter);
    }

    private void initListener() {
        mineTitleView.setMsgClickListener(this);
        mineTitleView.setSettingListener(this);
        mineMsgView.setBuyVipClickListener(this);
        mineVipCard.setBtnRenewalClickListener(this);
        mineVipCard.setMoreEquityClickListener(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 网络请求数据代码
//        MinePresenter minePresenter = new MinePresenter(this);
//        minePresenter.requestData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: isSuccess --- " + success);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mine_title_msg:
                Toast.makeText(getActivity(), "点击信息按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mine_title_setting:
                Toast.makeText(getActivity(), "点击设置按钮", Toast.LENGTH_SHORT).show();
                break;
            case R.id.title_buy_vip:
                Toast.makeText(getActivity(), "成为超级会员", Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_equity_more:
                Toast.makeText(getActivity(), "点击更多权益", Toast.LENGTH_SHORT).show();
                break;
            case R.id.card_renewal:
                Toast.makeText(getActivity(), "点击续费按钮", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
