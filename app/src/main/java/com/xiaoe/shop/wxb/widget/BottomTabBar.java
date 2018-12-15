package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.interfaces.OnBottomTabSelectListener;

import java.util.ArrayList;
import java.util.List;

public class BottomTabBar extends FrameLayout implements View.OnClickListener {
    private String TAG = "BottomTabBar";
    private Context mContext;
    private View rootView;
    private LinearLayout bottomTabBar;
    private OnBottomTabSelectListener bottomTabSelectListener;
    private List<BottomBarButton> bottomBarButtonList;
    /**
     * 底部导航栏名称
     */
    private List<String> buttonNames;

    public BottomTabBar(@NonNull Context context) {
        super(context);
        mContext = context;
        initView(mContext);
    }

    public BottomTabBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(mContext);
    }

    private void initView(Context context) {
        rootView = View.inflate(context, R.layout.layout_bottom_tab_bar, this);
        bottomTabBar = (LinearLayout) rootView.findViewById(R.id.bottom_tab_bar);
        bottomBarButtonList = new ArrayList<>();
    }

    public void setBottomTabSelectListener(final OnBottomTabSelectListener listener){
        this.bottomTabSelectListener = listener;
    }
    /**
     * 设置按钮的排布
     * @param orientation
     */
    public void setBottomTabBarOrientation(int orientation){
        bottomTabBar.setOrientation(orientation);
    }

    /**
     * 设置底部tab的个数
     * @param weightSum
     */
    public void setTabBarWeightSum(float weightSum){
        bottomTabBar.setWeightSum(weightSum);
    }

    public void addTabButtonIconByUrl(int count, List<String> buttonNames, List<String> buttonIcons){

    }
    public void addTabButton(@NonNull List<String> buttonNames, List<Integer> buttonIcons, List<Integer> buttonCheckedIcons, int textColor, int textCheckedColor){
        this.buttonNames = buttonNames;

        int[] ids = {R.id.bottom_bar_button_1,R.id.bottom_bar_button_2,R.id.bottom_bar_button_3,R.id.bottom_bar_button_4};
        for (int i = 0; i < buttonNames.size(); i++){
            BottomBarButton bottomBarButton = new BottomBarButton(mContext);

            bottomBarButton.setId(ids[i]);
            bottomBarButton.setButtonText(buttonNames.get(i));
            bottomBarButton.setButtonTextSize(10);
            bottomBarButton.setButtonNameColor(textColor,textCheckedColor);
            bottomBarButton.setButtonImageResource(buttonIcons.get(i), buttonCheckedIcons.get(i));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            lp.weight = 1;
            bottomBarButton.setLayoutParams(lp);
            bottomBarButton.setOnClickListener(this);
            if(i == 0){
                bottomBarButton.setButtonChecked(true);
            }else{
                bottomBarButton.setButtonChecked(false);
            }
            bottomTabBar.addView(bottomBarButton);
            bottomBarButtonList.add(bottomBarButton);
        }
    }

    public BottomBarButton getBottomBarButtonByIndex(int index) {
        return bottomBarButtonList.get(index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bottom_bar_button_1:
                if(bottomTabSelectListener != null){
                    setCheckedButton(0);
                    eventReport(0);
                }
                break;
            case R.id.bottom_bar_button_2:
                if(bottomTabSelectListener != null){
                    setCheckedButton(1);
                    eventReport(1);
                }
                break;
            case R.id.bottom_bar_button_3:
                if(bottomTabSelectListener != null){
                    setCheckedButton(2);
                    eventReport(2);
                }
                break;
            case R.id.bottom_bar_button_4:
                if(bottomTabSelectListener != null){
                    setCheckedButton(3);
                    eventReport(3);
                }
                break;
            default:
                break;
        }
    }

    private void eventReport(int index) {
        String tabName = buttonNames.get(index);
        if (tabName.equals(mContext.getString(R.string.tab_today))) {
            EventReportManager.onEvent(mContext, MobclickEvent.TABBAR_TODAY_CLICK);
        } else if (tabName.equals(mContext.getString(R.string.micro_fragment_title))) {
            EventReportManager.onEvent(mContext, MobclickEvent.TABBAR_COURSE_CLICK);
        } else if (tabName.equals(mContext.getString(R.string.scholarship_title))) {
            EventReportManager.onEvent(mContext, MobclickEvent.TABBAR_SCHOLARSHIP_CLICK);
        } else if (tabName.equals(mContext.getString(R.string.tab_mine))) {
            EventReportManager.onEvent(mContext, MobclickEvent.TABBAR_MINE_CLICK);
        }
    }

    public void setCheckedButton(int index){
        for (int i = 0; i < bottomTabBar.getChildCount(); i++){
            if(index == i){
                continue;
            }
            BottomBarButton bottomBarButton = (BottomBarButton) bottomTabBar.getChildAt(i);
            bottomBarButton.setButtonChecked(false);
        }
        BottomBarButton bottomBarButton = (BottomBarButton) bottomTabBar.getChildAt(index);
        bottomBarButton.setButtonChecked(true);
        bottomTabSelectListener.onCheckedTab(index);
    }
}
