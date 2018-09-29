package xiaoe.com.shop.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import xiaoe.com.shop.R;

public class BottomBarButton extends FrameLayout {
    private static final String TAG = "BottomBarButton";
    private static int view_id = R.id.bottom_bar_button + 1000;
    public static final int DRAWABLE_LEFT = 0;
    public static final int DRAWABLE_TOP = 1;
    public static final int DRAWABLE_RIGHT = 2;
    public static final int DRAWABLE_BOTTOM = 3;
    private Context mContext;
    private View rootView;
    private RadioButton bottomBarButton;

    public BottomBarButton(@NonNull Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public BottomBarButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        rootView = View.inflate(context, R.layout.view_bottom_bar_button, null);
        bottomBarButton = (RadioButton) rootView.findViewById(R.id.bottom_bar_button);
        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        bottomBarButton.setId(view_id ++);
        lp.weight = 1;
        bottomBarButton.setLayoutParams(lp);
    }

    /**
     * 设置按钮文字
     * @param text
     */
    public void setButtonText(String text){
        bottomBarButton.setText(text);
    }

    /**
     * 设置按钮上的图标
     * @param resources
     * @param position
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    public void setButtonDrawable(int resources,int position, int left, int top, int right, int bottom){
        Drawable drawable = getResources().getDrawable(resources);
        drawable.setBounds(left, top, right, bottom);
        if(position == DRAWABLE_LEFT){
            bottomBarButton.setCompoundDrawables(drawable, null, null, null);
        }else if(position == DRAWABLE_TOP){
            bottomBarButton.setCompoundDrawables(null, drawable, null, null);
        }else if(position == DRAWABLE_RIGHT){
            bottomBarButton.setCompoundDrawables(null, null, drawable, null);
        }else if(position == DRAWABLE_BOTTOM){
            bottomBarButton.setCompoundDrawables(null, null, null, drawable);
        }
    }

    /**
     * 设置按钮状态
     * @param checked
     */
    public void setButtonChecked(boolean checked){
        bottomBarButton.setChecked(checked);
    }

    public RadioButton getTabButton(){
        return  bottomBarButton;
    }
}
