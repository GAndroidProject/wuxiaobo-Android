package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pnikosis.materialishprogress.ProgressWheel;

import xiaoe.com.shop.R;

public class ListBottomLoadMoreView extends FrameLayout {
    private static final String TAG = "ListBottomLoadMoreView";
    public static final int STATE_SUCCEED = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_ALL_FINISH = 2;
    public static final int STATE_LOAD_FAILED = 3;
    public static final int STATE_NOT_LOAD = -1;
    private View rootView;
    private ProgressWheel progressWheel;
    private TextView loadText;

    public ListBottomLoadMoreView(@NonNull Context context) {
        this(context,null);
    }

    public ListBottomLoadMoreView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_bottom_load_more, this, true);
        progressWheel = (ProgressWheel) rootView.findViewById(R.id.load_more_progress);
        loadText = (TextView) rootView.findViewById(R.id.load_more_text);
    }

    public void setLoadState(int state){
        switch (state){
            case STATE_NOT_LOAD:
            case STATE_SUCCEED:
            case STATE_LOADING:
                progressWheel.setVisibility(VISIBLE);
                loadText.setText(getResources().getString(R.string.loading_text));
                break;
            case STATE_ALL_FINISH:
                progressWheel.setVisibility(GONE);
                loadText.setText(getResources().getString(R.string.load_all_text));
                break;
            case STATE_LOAD_FAILED:
                progressWheel.setVisibility(GONE);
                loadText.setText(getResources().getString(R.string.click_load_more_text));
                break;
            default:
                break;
        }
    }
}
