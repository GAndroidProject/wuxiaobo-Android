package com.xiaoe.shop.wxb.common.pay.presenter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.xiaoe.common.entitys.AmountDataItem
import com.xiaoe.common.entitys.AmountItemEntity
import com.xiaoe.common.interfaces.OnItemClickWithAmountListener
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.shop.wxb.R

import com.xiaoe.shop.wxb.base.BaseViewHolder
import org.jetbrains.anko.find

/**
 * @author: zak
 * @date: 2018/12/28
 */
class AmountAdapter(context: Context, dataList: MutableList<AmountDataItem>) : RecyclerView.Adapter<BaseViewHolder>() {

    private var mContext: Context = context
    private var amountList: MutableList<AmountDataItem> = dataList
    private lateinit var onItemClickWithAmountListener: OnItemClickWithAmountListener
    private var itemArr: MutableMap<Int, AmountItemViewHolder> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder? {
        val view = LayoutInflater.from(mContext).inflate(R.layout.bo_bi_amount_item, parent, false)
        return AmountItemViewHolder(mContext, view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        with (holder as AmountItemViewHolder) {
            with(amountList[position]) {
                val realPos = holder.adapterPosition
                if (!itemArr.containsKey(realPos) && !itemArr.containsValue(holder)) {
                    itemArr[realPos] = holder
                }
                if (position == 0) {
                    boBiItemWrap.background = ContextCompat.getDrawable(mContext, R.drawable.bo_bi_item_full_bg)
                    boBiTitle.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                    boBiContent.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                }
                boBiItemWrap.setOnClickListener { onItemClickWithAmountListener.onAmountItemClick(view, amountList[position], position) }
                boBiTitle.text = String.format(mContext.resources.getString(R.string.bo_bi_item_title), balance)
                boBiContent.text = String.format(mContext.resources.getString(R.string.bo_bi_item_content), price)
            }
        }
    }

    override fun getItemCount(): Int {
        return amountList.size
    }

    fun setOnItemClickWithAmountListener(onItemClickWithAmountListener: OnItemClickWithAmountListener) {
        this.onItemClickWithAmountListener = onItemClickWithAmountListener
    }

    /**
     * 刷新 item 的样式
     */
    fun refreshItemStyle(pos:Int) {
        for (item in itemArr) {
            val viewHolder:AmountItemViewHolder = item.value
            val wrap = viewHolder.boBiItemWrap
            val title = viewHolder.boBiTitle
            val content = viewHolder.boBiContent
            if (pos == item.key) {
                wrap.background = ContextCompat.getDrawable(mContext, R.drawable.bo_bi_item_full_bg)
                title.setTextColor(ContextCompat.getColor(mContext, R.color.white))
                content.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            } else {
                wrap.background = ContextCompat.getDrawable(mContext, R.drawable.bo_bi_item_bg)
                title.setTextColor(ContextCompat.getColor(mContext, R.color.high_title_color))
                content.setTextColor(ContextCompat.getColor(mContext, R.color.secondary_title_color))
            }
        }
    }
}
