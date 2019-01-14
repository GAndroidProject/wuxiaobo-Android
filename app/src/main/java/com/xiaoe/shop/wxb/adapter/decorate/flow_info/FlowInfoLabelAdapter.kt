package com.xiaoe.shop.wxb.adapter.decorate.flow_info

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup

import com.xiaoe.common.entitys.LabelItemEntity
import com.xiaoe.common.utils.Dp2Px2SpUtil
import com.xiaoe.shop.wxb.R
import com.xiaoe.shop.wxb.base.BaseViewHolder
import com.xiaoe.shop.wxb.business.search.presenter.SearchMainContentViewHolder

/**
 * 标签 Adapter
 * @author: zak
 * @date: 2019/1/12
 */
class FlowInfoLabelAdapter(private val mContext: Context, private val labelItemEntityList: List<LabelItemEntity>?) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = View.inflate(parent.context, R.layout.search_main_item, null)
        view.setPadding(Dp2Px2SpUtil.dp2px(mContext, 12f), Dp2Px2SpUtil.dp2px(mContext, 4f),
                Dp2Px2SpUtil.dp2px(mContext, 12f), Dp2Px2SpUtil.dp2px(mContext, 4f))
        return SearchMainContentViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val searchMainContentViewHolder = holder as SearchMainContentViewHolder
        val labelItemEntity = labelItemEntityList?.get(position)
        if (labelItemEntity != null) {
            searchMainContentViewHolder.content.text = labelItemEntity.labelContent
            val labelTextColor: Int = if (labelItemEntity.labelFontColor.length == 4) { // #fff 这种情况
                val tailStr = labelItemEntity.labelContent.substring(1)
                val resultStr = labelItemEntity.labelContent + tailStr
                Color.parseColor(resultStr)
            } else {
                Color.parseColor(labelItemEntity.labelFontColor)
            }
            searchMainContentViewHolder.content.setTextColor(labelTextColor)
            val drawable: GradientDrawable = searchMainContentViewHolder.content.background as GradientDrawable
            val labelBgColor: Int = if (labelItemEntity.labelBackground.length == 4) {
                val tailStr = labelItemEntity.labelBackground.substring(1)
                val resultStr = labelItemEntity.labelBackground + tailStr
                Color.parseColor(resultStr)
            } else {
                Color.parseColor(labelItemEntity.labelBackground)
            }
            drawable.setStroke(1, ContextCompat.getColor(mContext, R.color.self_transparent)) // 将原来的 drawable 的边框置为透明
            drawable.setColor(labelBgColor)
        }
    }

    override fun getItemCount(): Int {
        return labelItemEntityList?.size ?: 0
    }
}
