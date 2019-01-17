package com.xiaoe.shop.wxb.adapter.decorate.flow_info

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

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
        val bindPos = holder.adapterPosition
        val searchMainContentViewHolder = holder as SearchMainContentViewHolder
        val labelItemEntity = labelItemEntityList?.get(bindPos)
        if (labelItemEntity != null) {
            if (TextUtils.isEmpty(labelItemEntity.labelContent)) {
                searchMainContentViewHolder.content.visibility = View.GONE
            } else {
                searchMainContentViewHolder.content.visibility = View.VISIBLE
                searchMainContentViewHolder.content.text = labelItemEntity.labelContent
            }
            // 有划线价就显示划线价，否则显示真实价格，若二者都没有，隐藏价格位置（第一个 item 是原价或划线价）
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val labelTextColor: Int = when {
                labelItemEntity.labelFontColor == "showPrice" -> {
                    searchMainContentViewHolder.content.paint.flags = 0  // 取消设置的的划线
                    if (searchMainContentViewHolder.content.visibility == View.GONE) {
                        searchMainContentViewHolder.content.setPadding(0, 0, 0, 0)
                        layoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, -8f), 0, 0, 0)
                    } else {
                        searchMainContentViewHolder.content.setPadding(0, Dp2Px2SpUtil.dp2px(mContext, 4f),
                                0, Dp2Px2SpUtil.dp2px(mContext, 4f))
                    }
                    searchMainContentViewHolder.content.layoutParams = layoutParams
                    ContextCompat.getColor(mContext, R.color.price_color)
                }
                labelItemEntity.labelFontColor == "linePrice" -> {
                    searchMainContentViewHolder.content.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG + Paint.ANTI_ALIAS_FLAG // 设置中划线并加清晰
                    if (searchMainContentViewHolder.content.visibility == View.GONE) {
                        searchMainContentViewHolder.content.setPadding(0, 0, 0, 0)
                        layoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, -8f), 0, 0, 0)
                    } else {
                        searchMainContentViewHolder.content.setPadding(0, Dp2Px2SpUtil.dp2px(mContext, 4f),
                                0, Dp2Px2SpUtil.dp2px(mContext, 4f))
                    }
                    searchMainContentViewHolder.content.layoutParams = layoutParams
                    ContextCompat.getColor(mContext, R.color.knowledge_item_desc_color)
                }
                labelItemEntity.labelFontColor.length == 4 -> { // #fff 这种情况
                    val tailStr = labelItemEntity.labelContent.substring(1)
                    val resultStr = labelItemEntity.labelContent + tailStr
                    Color.parseColor(resultStr)
                }
                else -> Color.parseColor(labelItemEntity.labelFontColor)
            }
            searchMainContentViewHolder.content.setTextColor(labelTextColor)
            val drawable: GradientDrawable = searchMainContentViewHolder.content.background as GradientDrawable
            val labelBgColor: Int = when {
                labelItemEntity.labelBackground == "linePrice" || labelItemEntity.labelFontColor == "showPrice" -> {
                    ContextCompat.getColor(mContext, R.color.self_transparent)
                }
                labelItemEntity.labelBackground.length == 4 -> {
                    val tailStr = labelItemEntity.labelBackground.substring(1)
                    val resultStr = labelItemEntity.labelBackground + tailStr
                    Color.parseColor(resultStr)
                }
                else -> Color.parseColor(labelItemEntity.labelBackground)
            }
            drawable.setStroke(1, ContextCompat.getColor(mContext, R.color.self_transparent)) // 将原来的 drawable 的边框置为透明
            drawable.setColor(labelBgColor)
            if (searchMainContentViewHolder.content.visibility == View.VISIBLE) {
                searchMainContentViewHolder.content.background = drawable
            }
        }
    }

    override fun getItemCount(): Int {
        return labelItemEntityList?.size ?: 0
    }
}
