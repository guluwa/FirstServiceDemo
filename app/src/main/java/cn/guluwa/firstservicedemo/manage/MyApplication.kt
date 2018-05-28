package cn.guluwa.firstservicedemo.manage

import android.content.Context
import android.support.multidex.MultiDexApplication
import cn.guluwa.firstservicedemo.utils.BoxingGlideLoader
import com.bilibili.boxing.BoxingMediaLoader

/**
 * Created by guluwa on 2018/1/9.
 */

class MyApplication : MultiDexApplication() {

    companion object {

        private lateinit var mContext: Context

        fun getContext() = mContext
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        initBoxing()
    }

    private fun initBoxing() {
        val loader = BoxingGlideLoader()
        BoxingMediaLoader.getInstance().init(loader)
    }
}
