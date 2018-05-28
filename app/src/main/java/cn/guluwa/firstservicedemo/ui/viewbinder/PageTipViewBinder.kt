package cn.guluwa.firstservicedemo.ui.viewbinder

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.guluwa.firstservicedemo.R
import cn.guluwa.firstservicedemo.data.bean.PageTipBean
import cn.guluwa.firstservicedemo.databinding.ListPageTipLayoutBinding
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by guluwa on 2018/4/27.
 */

class PageTipViewBinder : ItemViewBinder<PageTipBean, PageTipViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder{
        return ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.list_page_tip_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PageTipBean) {
        holder.databinding.pageTipBean = item
    }

    inner class ViewHolder(val databinding: ListPageTipLayoutBinding) : RecyclerView.ViewHolder(databinding.root) {

    }
}