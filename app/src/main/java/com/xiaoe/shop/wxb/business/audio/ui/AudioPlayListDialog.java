package com.xiaoe.shop.wxb.business.audio.ui;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.audio.AudioPlayListAdapter;
import com.xiaoe.shop.wxb.widget.DashlineItemDivider;
import java.util.List;

public class AudioPlayListDialog implements View.OnClickListener {
    private static final String TAG = "AudioPlayListLayout";
    private View rootView;
    private RecyclerView playListRecyclerView;
    private int padding = 0;
    private AudioPlayListAdapter playListAdapter;
    private TextView btnCancel;
    private RelativeLayout layoutAudioPlayList;
    private TextView productsTitle;
    private AlertDialog dialog;

    public AudioPlayListDialog(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_audio_play_list2, null, false);
        padding = Dp2Px2SpUtil.dp2px(context, 20);

        layoutAudioPlayList = (RelativeLayout) rootView.findViewById(R.id.layout_audio_play_list);
        layoutAudioPlayList.setOnClickListener(this);

        //取消按钮
        btnCancel = (TextView) rootView.findViewById(R.id.btn_cancel_play_list);
        btnCancel.setOnClickListener(this);
        //列表
        playListRecyclerView = (RecyclerView) rootView.findViewById(R.id.play_list_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setAutoMeasureEnabled(true);
        playListRecyclerView.setLayoutManager(layoutManager);
        playListRecyclerView.addItemDecoration(new DashlineItemDivider(padding, padding));
        playListAdapter = new AudioPlayListAdapter(context);
        playListRecyclerView.setAdapter(playListAdapter);
        productsTitle = (TextView) rootView.findViewById(R.id.products_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
    }

    public void showDialog() {
        dialog.show();
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(null);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.BOTTOM);
        dialog.setContentView(rootView);
    }

    public void addPlayData(List<AudioPlayEntity> list){
        playListAdapter.addAll(list);
    }

    public void notifyDataSetChanged(){
        if (playListAdapter != null)
            playListAdapter.notifyDataSetChanged();
    }

    public boolean isShow(){
        return dialog != null && dialog.isShowing();
    }

    public void dismissDialog(){
        if (isShow())
            dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_audio_play_list:
            case R.id.btn_cancel_play_list:
                if (isShow())
                    dismissDialog();
                break;
            default:
                break;
        }
    }

    public void setProductsTitle(String title) {
        if (productsTitle != null)
            productsTitle.setText(title);
    }
}
