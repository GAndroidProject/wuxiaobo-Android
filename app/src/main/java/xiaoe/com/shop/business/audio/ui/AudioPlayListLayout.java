package xiaoe.com.shop.business.audio.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.widget.DashlineItemDivider;

public class AudioPlayListLayout extends FrameLayout {
    private static final String TAG = "AudioPlayListLayout";
    private View rootView;
    private RecyclerView playListRecyclerView;
    private Context mContext;
    private int padding = 0;

    public AudioPlayListLayout(@NonNull Context context) {
        this(context, null);
    }

    public AudioPlayListLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_audio_play_list, this, true);
        padding = Dp2Px2SpUtil.dp2px(context, 16);
        initView();
    }

    private void initView() {
        playListRecyclerView = (RecyclerView) rootView.findViewById(R.id.play_list_recycler_view);
        playListRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        playListRecyclerView.addItemDecoration(new DashlineItemDivider(padding, padding));
    }
}
