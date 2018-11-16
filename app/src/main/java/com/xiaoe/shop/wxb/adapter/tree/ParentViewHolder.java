package com.xiaoe.shop.wxb.adapter.tree;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.common.entitys.ColumnDirectoryEntity;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.interfaces.OnClickExpandListener;
import com.xiaoe.shop.wxb.interfaces.OnClickListPlayListener;
import com.xiaoe.shop.wxb.widget.DashlineItemDivider;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class ParentViewHolder extends BaseViewHolder {
	private String TAG = "ParentViewHolder";
	private Context mContext;
	private TextView text;
	private RecyclerView childRecyclerView;
	private TreeChildRecyclerAdapter treeChildRecyclerAdapter;
	private final View divisionLine;
	private final LinearLayout btnExpandDown;
    private final LinearLayout btnPlayAll;
    private final RelativeLayout btnExpandTop;

	public ParentViewHolder(Context context, View itemView, int paddingLeft, int paddingRight) {
		super(itemView);
		mContext = context;
		text = (TextView) itemView.findViewById(R.id.text);
		//列表控件
		childRecyclerView = (RecyclerView) itemView.findViewById(R.id.tree_child_recycler_view);
		LinearLayoutManager layoutManager = new LinearLayoutManager(context);
		layoutManager.setAutoMeasureEnabled(true);
		childRecyclerView.setLayoutManager(layoutManager);
		//item分割线
		childRecyclerView.addItemDecoration(new DashlineItemDivider(paddingLeft,paddingRight));
		childRecyclerView.setNestedScrollingEnabled(false);
		treeChildRecyclerAdapter = new TreeChildRecyclerAdapter(context, null);
		childRecyclerView.setAdapter(treeChildRecyclerAdapter);
		//分割线
		divisionLine = itemView.findViewById(R.id.division_line);
		//展开按钮
		btnExpandDown = (LinearLayout) itemView.findViewById(R.id.expand_down_btn);
		//收起按钮
        btnExpandTop = (RelativeLayout) itemView.findViewById(R.id.expand_top_btn);
		//播放全部按钮
        btnPlayAll = (LinearLayout) itemView.findViewById(R.id.play_all_btn);
	}

	public void bindView(final ColumnDirectoryEntity itemData, final int position,
						 final OnClickExpandListener imageClickListener, final OnClickListPlayListener playListener) {
		text.setText(itemData.getTitle());
		if (itemData.isExpand()) {
			divisionLine.setVisibility(View.VISIBLE);
			childRecyclerView.setVisibility(View.VISIBLE);
			setExpandState(View.GONE);
			setPlayButtonState(View.VISIBLE);
		}else{
			divisionLine.setVisibility(View.GONE);
			childRecyclerView.setVisibility(View.GONE);
			setExpandState(View.VISIBLE);
			setPlayButtonState(View.GONE);
		}

		btnPlayAll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				playListener.onPlayPosition(v, position, -1, false);
			}
		});
		treeChildRecyclerAdapter.setParentPosition(position);
		treeChildRecyclerAdapter.setListPlayListener(playListener);
		treeChildRecyclerAdapter.setData(itemData.getResource_list());
		//展开点击事件
		btnExpandDown.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (imageClickListener != null) {
                    imageClickListener.onExpandChildren(itemData);
                    itemData.setExpand(true);
                    divisionLine.setVisibility(View.VISIBLE);
                    childRecyclerView.setVisibility(View.VISIBLE);
                    setExpandState(View.GONE);
                    setPlayButtonState(View.VISIBLE);
				}
			}
		});
        //收起点击事件
        btnExpandTop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (imageClickListener != null) {
                    imageClickListener.onHideChildren(itemData);
                    itemData.setExpand(false);
                    divisionLine.setVisibility(View.GONE);
                    childRecyclerView.setVisibility(View.GONE);
                    setExpandState(View.VISIBLE);
                    setPlayButtonState(View.GONE);
                }
            }
        });
	}
	private void setExpandState(int visibility){
		btnExpandDown.setVisibility(visibility);
		btnExpandTop.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
	}
	private void setPlayButtonState(int visibility){
        btnPlayAll.setVisibility(visibility);
    }

	public TreeChildRecyclerAdapter getTreeChildRecyclerAdapter() {
		return treeChildRecyclerAdapter;
	}
}
