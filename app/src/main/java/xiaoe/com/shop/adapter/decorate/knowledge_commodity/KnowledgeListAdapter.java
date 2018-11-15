package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.List;

import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.shop.R;
import xiaoe.com.shop.business.course.ui.CourseImageTextActivity;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.interfaces.OnConfirmListener;
import xiaoe.com.shop.utils.CollectionUtils;
import xiaoe.com.shop.widget.CustomDialog;

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
            if (srcType.equals(DecorateEntityType.TOPIC) || srcType.equals(DecorateEntityType.COLUMN)) { // 专栏或者大专栏
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
        // 将 desc 文案设置在左边的 textView 中，右边的 textView 内容置空，这个是什么状态
//            viewHolder.itemDesc.setText("");
//            viewHolder.itemPrice.setText(mItemList.get(position).getItemDesc());
//            viewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
//            viewHolder.itemPrice.setTextSize(12);
        if (mItemList.get(position).isHasBuy()) { // 买了
            viewHolder.itemPrice.setText("已购");
            viewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
            viewHolder.itemDesc.setText(mItemList.get(position).getItemDesc());
        } else { // 没买
            viewHolder.itemDesc.setText(mItemList.get(position).getItemDesc());
            viewHolder.itemPrice.setText(mItemList.get(position).getItemPrice());
        }
        viewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (mItemList.get(position).getSrcType() == null) {
                    Toast.makeText(mContext, "正在处理", Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (mItemList.get(position).getSrcType()) {
                    case DecorateEntityType.IMAGE_TEXT:
                        intent = new Intent(mContext, CourseImageTextActivity.class);
                        intent.putExtra("imgUrl", mItemList.get(position).getItemImg());
                        intent.putExtra("resourceId", mItemList.get(position).getResourceId());
                        mContext.startActivity(intent);
                        break;
                    case DecorateEntityType.AUDIO:
                        JumpDetail.jumpAudio(mContext, mItemList.get(position).getResourceId(),0);
                        break;
                    case DecorateEntityType.VIDEO:
                        JumpDetail.jumpVideo(mContext, mItemList.get(position).getResourceId(), "", false);
                        break;
                    case DecorateEntityType.COLUMN:
                        JumpDetail.jumpColumn(mContext, mItemList.get(position).getResourceId(), mItemList.get(position).getItemImg(), false);
                        break;
                    case DecorateEntityType.TOPIC:
                        JumpDetail.jumpColumn(mContext, mItemList.get(position).getResourceId(), mItemList.get(position).getItemImg(), true);
                        break;
                }
            }
        });
        if (mItemList.get(position).isCollectionList()) {
            viewHolder.itemWrap.setOnLongClickListener(v -> {
                CustomDialog customDialog = new CustomDialog(mContext);
                customDialog.setMessageVisibility(View.GONE);
                customDialog.setTitle("确认删除收藏吗");
                customDialog.setConfirmListener((view, tag) -> {
                    CollectionUtils collectionUtils = new CollectionUtils();
                    String type = convertItemType(mItemList.get(position).getSrcType());
                    collectionUtils.requestRemoveCollection(mItemList.get(position).getResourceId(), type);
                    mItemList.remove(mItemList.get(position));
                    notifyDataSetChanged();
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
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
            case DecorateEntityType.COLUMN:
                return "6";
            case DecorateEntityType.TOPIC:
                return "8";
            default:
                return "";
        }
    }
}
