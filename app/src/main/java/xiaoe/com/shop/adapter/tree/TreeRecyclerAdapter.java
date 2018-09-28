package xiaoe.com.shop.adapter.tree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xiaoe.com.common.entitys.ItemData;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.ItemDataClickListener;
import xiaoe.com.shop.interfaces.OnScrollToListener;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class TreeRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
	private String TAG = "RecyclerAdapter";
	private Context mContext;
	private List<ItemData> mDataSet;
	private OnScrollToListener onScrollToListener;

	public void setOnScrollToListener(OnScrollToListener onScrollToListener) {
		this.onScrollToListener = onScrollToListener;
	}

	public TreeRecyclerAdapter(Context context) {
		mContext = context;
		mDataSet = new ArrayList<ItemData>();
	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.item_recycler_parent, parent, false);
		return new ParentViewHolder(mContext, view);
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		ParentViewHolder imageViewHolder = (ParentViewHolder) holder;
		imageViewHolder.bindView(mDataSet.get(position), position,
				imageClickListener);
	}

	private ItemDataClickListener imageClickListener = new ItemDataClickListener() {

		@Override
		public void onExpandChildren(ItemData itemData) {
			int position = getCurrentPosition(itemData.getUuid());
			if (onScrollToListener != null) {
				Log.d(TAG, "onExpandChildren: "+position);
				onScrollToListener.scrollTo(position + 1);
			}
		}

		@Override
		public void onHideChildren(ItemData itemData) {
			int position = getCurrentPosition(itemData.getUuid());
			if (onScrollToListener != null) {
				Log.d(TAG, "onHideChildren: "+position);
				onScrollToListener.scrollTo(position);
			}
		}
	};

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}

	private int getChildrenCount(ItemData item) {
		List<ItemData> list = new ArrayList<ItemData>();
		printChild(item, list);
		return list.size();
	}

	private void printChild(ItemData item, List<ItemData> list) {
		list.add(item);
		if (item.getChildren() != null) {
			for (int i = 0; i < item.getChildren().size(); i++) {
				printChild(item.getChildren().get(i), list);
			}
		}
	}


    public List<ItemData> getData() {
        List<ItemData> list = new ArrayList<ItemData>();
        for (int i = 0; i < 30; i++){
            int type = ItemData.ITEM_TYPE_PARENT;
            String text = "专栏-"+i;
			ItemData itemData = new ItemData(type, text,"", UUID.randomUUID().toString(), 1, null);
			List<ItemData> childList = new ArrayList<ItemData>();
			for (int j = 0; j < i + 1; j++){
				childList.add(new ItemData(ItemData.ITEM_TYPE_CHILD, text+"_音视频图文-"+j,"",UUID.randomUUID().toString(), 2,null));
			}
			itemData.setChildren(childList);
            list.add(itemData);
        }
        return list;
    }

	/**
	 * 从position开始删除，删除
	 * 
	 * @param position
	 * @param itemCount
	 *            删除的数目
	 */
	protected void removeAll(int position, int itemCount) {
		for (int i = 0; i < itemCount; i++) {
			mDataSet.remove(position);
		}
		notifyItemRangeRemoved(position, itemCount);
	}

	protected int getCurrentPosition(String uuid) {
		for (int i = 0; i < mDataSet.size(); i++) {
			if (uuid.equalsIgnoreCase(mDataSet.get(i).getUuid())) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getItemViewType(int position) {
		return mDataSet.get(position).getType();
	}

	public void add(ItemData text, int position) {
		mDataSet.add(position, text);
		notifyItemInserted(position);
	}

	public void addAll(List<ItemData> list, int position) {
		Log.d(TAG, "addAll:................ ");
		mDataSet.addAll(position, list);
		notifyItemRangeInserted(position, list.size());
	}

	public void delete(int pos) {
		if (pos >= 0 && pos < mDataSet.size()) {
			if (mDataSet.get(pos).getType() == ItemData.ITEM_TYPE_PARENT
					&& mDataSet.get(pos).isExpand()) {// 父组件并且子节点已经展开
				for (int i = 0; i < mDataSet.get(pos).getChildren().size() + 1; i++) {
					mDataSet.remove(pos);
				}
				notifyItemRangeRemoved(pos, mDataSet.get(pos).getChildren()
						.size() + 1);
			} else {// 孩子节点，或没有展开的父节点
				mDataSet.remove(pos);
				notifyItemRemoved(pos);
			}
		}
	}
}
