package xiaoe.com.shop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

import xiaoe.com.shop.interfaces.OnCustomScrollChangedListener;

public class CustomScrollView extends ScrollView {
    private static final String TAG = "CustomScrollView";
    private OnCustomScrollChangedListener scrollChanged;
    private int loadState = ListBottomLoadMoreView.STATE_NOT_LOAD;
    private int loadHeight;

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

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

        int height = getHeight();
        int childHeight = getChildAt(0).getHeight();
        Log.d(TAG, "onOverScrolled: "+scrollY+" ; "+height+" ; "+childHeight+" ; "+loadHeight);
        boolean loadMore = scrollY + height >= childHeight - loadHeight;
        if(loadMore && loadState == ListBottomLoadMoreView.STATE_NOT_LOAD && scrollChanged != null){
            scrollChanged.onLoadState(loadState);
        }
    }

    public void setLoadHeigth(int heigth){
        loadHeight = heigth;
    }

    public void setLoadState(int loadState) {
        this.loadState = loadState;
    }

    public int getLoadState() {
        return loadState;
    }
}
