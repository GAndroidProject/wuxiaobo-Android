package xiaoe.com.shop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import xiaoe.com.shop.interfaces.OnCustomScrollChangedListener;

public class CustomScrollView extends ScrollView {
    private OnCustomScrollChangedListener scrollChanged;
    public CustomScrollView(Context context) {
        this(context,null);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollChanged != null){
            scrollChanged.onScrollChanged(l,t,oldl,oldt);
        }
    }
    public void setScrollChanged(OnCustomScrollChangedListener changed){
        scrollChanged = changed;
    }
}
