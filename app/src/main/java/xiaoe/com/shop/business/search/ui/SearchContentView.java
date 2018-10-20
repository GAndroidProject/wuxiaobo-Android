package xiaoe.com.shop.business.search.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.business.search.presenter.AutoLineFeedLayoutManager;
import xiaoe.com.shop.business.search.presenter.SearchMainContentRecyclerAdapter;
import xiaoe.com.shop.business.search.presenter.SpacesItemDecoration;

public class SearchContentView extends LinearLayout {

    private Context mContext;

    private TextView contentTitleStart;
    private TextView contentTitleEnd;
    private RecyclerView contentRecycler;

    public SearchContentView(Context context) {
        super(context);
        init(context);
    }

    public SearchContentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SearchContentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SearchContentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.search_content_view, this);
        contentTitleStart = (TextView) view.findViewById(R.id.search_main_title_start);
        contentTitleEnd = (TextView) view.findViewById(R.id.search_main_title_end);
        contentRecycler = (RecyclerView) view.findViewById(R.id.search_content);
    }

    public void setTitleStartText(String title) {
        contentTitleStart.setText(title);
    }

    public void setTitleEndText(String title) {
        contentTitleEnd.setText(title);
    }

    public int getTitleStartVisibility() {
        return contentTitleStart.getVisibility();
    }

    public int getTitleEndVisibility() {
        return contentTitleEnd.getVisibility();
    }

    public void setTitleStartVisibility(int visibility) {
        contentTitleStart.setVisibility(visibility);
    }

    public void setTitleEndVisibility(int visibility) {
        contentTitleEnd.setVisibility(visibility);
    }

    public void setContentAdapter(SearchMainContentRecyclerAdapter adapter) {
        AutoLineFeedLayoutManager autoLineFeedLayoutManager = new AutoLineFeedLayoutManager(mContext);
        SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration();
        int top = Dp2Px2SpUtil.dp2px(mContext, 16);
        int right = Dp2Px2SpUtil.dp2px(mContext, 16);
        spacesItemDecoration.setMargin(0, top, right, 0);
        contentRecycler.setLayoutManager(autoLineFeedLayoutManager);
        contentRecycler.addItemDecoration(spacesItemDecoration);
        contentRecycler.setAdapter(adapter);
    }

    public boolean isMatchRecycler (ViewParent view) {
        return contentRecycler == view;
    }
}
