package cn.guluwa.firstservicedemo.data.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import cn.guluwa.firstservicedemo.data.bean.*
import cn.guluwa.firstservicedemo.data.remote.RemoteDataSource

/**
 * Created by guluwa on 2018/4/26.
 */
class MainViewModel:ViewModel() {

    private var queryWordsFresh = MutableLiveData<FreshBean>()

    private var queryWordsResult: LiveData<ViewDataBean<ResultBean<List<WordBean>>>>? = null

    fun queryWords(): LiveData<ViewDataBean<ResultBean<List<WordBean>>>>? {
        if (queryWordsResult == null) {
            queryWordsResult = Transformations.switchMap(queryWordsFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().queryWords(it.map)
                } else {
                    null
                }
            }
        }
        return queryWordsResult!!
    }

    fun freshQueryWords(map: HashMap<String, String>, isFresh: Boolean) {
        queryWordsFresh.value = FreshBean(map, isFresh)
    }
}