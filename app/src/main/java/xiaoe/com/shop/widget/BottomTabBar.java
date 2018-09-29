package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import xiaoe.com.shop.R;
import xiaoe.com.shop.interfaces.OnBottomTabSelectListener;

public class BottomTabBar extends FrameLayout {
    private String TAG = "BottomTabBar";
    private Context mContext;
    private View rootView;
    private RadioGroup bottomTabBar;

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
        bottomTabBar = (RadioGroup) rootView.findViewById(R.id.bottom_tab_bar);

    }
    public void setBottomTabSelectListener(final OnBottomTabSelectListener listener){
        bottomTabBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++){
                    RadioButton childView = (RadioButton) group.getChildAt(i);
                    if(childView.getId() == checkedId){
                        listener.onCheckedTab(i);
                        break;
                    }
                }
            }
        });
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

    /**
     * 添加一个tab
     * @param tab
     */
    public void addTabButton(View tab){
        bottomTabBar.addView(tab);
    }

    /**
     * 指定位置添加
     * @param tab
     * @param index
     */
    public void addTabButton(View tab, int index){
        bottomTabBar.addView(tab,index);
    }

    /**
     * 添加一组tab
     * @param tabs
     */
    public void addTabButton(List<View> tabs){
        for (int i = 0; i < tabs.size(); i++){
            bottomTabBar.addView(tabs.get(i));
        }
    }
}
