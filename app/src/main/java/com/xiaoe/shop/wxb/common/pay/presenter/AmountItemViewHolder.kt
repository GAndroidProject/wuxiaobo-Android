package com.xiaoe.shop.wxb.common.pay.presenter

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.base.BaseViewHolder

/**
 * @author: zak
 * @date: 2018/12/28
 */
class AmountItemViewHolder(context: Context, itemView: View) : BaseViewHolder(itemView) {

    var mContext: Context = context
    var view: View = itemView
    var boBiItemWrap: LinearLayout = itemView.findViewById(R.id.boBiItemWrap) as LinearLayout
    var boBiTitle: TextView = itemView.findViewById(R.id.boBiTitle) as TextView
    var boBiContent: TextView = itemView.findViewById(R.id.boBiContent) as TextView

}
