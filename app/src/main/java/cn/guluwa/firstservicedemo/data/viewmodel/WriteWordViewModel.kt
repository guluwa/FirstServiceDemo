package cn.guluwa.firstservicedemo.data.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import cn.guluwa.firstservicedemo.data.bean.FreshBean
import cn.guluwa.firstservicedemo.data.bean.ResultBean
import cn.guluwa.firstservicedemo.data.bean.TokenBean
import cn.guluwa.firstservicedemo.data.bean.ViewDataBean
import cn.guluwa.firstservicedemo.data.remote.RemoteDataSource

/**
 * Created by guluwa on 2018/4/27.
 */
class WriteWordViewModel : ViewModel() {

    private var writeWordsFresh = MutableLiveData<FreshBean>()

    private var writeWordsResult: LiveData<ViewDataBean<ResultBean<Any>>>? = null

    fun writeWords(): LiveData<ViewDataBean<ResultBean<Any>>>? {
        if (writeWordsResult == null) {
            writeWordsResult = Transformations.switchMap(writeWordsFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().writeWords(it.map)
                } else {
                    null
                }
            }
        }
        return writeWordsResult!!
    }

    fun freshWriteWords(map: HashMap<String, String>, isFresh: Boolean) {
        writeWordsFresh.value = FreshBean(map, isFresh)
    }
}