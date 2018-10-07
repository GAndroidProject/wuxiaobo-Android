package xiaoe.com.shop.business.main.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.DraweeTransition;
import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.main.MainFragmentStatePagerAdapter;
import xiaoe.com.shop.interfaces.OnBottomTabSelectListener;
import xiaoe.com.shop.widget.BottomBarButton;
import xiaoe.com.shop.widget.BottomTabBar;
import xiaoe.com.shop.widget.ScrollViewPager;

public class MainActivity extends AppCompatActivity implements OnBottomTabSelectListener {
    private static final String TAG = "MainActivity";
    private BottomTabBar bottomTabBar;
    private ScrollViewPager mainViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setSharedElementEnterTransition(
                    DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP,
                            ScalingUtils.ScaleType.CENTER_CROP)); // 进入
            getWindow().setSharedElementReturnTransition(
                    DraweeTransition.createTransitionSet(ScalingUtils.ScaleType.CENTER_CROP,
                            ScalingUtils.ScaleType.CENTER_CROP)); // 返回
        }
        setContentView(R.layout.activity_main);

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        //状态栏颜色字体(白底黑字)修改 MIUI6+
        //setMiuiStatusBarDarkMode(this,true);
        //状态栏颜色字体(白底黑字)修改 Flyme4+
        //setMeizuStatusBarDarkIcon(this,true);

//        HomepageFragment homepageFragment = new HomepageFragment();
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.main_container, homepageFragment);
//
//        fragmentTransaction.commit();

        initView();

    }

    private void initView() {
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
    public void onCheckedTab(int index) {
        Log.d(TAG, "onCheckedTab: "+index);
        mainViewPager.setCurrentItem(index);
        Log.d(TAG, "onCheckedTab: I "+mainViewPager.getCurrentItem());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100) {
            if (requestCode == 101) {
                String type = data.getStringExtra("type");
                String imgUrl = data.getStringExtra("imgUrl");
                if (type.equals(DecorateEntityType.FLOW_INFO_IMG_TEXT)) {
                    // TODO: 设置背景图片
                    SimpleDraweeView sdv = (SimpleDraweeView) findViewById(R.id.flow_info_img_text_bg);
                    sdv.setImageURI(imgUrl);
                }
            }
        }
    }
}
