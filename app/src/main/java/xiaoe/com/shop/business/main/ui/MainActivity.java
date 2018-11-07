package xiaoe.com.shop.business.main.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.main.MainFragmentStatePagerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.audio.ui.MiniAudioPlayControllerLayout;
import xiaoe.com.shop.events.AudioPlayEvent;
import xiaoe.com.shop.interfaces.OnBottomTabSelectListener;
import xiaoe.com.shop.jpush.ExampleUtil;
import xiaoe.com.shop.jpush.LocalBroadcastManager;
import xiaoe.com.shop.widget.BottomTabBar;
import xiaoe.com.shop.widget.ScrollViewPager;

public class MainActivity extends XiaoeActivity implements OnBottomTabSelectListener {
    private static final String TAG = "MainActivity";
    private BottomTabBar bottomTabBar;
    private ScrollViewPager mainViewPager;
    private MiniAudioPlayControllerLayout miniAudioPlayController;

    public static final String MICRO_PAGE_MAIN = "app_home_page";
    public static final String MICRO_PAGE_COURSE = "app_course_page";

    public static boolean isForeground = false;

//    SettingPresenter settingPresenter;

//    String apiToken;
//    List<LoginUserInfo> loginUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置状态栏需要在 setContentView 之前进行
        setStatusBar();

        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);

//        apiToken = CommonUserInfo.getApiToken();
//        loginUserList = getLoginUserInfoList();
//        settingPresenter = new SettingPresenter(this);
//        settingPresenter.requestPersonData(apiToken, false);

        initView();
        initPermission();
        Intent audioPlayServiceIntent = new Intent(this, AudioMediaPlayer.class);
        startService(audioPlayServiceIntent);

        registerMessageReceiver();  // used for receive msg
    }

    private void initView() {
        RelativeLayout mainActivityRootView = (RelativeLayout) findViewById(R.id.main_activity_root_view);
        mainActivityRootView.setBackgroundColor(Color.parseColor(Global.g().getGlobalColor()));
        bottomTabBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar_layout);
        bottomTabBar.setBottomTabBarOrientation(LinearLayout.HORIZONTAL);
        bottomTabBar.setTabBarWeightSum(4);
        bottomTabBar.setBottomTabSelectListener(this);
        List<String> buttonNames = new ArrayList<String>();
        buttonNames.add("今日");
        buttonNames.add("课程");
        buttonNames.add("任务");
        buttonNames.add("我的");
        List<Integer> buttonCheckedIcons = new ArrayList<Integer>();
        buttonCheckedIcons.add(R.mipmap.today_selected);
        buttonCheckedIcons.add(R.mipmap.class_selected);
        buttonCheckedIcons.add(R.mipmap.task_selected);
        buttonCheckedIcons.add(R.mipmap.profile_selected);
        List<Integer> buttonIcons = new ArrayList<Integer>();
        buttonIcons.add(R.mipmap.today_default);
        buttonIcons.add(R.mipmap.class_default);
        buttonIcons.add(R.mipmap.task_default);
        buttonIcons.add(R.mipmap.profile_default);
        bottomTabBar.addTabButton(4, buttonNames, buttonIcons, buttonCheckedIcons, getResources().getColor(R.color.secondary_button_text_color), getResources().getColor(R.color.high_title_color));

        mainViewPager = (ScrollViewPager) findViewById(R.id.main_view_pager);
        mainViewPager.setScroll(false);
        mainViewPager.setAdapter(new MainFragmentStatePagerAdapter(getSupportFragmentManager()));
        mainViewPager.setOffscreenPageLimit(3);

        miniAudioPlayController = (MiniAudioPlayControllerLayout) findViewById(R.id.mini_audio_play_controller);
        setMiniAudioPlayController(miniAudioPlayController);
        miniAudioPlayController.setMiniPlayerAnimHeight(Dp2Px2SpUtil.dp2px(this, 76));
        setMiniPlayerAnimHeight(Dp2Px2SpUtil.dp2px(this, 76));
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

    protected void replaceFragment(int index) {
        mainViewPager.setCurrentItem(index);
        bottomTabBar.setCheckedButton(index);
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        if(playEntity == null){
            return;
        }
        switch (event.getState()){
            case AudioPlayEvent.LOADING:
                miniAudioPlayController.setVisibility(View.VISIBLE);
                miniAudioPlayController.setAudioTitle(playEntity.getTitle());
                miniAudioPlayController.setPlayButtonEnabled(false);
                miniAudioPlayController.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.PLAY:
                miniAudioPlayController.setPlayButtonEnabled(true);
                miniAudioPlayController.setAudioTitle(playEntity.getTitle());
                miniAudioPlayController.setPlayState(AudioPlayEvent.PLAY);
                miniAudioPlayController.setMaxProgress(AudioMediaPlayer.getDuration());
                break;
            case AudioPlayEvent.PAUSE:
                miniAudioPlayController.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.STOP:
                miniAudioPlayController.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.PROGRESS:
                miniAudioPlayController.setProgress(event.getProgress());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(!AudioMediaPlayer.isStop()){
            AudioMediaPlayer.release();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
    }

    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!ExampleUtil.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    setCustomMsg(showMsg.toString());
                }
            } catch (Exception ignored){
            }
        }
    }

    private void setCustomMsg(String msg){
//        if (null != msgText) {
//            msgText.setText(msg);
//            msgText.setVisibility(android.view.View.VISIBLE);
//        }
    }
}
