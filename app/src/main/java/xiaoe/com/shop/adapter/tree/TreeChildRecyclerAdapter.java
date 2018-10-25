package xiaoe.com.shop.adapter.tree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.ItemData;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.OnScrollToListener;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class TreeChildRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
	private String TAG = "TreeChildRecyclerAdapter";
	private Context mContext;
	private List<ItemData> mDataSet;
	private OnScrollToListener onScrollToListener;

	public void setOnScrollToListener(OnScrollToListener onScrollToListener) {
		this.onScrollToListener = onScrollToListener;
	}

	public TreeChildRecyclerAdapter(Context context) {
		mContext = context;
		mDataSet = new ArrayList<ItemData>();
	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.item_recycler_tree_child, parent, false);
		return new ChildViewHolder(mContext, view);
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		ChildViewHolder imageViewHolder = (ChildViewHolder) holder;
		imageViewHolder.bindView(mDataSet.get(position), position);
	}


	@Override
	public int getItemCount() {
		if(mDataSet == null){
			return 0;
		}
		return mDataSet.size();
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

	public void setData(List<ItemData> list) {
		mDataSet = list;
		notifyDataSetChanged();
	}

	public void add(ItemData text, int position) {
		mDataSet.add(position, text);
		notifyItemInserted(position);
	}
	public void add(ItemData text) {
		mDataSet.add(text);
		notifyDataSetChanged();
	}

	public void addAll(List<ItemData> list, int position) {
		mDataSet.addAll(position, list);
		notifyItemRangeInserted(position, list.size());
	}
	public void addAll(List<ItemData> list) {
		int position = mDataSet.size() - 1;
		mDataSet.addAll(list);
		notifyDataSetChanged();
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
