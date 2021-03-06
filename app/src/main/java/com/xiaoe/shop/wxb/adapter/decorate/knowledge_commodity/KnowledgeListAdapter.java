package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.interfaces.OnCustomDialogListener;
import com.xiaoe.shop.wxb.utils.CollectionUtils;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;
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
        updateView(position, viewHolder);

        return convertView;
    }

    private void updateView(int position, KnowledgeHolder viewHolder) {
        if (viewHolder == null || mItemList.get(position) == null)  return;
        // 如果是专栏的话需要有两行标题，其他单品就显示一行标题和一行描述
        String srcType = mItemList.get(position).getSrcType();
        if (srcType != null) { // 写的数据的时候并没有添加 srcType 先兼容，课程页用了真数据之后删掉
            if (srcType.equals(DecorateEntityType.TOPIC) || srcType.equals(DecorateEntityType.COLUMN)
                    || srcType.equals(DecorateEntityType.MEMBER)) { // 专栏或者大专栏
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
        boolean isAudio = "2".equals(convertItemType(mItemList.get(position).getSrcType()));
        viewHolder.listItem.setVisibility(isAudio ? View.VISIBLE : View.GONE);
        viewHolder.itemIcon.setVisibility(isAudio ? View.INVISIBLE : View.VISIBLE);
        if (isAudio){
            SetImageUriUtil.setImgURI(viewHolder.itemIconBg, "res:///" +
                            R.mipmap.audio_list_bg , Dp2Px2SpUtil.dp2px(mContext, 160),
                    Dp2Px2SpUtil.dp2px(mContext, 120));
            String url = TextUtils.isEmpty(mItemList.get(position).getItemImg()) ? "res:///" +
                    R.mipmap.detail_disk : mItemList.get(position).getItemImg();
            int imageWidthDp = 84;
            if (url.contains("res:///") || !SetImageUriUtil.isGif(url)) {// 本地图片
                SetImageUriUtil.setImgURI(viewHolder.itemIcon2, url, Dp2Px2SpUtil.dp2px(mContext, imageWidthDp),
                        Dp2Px2SpUtil.dp2px(mContext, imageWidthDp));
            } else {// 网络图片
                SetImageUriUtil.setRoundAsCircle(viewHolder.itemIcon2, Uri.parse(url));
            }
        }else {
            viewHolder.itemIcon.setImageURI(mItemList.get(position).getItemImg());
        }

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
                        JumpDetail.jumpImageText(mContext, mItemList.get(position).getResourceId(), mItemList.get(position).getItemImg(), "");
                        break;
                    case DecorateEntityType.AUDIO:
                        JumpDetail.jumpAudio(mContext, mItemList.get(position).getResourceId(),0);
                        break;
                    case DecorateEntityType.VIDEO:
                        JumpDetail.jumpVideo(mContext, mItemList.get(position).getResourceId(), "", false, "");
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
                    case DecorateEntityType.SUPER_VIP:
                        if (CommonUserInfo.isIsSuperVipAvailable()) {
                            JumpDetail.jumpSuperVip(mContext);
                        } else {
                            ToastUtils.show(mContext, "app 暂不支持非一年规格的会员购买");
                        }
                        break;
                    default:
                        break;
                }
            }
        });
        if (mItemList.get(position).isCollectionList()) {
            viewHolder.itemWrap.setOnLongClickListener(v -> {
                CustomDialog customDialog = new CustomDialog(mContext);
                customDialog.setMessageVisibility(View.GONE);
                customDialog.setTitle(mContext.getString(R.string.are_delete_favorites));
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
                        ToastUtils.show(mContext, R.string.successfully_delete);
                    }

                    @Override
                    public void onDialogDismiss(DialogInterface dialog, int tag, boolean backKey) {
                    }
                });
                customDialog.showDialog(0);
                return false;
            });
        }
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
