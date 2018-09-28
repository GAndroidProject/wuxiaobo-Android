package xiaoe.com.shop.interfaces;


import xiaoe.com.common.entitys.ItemData;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public interface ItemDataClickListener {

	public void onExpandChildren(ItemData itemData);

	public void onHideChildren(ItemData itemData);

}
