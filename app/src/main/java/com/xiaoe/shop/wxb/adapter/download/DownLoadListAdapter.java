package com.xiaoe.shop.wxb.adapter.download;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoe.shop.wxb.R;

import java.util.List;

/**
 * @author: zak
 * @date: 2018/12/24
 */
public class DownLoadListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    private static final int SINGLE_ITEM = 0; // 单品
    private static final int GROUP_TITLE_ITEM = 1; // 非单品待展开
    private static final int GROUP_CONTENT_ITEM = 2; // 非单品展开

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DownLoadListAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(SINGLE_ITEM, R.layout.download_signal);
        addItemType(GROUP_TITLE_ITEM, R.layout.download_group_title_item);

    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {

    }
}
