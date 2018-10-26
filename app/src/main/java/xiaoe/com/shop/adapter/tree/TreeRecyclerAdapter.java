package xiaoe.com.shop.adapter.tree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.ColumnDirectoryEntity;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
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
	private List<ColumnDirectoryEntity> mDataSet;
	private OnScrollToListener onScrollToListener;
	private int paddingLeft = 0;

	public TreeRecyclerAdapter(Context context) {
		mContext = context;
		mDataSet = new ArrayList<ColumnDirectoryEntity>();
		paddingLeft = Dp2Px2SpUtil.dp2px(mContext, 16);
	}

	@Override
	public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.item_recycler_parent, parent, false);
		return new ParentViewHolder(mContext, view, paddingLeft, paddingLeft);
	}

	@Override
	public void onBindViewHolder(BaseViewHolder holder, int position) {
		ParentViewHolder imageViewHolder = (ParentViewHolder) holder;
		imageViewHolder.bindView(mDataSet.get(position), position,
				imageClickListener);
	}

	private ItemDataClickListener imageClickListener = new ItemDataClickListener() {

		@Override
		public void onExpandChildren(ColumnDirectoryEntity itemData) {
		}

		@Override
		public void onHideChildren(ColumnDirectoryEntity itemData) {
		}
	};

	@Override
	public int getItemCount() {
		return mDataSet.size();
	}

	public void setOnScrollToListener(OnScrollToListener onScrollToListener) {
		this.onScrollToListener = onScrollToListener;
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

	public void setData(List<ColumnDirectoryEntity> list) {
		mDataSet = list;
		notifyDataSetChanged();
	}

	public void add(ColumnDirectoryEntity text, int position) {
		mDataSet.add(position, text);
		notifyItemInserted(position);
	}
	public void add(ColumnDirectoryEntity text) {
		mDataSet.add( text);
		notifyDataSetChanged();
	}
	public void addAll(List<ColumnDirectoryEntity> list, int position) {
		mDataSet.addAll(position, list);
		notifyItemRangeInserted(position, list.size());
	}
	public void addAll(List<ColumnDirectoryEntity> list) {
		mDataSet.addAll(list);
		notifyDataSetChanged();
	}

}
