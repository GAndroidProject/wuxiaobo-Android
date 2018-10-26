package xiaoe.com.shop.adapter.tree;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.OnClickListPlayListener;
import xiaoe.com.shop.interfaces.OnScrollToListener;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class TreeChildRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {
	private String TAG = "TreeChildRecyclerAdapter";
	private Context mContext;
	private List<ColumnSecondDirectoryEntity> mDataSet;
	private OnScrollToListener onScrollToListener;
	private OnClickListPlayListener mListPlayListener;


	public TreeChildRecyclerAdapter(Context context, OnClickListPlayListener listPlayListener) {
		mContext = context;
		mDataSet = new ArrayList<ColumnSecondDirectoryEntity>();
		mListPlayListener = listPlayListener;
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
		imageViewHolder.bindView(mDataSet.get(position), position, mListPlayListener);
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

	public List<ColumnSecondDirectoryEntity> getData(){
		return mDataSet;
	}

	public void setData(List<ColumnSecondDirectoryEntity> list) {
		mDataSet = list;
		notifyDataSetChanged();
	}

	public void add(ColumnSecondDirectoryEntity text, int position) {
		mDataSet.add(position, text);
		notifyItemInserted(position);
	}
	public void add(ColumnSecondDirectoryEntity text) {
		mDataSet.add(text);
		notifyDataSetChanged();
	}

	public void addAll(List<ColumnSecondDirectoryEntity> list, int position) {
		mDataSet.addAll(position, list);
		notifyItemRangeInserted(position, list.size());
	}
	public void addAll(List<ColumnSecondDirectoryEntity> list) {
		int position = mDataSet.size() - 1;
		mDataSet.addAll(list);
		notifyDataSetChanged();
	}

	public void setOnScrollToListener(OnScrollToListener onScrollToListener) {
		this.onScrollToListener = onScrollToListener;
	}
}
