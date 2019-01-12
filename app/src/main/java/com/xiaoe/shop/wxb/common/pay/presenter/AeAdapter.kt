package com.xiaoe.shop.wxb.common.pay.presenter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.xiaoe.common.entitys.AeDataItem
import com.xiaoe.shop.wxb.R

/**
 * @author: zak
 * @date: 2019/1/10
 */
class AeAdapter(context: Context, private var aeDataItem: MutableList<AeDataItem>) : BaseAdapter() {

    private var mContext: Context = context

    override fun getCount(): Int {
        return aeDataItem.size
    }

    override fun getItem(position: Int): AeDataItem {
        return aeDataItem[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var itemView = convertView
        val aeViewHolder: AeViewHolder
        if (itemView == null) {
            itemView = LayoutInflater.from(mContext).inflate(R.layout.wr_list_item, parent, false)
            aeViewHolder = AeViewHolder(mContext, itemView)
            itemView.tag = aeViewHolder
        } else {
            aeViewHolder = itemView.tag as AeViewHolder
        }

        aeViewHolder.itemTitle.text = aeDataItem[position].remark
        aeViewHolder.itemTime.text = aeDataItem[position].createdAt
        aeViewHolder.itemDesc.visibility = View.GONE
        val price = String.format("%1.2f", aeDataItem[position].amount.toFloat() / 100) + "波豆"
        var finalPrice = ""
        if (aeDataItem[position].flowType == 1) { // 充值
            finalPrice = "+$price"
            aeViewHolder.itemMoney.setTextColor(ContextCompat.getColor(mContext, R.color.high_title_color))
        } else if (aeDataItem[position].flowType == 2) { // 支出
            finalPrice = "-$price"
            aeViewHolder.itemMoney.setTextColor(ContextCompat.getColor(mContext, R.color.mine_super_vip_bg))
        }
        aeViewHolder.itemMoney.text = finalPrice

        return itemView
    }
}
