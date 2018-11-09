package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;

public class SpeedMenuLayout extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "ContentMenuLayout";
    @BindView(R.id.btn_speed_1)
    LinearLayout mBtnSpeed1;
    @BindView(R.id.btn_speed_2)
    LinearLayout mBtnSpeed2;
    @BindView(R.id.btn_speed_3)
    LinearLayout mBtnSpeed3;
    @BindView(R.id.btn_speed_4)
    LinearLayout mBtnSpeed4;
    @BindView(R.id.btn_cancel)
    TextView mBtnCancel;
    @BindView(R.id.menu_bg)
    View mMenuBg;
    private View rootView;

    public SpeedMenuLayout(@NonNull Context context) {
        this(context, null);
    }

    public SpeedMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        rootView = View.inflate(context, R.layout.layout_speed_menu, this);
        ButterKnife.bind(rootView,this);
        mMenuBg.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
    }

    public void setButtonClickListener(OnClickListener listener) {
        if (listener == null) {
            return;
        }
        mBtnSpeed1.setOnClickListener(listener);
        mBtnSpeed2.setOnClickListener(listener);
        mBtnSpeed3.setOnClickListener(listener);
        mBtnSpeed4.setOnClickListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_bg:
            case R.id.btn_cancel:
                setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
