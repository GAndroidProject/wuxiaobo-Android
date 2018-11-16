package com.xiaoe.shop.wxb.interfaces;


import com.xiaoe.common.entitys.ColumnDirectoryEntity;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public interface OnClickExpandListener {

	public void onExpandChildren(ColumnDirectoryEntity itemData);

	public void onHideChildren(ColumnDirectoryEntity itemData);

}
