package xiaoe.com.shop.business.setting.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.SettingItemInfo;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.setting.presenter.LinearDividerDecoration;
import xiaoe.com.shop.business.setting.presenter.OnItemClickListener;
import xiaoe.com.shop.business.setting.presenter.SettingRecyclerAdapter;

public class SettingAccountActivity extends XiaoeActivity implements OnItemClickListener {

    private static final String TAG = "SettingAccountActivity";

    // 需要切换的 fragment tag
    private static final String ACCOUNT = "account";
    private static final String MESSAGE = "message";
    private static final String CACHE = "cache";
    private static final String VERSION = "version";
    private static final String ABOUT = "about";
    private static final String SUGGESTION = "suggestion";

    @BindView(R.id.setting_account_edit_back)
    ImageView accountBack;
    @BindView(R.id.setting_account_edit_title)
    TextView accountTitle;
    @BindView(R.id.setting_account_content)
    RecyclerView accountContent;
    @BindView(R.id.setting_account_btn)
    Button accountBtn;

    List<SettingItemInfo> itemInfoList;

    private Fragment currentFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_account);
        ButterKnife.bind(this);
        // TODO: 网络请求
        getSupportFragmentManager().beginTransaction().add(R.id.temp_container, new AccountFragment()).commit();
        initData();
        initListener();
    }

    private void initData() {
        // 账号设置页面假数据
        itemInfoList = new ArrayList<>();
        // TODO: 获取最新版本信息和缓存信息
        SettingItemInfo account = new SettingItemInfo("账号设置", "", "");
        SettingItemInfo message = new SettingItemInfo("推送消息设置", "", "");
        SettingItemInfo memory = new SettingItemInfo("消除缓存", "", "");
        SettingItemInfo version = new SettingItemInfo("版本更新", "", "");
        SettingItemInfo us = new SettingItemInfo("关于我们", "", "");
        SettingItemInfo suggestion = new SettingItemInfo("意见反馈", "", "");
        itemInfoList.add(account);
        itemInfoList.add(message);
        itemInfoList.add(memory);
        itemInfoList.add(version);
        itemInfoList.add(us);
        itemInfoList.add(suggestion);

        SettingRecyclerAdapter settingRecyclerAdapter = new SettingRecyclerAdapter(this, itemInfoList, false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        LinearDividerDecoration linearDividerDecoration = new LinearDividerDecoration(this, R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(this, 20));
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        accountContent.setLayoutManager(llm);
        settingRecyclerAdapter.setOnItemClickListener(this);
        accountContent.addItemDecoration(linearDividerDecoration);
        accountContent.setAdapter(settingRecyclerAdapter);
    }

    private void initListener() {
        accountBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        accountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast("退出登录...");
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
    }

    @Override
    public void onItemClick(View view, int position) {
        String tag = int2Str(position);
        replaceFragment(tag);
    }

    private String int2Str(int position) {
        switch (position) {
            case 0: // 账号设置
                return ACCOUNT;
            case 1: // 推送消息设置
                return MESSAGE;
            case 2: // 消除缓存
                return CACHE;
            case 3: // 版本更新
                return VERSION;
            case 4: // 关于我们
                return ABOUT;
            case 5: // 意见反馈
                return SUGGESTION;
            default:
                return null;
        }
    }

    // 切换显示的 fragment
    private void replaceFragment(String tag) {
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            switch (tag) {
                case ACCOUNT: // 账号设置
                    Log.d(TAG, "replaceFragment: ");
                    currentFragment = new AccountFragment();
                    break;
                case MESSAGE: // 推送消息设置
                    Toast("推送消息设置 --- " + tag);
                    break;
                case CACHE: // 消除缓存
                    Toast("消除缓存 --- " + tag);
                    break;
                case VERSION: // 版本更新
                    Toast("版本更新 --- " + tag);
                    break;
                case ABOUT: // 关于我们
                    Toast("关于我们 --- " + tag);
                    break;
                case SUGGESTION: // 意见反馈
                    Toast("意见反馈 --- " + tag);
                    break;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.temp_container, currentFragment, tag).commit();
        } else {
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        }
    }
}
