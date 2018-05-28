package cn.guluwa.firstservicedemo.ui.viewbinder

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.guluwa.firstservicedemo.R
import cn.guluwa.firstservicedemo.data.bean.WordBean
import cn.guluwa.firstservicedemo.databinding.WordListItemBinding
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.utils.AppUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import me.drakeet.multitype.ItemViewBinder

/**
 * Created by guluwa on 2018/4/27.
 */
class WordViewBinder : ItemViewBinder<WordBean, WordViewBinder.ViewHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.word_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: WordBean) {
        holder.databinding.tvContent.text = item.content
        holder.databinding.tvAuthor.text = item.author!!.username
        holder.databinding.tvCreateTime.text = item.createTimeStr
        Glide.with(holder.itemView.context).asBitmap()
                .apply(RequestOptions().circleCrop())
                .load(Contacts.BASEURL + item.author!!.thumb)
                .into(holder.databinding.ivUserImage)
        when {
            holder.adapterPosition == 0 -> {
                holder.databinding.mTopView.visibility = View.VISIBLE
                holder.databinding.mBotView.visibility = View.GONE
            }
            holder.adapterPosition == adapter.items.size - 1 -> {
                holder.databinding.mTopView.visibility = View.GONE
                holder.databinding.mBotView.visibility = View.VISIBLE
            }
            else -> {
                holder.databinding.mTopView.visibility = View.GONE
                holder.databinding.mBotView.visibility = View.GONE
            }
        }
    }

    inner class ViewHolder(val databinding: WordListItemBinding) : RecyclerView.ViewHolder(databinding.root)
}