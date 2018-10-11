package xiaoe.com.shop.business.main.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import xiaoe.com.common.app.Global;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.main.MainFragmentStatePagerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.interfaces.OnBottomTabSelectListener;
import xiaoe.com.shop.utils.StatusBarUtil;
import xiaoe.com.shop.widget.BottomBarButton;
import xiaoe.com.shop.widget.BottomTabBar;
import xiaoe.com.shop.widget.ScrollViewPager;

public class MainActivity extends XiaoeActivity implements OnBottomTabSelectListener {
    private static final String TAG = "MainActivity";
    private BottomTabBar bottomTabBar;
    private ScrollViewPager mainViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            Global.g().setGlobalColor("#000000");
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_VISIBLE);
        }
        // 透明状态栏
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        initView();
        initPermission();
        Intent audioPlayServiceIntent = new Intent(this, AudioMediaPlayer.class);
        startService(audioPlayServiceIntent);
    }

    private void initView() {
        RelativeLayout mainActivityRootView = (RelativeLayout) findViewById(R.id.main_activity_root_view);
        mainActivityRootView.setBackgroundColor(Color.parseColor(Global.g().getGlobalColor()));
        bottomTabBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar_layout);
        bottomTabBar.setBottomTabBarOrientation(LinearLayout.HORIZONTAL);
        bottomTabBar.setTabBarWeightSum(3);
        bottomTabBar.setBottomTabSelectListener(this);
        for (int i = 0; i < 3; i++){
            BottomBarButton radioButton = new BottomBarButton(this);
            radioButton.setButtonText("按钮"+(i + 1));
            bottomTabBar.addTabButton(radioButton.getTabButton());
        }

        mainViewPager = (ScrollViewPager) findViewById(R.id.main_view_pager);
        mainViewPager.setScroll(true);
        mainViewPager.setAdapter(new MainFragmentStatePagerAdapter(getSupportFragmentManager()));
        mainViewPager.setOffscreenPageLimit(3);
    }

    @Override
    public void initPermission() {
        super.initPermission();
    }

    @Override
    public void onCheckedTab(int index) {
        Log.d(TAG, "onCheckedTab: "+index);
        mainViewPager.setCurrentItem(index);
        Log.d(TAG, "onCheckedTab: I "+mainViewPager.getCurrentItem());
    }
}
