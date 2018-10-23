package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class StatusPagerView extends FrameLayout {
    private static final String TAG = "LoadingView";

    public StatusPagerView(@NonNull Context context) {
        this(context,null);
    }

    public StatusPagerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
