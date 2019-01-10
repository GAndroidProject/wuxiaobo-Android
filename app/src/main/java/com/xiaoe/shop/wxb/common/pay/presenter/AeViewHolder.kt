package com.xiaoe.shop.wxb.common.pay.presenter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.xiaoe.shop.wxb.R

/**
 * @author: zak
 * @date: 2019/1/10
 */
class AeViewHolder(context: Context, itemView: View) {

    var mContext: Context = context
    var view: View = itemView
    var itemTitle: TextView = itemView.findViewById(R.id.wr_item_title) as TextView
    var itemMoney: TextView = itemView.findViewById(R.id.wr_item_money) as TextView
    var itemTime: TextView = itemView.findViewById(R.id.wr_item_time) as TextView
    var itemDesc: TextView = itemView.findViewById(R.id.wr_item_state) as TextView
}