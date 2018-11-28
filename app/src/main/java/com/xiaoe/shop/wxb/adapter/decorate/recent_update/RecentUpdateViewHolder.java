package com.xiaoe.shop.wxb.adapter.decorate.recent_update;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.MeasureUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.LoginDialogUtils;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

import java.util.Map;

/**
 * 最近更新 ViewHolder
 */
public class RecentUpdateViewHolder extends BaseViewHolder {

    @BindView(R.id.recent_update_sub_list)
    ListView recentUpdateListView;
    @BindView(R.id.recent_update_avatar)
    SimpleDraweeView recentUpdateAvatar;
    @BindView(R.id.recent_update_sub_title)
    TextView recentUpdateSubTitle;
    @BindView(R.id.recent_update_sub_desc)
    TextView recentUpdateSubDesc;
    @BindView(R.id.recent_update_sub_btn)
    Button recentUpdateSubBtn;

    private Context mContext;
    private RecentUpdateListAdapter recentUpdateListAdapter;

    public RecentUpdateViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public RecentUpdateViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void initViewHolder(ComponentInfo currentBindComponent, int currentBindPos, SparseArray<RecentUpdateListAdapter> recentUpdateListAdapterArr) {
        SetImageUriUtil.setImgURI(recentUpdateAvatar, currentBindComponent.getImgUrl(), Dp2Px2SpUtil.dp2px(mContext, 72), Dp2Px2SpUtil.dp2px(mContext, 72));
        recentUpdateSubTitle.setText(currentBindComponent.getTitle());
        recentUpdateSubDesc.setText(currentBindComponent.getDesc());
        // 加载 ListView 的数据
        if (recentUpdateListAdapterArr.get(currentBindPos) == null) {
            recentUpdateListAdapter = new RecentUpdateListAdapter(mContext, currentBindComponent.getSubList(), currentBindComponent.isHasBuy());
            recentUpdateListAdapter.setColumnMsg(currentBindComponent.getColumnId(), currentBindComponent.getSubType());
            recentUpdateListAdapterArr.put(currentBindPos, recentUpdateListAdapter);
        } else {
            recentUpdateListAdapter = recentUpdateListAdapterArr.get(currentBindPos);
            if (recentUpdateListAdapter.getItemList() == null) { // 如果列表没值的话给他赋值
                recentUpdateListAdapter.setItemList(currentBindComponent.getSubList());
            }
        }
        // 进行刷新
        if (AudioMediaPlayer.getAudio() != null && AudioMediaPlayer.getAudio().isPlaying()) { // 有歌在播
            if (AudioMediaPlayer.getAudio().getColumnId().equals(currentBindComponent.getColumnId())) { // 同一个专栏
                recentUpdateSubBtn.setText(R.string.stop_all);
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.class_stopall);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                recentUpdateSubBtn.setCompoundDrawables(drawable, null, null, null);
                recentUpdateSubBtn.setCompoundDrawablePadding(Dp2Px2SpUtil.dp2px(mContext, 6));
            } else { // 不同专栏
                recentUpdateSubBtn.setText(R.string.recent_update_btn_txt);
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.class_playall);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                recentUpdateSubBtn.setCompoundDrawables(drawable, null, null, null);
                recentUpdateSubBtn.setCompoundDrawablePadding(Dp2Px2SpUtil.dp2px(mContext, 6));
            }
        } else { // 没有歌在播
            recentUpdateSubBtn.setText(R.string.recent_update_btn_txt);
            Drawable drawable = mContext.getResources().getDrawable(R.mipmap.class_playall);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            recentUpdateSubBtn.setCompoundDrawables(drawable, null, null, null);
            recentUpdateSubBtn.setCompoundDrawablePadding(Dp2Px2SpUtil.dp2px(mContext, 6));
        }
        recentUpdateListView.setAdapter(recentUpdateListAdapter);
        MeasureUtil.setListViewHeightBasedOnChildren(recentUpdateListView);
        if (currentBindComponent.isHideTitle()) { // 隐藏收听全部按钮
            recentUpdateSubBtn.setVisibility(View.GONE);
        } else {
            recentUpdateSubBtn.setVisibility(View.VISIBLE);
            recentUpdateSubBtn.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    if (currentBindComponent.isFormUser()) {
                        if(!currentBindComponent.isHasBuy()){
                            switch (currentBindComponent.getSubType()) {
                                case DecorateEntityType.TOPIC:
                                    JumpDetail.jumpColumn(mContext, currentBindComponent.getColumnId(), "", 8);
                                    break;
                                case DecorateEntityType.COLUMN:
                                    JumpDetail.jumpColumn(mContext, currentBindComponent.getColumnId(), "", 6);
                                    break;
                                case DecorateEntityType.MEMBER:
                                    JumpDetail.jumpColumn(mContext, currentBindComponent.getColumnId(), "", 5);
                                    break;
                            }
                            return;
                        }
                        if (recentUpdateListAdapter.getItemList() != null &&
                                recentUpdateListAdapter.getItemList().size() > 0 &&
                                recentUpdateListAdapter.getColumnId().equals(currentBindComponent.getColumnId())) { // 同一个组件
                            if (!recentUpdateListAdapter.isPlaying) {
                                recentUpdateListAdapter.clickPlayAll();
                            } else {
                                recentUpdateListAdapter.stopPlayAll();
                            }
                        }
                    } else {
                        LoginDialogUtils.showTouristDialog(mContext);
                    }
                }
            });
        }
        recentUpdateAvatar.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                JumpDetail.jumpColumn(mContext, currentBindComponent.getColumnId(), currentBindComponent.getImgUrl(), 5);
            }
        });
    }
}
