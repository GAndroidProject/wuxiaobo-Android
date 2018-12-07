package com.xiaoe.shop.wxb.business.download.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.download.CacheViewPagerAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.ui.MiniAudioPlayControllerLayout;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.widget.ScrollViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class OffLineCacheActivity extends XiaoeActivity implements View.OnClickListener {
    private static final String TAG = "OffLineCacheActivity";
    private RelativeLayout btnFinish;
    private TextView finishTitle;
    private View finishSelect;
    private RelativeLayout btnDownloadProceed;
    private TextView downloadProceedTitle;
    private View downloadProceedSelect;
    private ScrollViewPager tabViewPager;
    private MiniAudioPlayControllerLayout miniAudioPlayController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_off_line_cache);
        EventBus.getDefault().register(this);
        initViews();
    }

    private void initViews() {
        //已完成按钮
        btnFinish = (RelativeLayout) findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(this);
        finishTitle = (TextView) findViewById(R.id.finish_title);
        finishSelect = findViewById(R.id.finish_select);
        //下载中按钮
        btnDownloadProceed = (RelativeLayout) findViewById(R.id.btn_download_proceed);
        btnDownloadProceed.setOnClickListener(this);
        downloadProceedTitle = (TextView) findViewById(R.id.download_proceed_title);
        downloadProceedSelect = findViewById(R.id.download_proceed_select);
        setTabSelect(0);

        //tab切换页
        tabViewPager = (ScrollViewPager) findViewById(R.id.tab_view_page);
        tabViewPager.setScroll(false);
        tabViewPager.setAdapter(new CacheViewPagerAdapter(getSupportFragmentManager()));

        ImageView btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        miniAudioPlayController = (MiniAudioPlayControllerLayout) findViewById(R.id.mini_audio_play_controller);
        setMiniAudioPlayController(miniAudioPlayController);
        setMiniPlayerAnimHeight(Dp2Px2SpUtil.dp2px(this, 76));
    }

    private void setTabSelect(int type){
        boolean finish = type == 0;
        btnFinish.setEnabled(!finish);
        finishSelect.setEnabled(!finish);
        finishTitle.setEnabled(!finish);

        btnDownloadProceed.setEnabled(finish);
        downloadProceedSelect.setEnabled(finish);
        downloadProceedTitle.setEnabled(finish);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_finish:
                setTabSelect(0);
                tabViewPager.setCurrentItem(0);
                break;
            case R.id.btn_download_proceed:
                setTabSelect(1);
                tabViewPager.setCurrentItem(1);
                break;
            case R.id.btn_back:
                finish();
                break;
            default:
                break;
        }
    }
    public RelativeLayout getBottomButton(){
        return (RelativeLayout) findViewById(R.id.bottom_button);
    }
    public TextView getAllDeleteButton(){
        return (TextView) findViewById(R.id.btn_all_delete);
    }
    public TextView getAllStartDownloadButton(){
        return (TextView) findViewById(R.id.btn_all_start_download);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 设置悬浮播放器位置
     * @param verb
     * @param subject
     */
    public void setMiniPlayerPosition(int verb, int subject){
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) miniAudioPlayController.getLayoutParams();
        if(verb == RelativeLayout.ALIGN_PARENT_BOTTOM){
            layoutParams.removeRule(RelativeLayout.ABOVE);
        }else{
            layoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        layoutParams.addRule(verb, subject);
        miniAudioPlayController.setLayoutParams(layoutParams);
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
                miniAudioPlayController.setIsClose(false);
                miniAudioPlayController.setAudioTitle(playEntity.getTitle());
                miniAudioPlayController.setAudioImage(playEntity.getImgUrlCompressed());
                miniAudioPlayController.setColumnTitle(playEntity.getProductsTitle());
                miniAudioPlayController.setPlayButtonEnabled(false);
                miniAudioPlayController.setPlayState(AudioPlayEvent.PAUSE);
                break;
            case AudioPlayEvent.PLAY:
                miniAudioPlayController.setVisibility(View.VISIBLE);
                miniAudioPlayController.setIsClose(false);
                miniAudioPlayController.setPlayButtonEnabled(true);
                miniAudioPlayController.setAudioTitle(playEntity.getTitle());
                miniAudioPlayController.setColumnTitle(playEntity.getProductsTitle());
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
            case AudioPlayEvent.CLOSE:
                miniAudioPlayController.close();
                break;
            default:
                break;
        }
    }
}
