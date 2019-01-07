package com.xiaoe.shop.wxb.adapter.download;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoe.common.entitys.CommonDownloadBean;
import com.xiaoe.common.interfaces.OnItemClickWithCdbItemListener;
import com.xiaoe.common.utils.DateFormat;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

/**
 * @author: zak
 * @date: 2019/1/3
 */
public class DownloadListItemViewHolder extends BaseViewHolder {

    private Context mContext;
    private View mItemView;
    private int mInitType;

    private FrameLayout groupWrap;
    private TextView groupHead;
    private TextView groupCount;
    private ImageView groupCheck;
    private View groupBottomLine;

    private FrameLayout singleWrap;
    private TextView singleHead;
    private TextView singleLength;
    private View singleBottomLine;

    public DownloadListItemViewHolder(Context context, View itemView, int initType) {
        super(itemView);
        this.mContext = context;
        this.mItemView = itemView;
        this.mInitType = initType;
    }

    public void initViewHolder(CommonDownloadBean commonDownloadBean, OnItemClickWithCdbItemListener onItemClickWithCdbItemListener) {
        if (mInitType == DownLoadListAdapter.GROUP_TYPE) {
            initGroupItem(commonDownloadBean);
        } else if (mInitType == DownLoadListAdapter.SINGLE_TYPE) {
            initSingleItem(commonDownloadBean, onItemClickWithCdbItemListener);
        } else {
            throw new IllegalArgumentException("类型错误！请使用单品或非单品类型。");
        }
    }

    /**
     * 初始化非单品的 item
     */
    private void initGroupItem(CommonDownloadBean commonDownloadBean) {
        groupWrap = (FrameLayout) mItemView.findViewById(R.id.groupWrap);
        groupHead = (TextView) mItemView.findViewById(R.id.groupItemTitle);
        groupCount = (TextView) mItemView.findViewById(R.id.groupItemCount);
        groupCheck = (ImageView) mItemView.findViewById(R.id.groupItemIcon);
        groupBottomLine = mItemView.findViewById(R.id.groupItemLine);

        groupHead.setText(commonDownloadBean.getTitle());
        groupCount.setText(String.format(mContext.getString(R.string.download_count), 20));
        groupCheck.setVisibility(View.GONE);
        groupBottomLine.setVisibility(View.VISIBLE);

        groupWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupCheck.getVisibility() == View.VISIBLE) {
                    groupCheck.setVisibility(View.GONE);
                } else {
                    groupCheck.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /**
     * 初始化单品的 item
     */
    private void initSingleItem(CommonDownloadBean commonDownloadBean, OnItemClickWithCdbItemListener onItemClickWithCdbItemListener) {
        singleWrap = (FrameLayout) mItemView.findViewById(R.id.singleWrap);
        singleHead = (TextView) mItemView.findViewById(R.id.singleItemContent);
        singleLength = (TextView) mItemView.findViewById(R.id.singleItemLength);
        singleBottomLine = mItemView.findViewById(R.id.singleItemLine);

        singleHead.setText(commonDownloadBean.getTitle());
        if (commonDownloadBean.getResourceType() == 2) { // 音频
            singleLength.setText(DateFormat.longToString(commonDownloadBean.getAudioLength() * 1000));
        } else if (commonDownloadBean.getResourceType() == 3) { // 视频
            singleLength.setText(DateFormat.longToString(commonDownloadBean.getVideoLength() * 1000));
        }
        singleBottomLine.setVisibility(View.VISIBLE);

        if (commonDownloadBean.isSelected()) {
            singleHead.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.download_checking), null, null, null);
        } else {
            singleHead.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.download_tocheck), null, null, null);
        }

        if (DownloadManager.getInstance().isDownload(commonDownloadBean.getAppId(), commonDownloadBean.getResourceId())) { // 不能选中
            commonDownloadBean.setEnable(false);
        }

        if (!commonDownloadBean.isEnable()) {
            singleHead.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.download_alreadychecked), null, null, null);
        }


        singleWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickWithCdbItemListener.onCommonDownloadBeanItemClick(v, commonDownloadBean);
            }
        });
    }
}
