package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.interfaces.OnCustomDialogListener;
import com.xiaoe.shop.wxb.utils.CollectionUtils;
import com.xiaoe.shop.wxb.widget.CustomDialog;

import java.util.List;

public class KnowledgeListAdapter extends BaseAdapter {

    private static final String TAG = "KnowledgeListAdapter";

    private List<KnowledgeCommodityItem> mItemList;
    private Context mContext;
    private LayoutInflater mInflater;

    public KnowledgeListAdapter(Context context, List<KnowledgeCommodityItem> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public KnowledgeCommodityItem getItem(int position) {
        return mItemList == null ? null : mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final KnowledgeHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.knowledge_commodity_list_item, parent, false);
            viewHolder = new KnowledgeHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (KnowledgeHolder) convertView.getTag();
        }
        // 如果是专栏的话需要有两行标题，其他单品就显示一行标题和一行描述
        String srcType = mItemList.get(position).getSrcType();
        if (srcType != null) { // 写的数据的时候并没有添加 srcType 先兼容，课程页用了真数据之后删掉
            if (srcType.equals(DecorateEntityType.TOPIC) || srcType.equals(DecorateEntityType.COLUMN) || srcType.equals(DecorateEntityType.MEMBER)) { // 专栏或者大专栏
                viewHolder.itemTitle.setText(mItemList.get(position).getItemTitle());
                viewHolder.itemTitle.setMaxLines(1);
                viewHolder.itemTitleColumn.setVisibility(View.VISIBLE);
                viewHolder.itemTitleColumn.setText(mItemList.get(position).getItemTitleColumn());
            } else { // 其他单品
                viewHolder.itemTitle.setText(mItemList.get(position).getItemTitle());
                viewHolder.itemTitle.setMaxLines(2);
                viewHolder.itemTitleColumn.setVisibility(View.GONE);
            }
        }
        viewHolder.itemIcon.setImageURI(mItemList.get(position).getItemImg());
        if (mItemList.get(position).isHasBuy()) { // 买了
//            viewHolder.itemPrice.setText("");
//            viewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
//            viewHolder.itemDesc.setText(mItemList.get(position).getItemDesc());
            // 将 desc 文案设置在左边的 textView 中，右边的 textView 内容置空，这个是什么状态
            viewHolder.itemDesc.setText("");
            viewHolder.itemPrice.setText(mItemList.get(position).getItemDesc());
            viewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
            viewHolder.itemPrice.setTextSize(12);
        } else { // 没买
            viewHolder.itemDesc.setText(mItemList.get(position).getItemDesc());
            viewHolder.itemPrice.setText(mItemList.get(position).getItemPrice());
        }
        viewHolder.itemWrap.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (mItemList.get(position).getSrcType() == null) {
                    Toast.makeText(mContext, "正在处理", Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (mItemList.get(position).getSrcType()) {
                    case DecorateEntityType.IMAGE_TEXT:
                        JumpDetail.jumpImageText(mContext, mItemList.get(position).getResourceId(), mItemList.get(position).getItemImg());
                        break;
                    case DecorateEntityType.AUDIO:
                        JumpDetail.jumpAudio(mContext, mItemList.get(position).getResourceId(),0);
                        break;
                    case DecorateEntityType.VIDEO:
                        JumpDetail.jumpVideo(mContext, mItemList.get(position).getResourceId(), "", false);
                        break;
                    case DecorateEntityType.COLUMN:
                        JumpDetail.jumpColumn(mContext, mItemList.get(position).getResourceId(), mItemList.get(position).getItemImg(), 6);
                        break;
                    case DecorateEntityType.TOPIC:
                        JumpDetail.jumpColumn(mContext, mItemList.get(position).getResourceId(), mItemList.get(position).getItemImg(), 8);
                        break;
                    case DecorateEntityType.MEMBER:
                        JumpDetail.jumpColumn(mContext, mItemList.get(position).getResourceId(), mItemList.get(position).getItemImg(), 5);
                        break;
                }
            }
        });
        if (mItemList.get(position).isCollectionList()) {
            viewHolder.itemWrap.setOnLongClickListener(v -> {
                CustomDialog customDialog = new CustomDialog(mContext);
                customDialog.setMessageVisibility(View.GONE);
                customDialog.setTitle("确认删除收藏吗");
                customDialog.setOnCustomDialogListener(new OnCustomDialogListener() {
                    @Override
                    public void onClickCancel(View view, int tag) {
                    }

                    @Override
                    public void onClickConfirm(View view, int tag) {
                        CollectionUtils collectionUtils = new CollectionUtils();
                        String type = convertItemType(mItemList.get(position).getSrcType());
                        collectionUtils.requestRemoveCollection(mItemList.get(position).getResourceId(), type);
                        mItemList.remove(mItemList.get(position));
                        notifyDataSetChanged();
                        Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDialogDismiss(DialogInterface dialog, int tag) {
                    }
                });
                customDialog.showDialog(0);
                return false;
            });
        }
        return convertView;
    }
    public void addAllData(List<KnowledgeCommodityItem> list){
        mItemList.addAll(list);
        notifyDataSetChanged();
    }

    // 转换微页面组件的类型（适配收藏接口）
    private String convertItemType (String type) {
        switch (type) {
            case DecorateEntityType.IMAGE_TEXT:
                return "1";
            case DecorateEntityType.AUDIO:
                return "2";
            case DecorateEntityType.VIDEO:
                return "3";
            case DecorateEntityType.MEMBER:
                return "5";
            case DecorateEntityType.COLUMN:
                return "6";
            case DecorateEntityType.TOPIC:
                return "8";
            default:
                return "";
        }
    }
}
