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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.audio.AudioPlayListAdapter;
import xiaoe.com.shop.widget.DashlineItemDivider;

public class AudioPlayListLayout extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "AudioPlayListLayout";
    private View rootView;
    private RecyclerView playListRecyclerView;
    private Context mContext;
    private int padding = 0;
    private AudioPlayListAdapter playListAdapter;
    private TextView btnCancel;
    private RelativeLayout layoutAudioPlayList;

    public AudioPlayListLayout(@NonNull Context context) {
        this(context, null);
    }

    public AudioPlayListLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_audio_play_list, this, true);
        padding = Dp2Px2SpUtil.dp2px(context, 20);
        initView();
    }

    private void initView() {
        layoutAudioPlayList = (RelativeLayout) rootView.findViewById(R.id.layout_audio_play_list);
        layoutAudioPlayList.setOnClickListener(this);

        //取消按钮
        btnCancel = (TextView) rootView.findViewById(R.id.btn_cancel_play_list);
        btnCancel.setOnClickListener(this);
        //列表
        playListRecyclerView = (RecyclerView) rootView.findViewById(R.id.play_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setAutoMeasureEnabled(true);
        playListRecyclerView.setLayoutManager(layoutManager);
        playListRecyclerView.addItemDecoration(new DashlineItemDivider(padding, padding));
        playListAdapter = new AudioPlayListAdapter(mContext);
        playListRecyclerView.setAdapter(playListAdapter);
    }

    public void addPlayData(List<AudioPlayEntity> list){
        playListAdapter.addAll(list);
    }

    public void notifyDataSetChanged(){
        playListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_audio_play_list:
            case R.id.btn_cancel_play_list:
                setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
