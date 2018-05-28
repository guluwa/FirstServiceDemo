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
 * Created by guluwa on 2018/5/3.
 */
class OneWordViewModel : ViewModel() {

    private var oneWordFresh = MutableLiveData<FreshBean>()

    private var oneWordResult: LiveData<ViewDataBean<ResultBean<String>>>? = null

    fun oneWord(): LiveData<ViewDataBean<ResultBean<String>>>? {
        if (oneWordResult == null) {
            oneWordResult = Transformations.switchMap(oneWordFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().oneWord(it.map)
                } else {
                    null
                }
            }
        }
        return oneWordResult!!
    }

    fun freshOneWord(map: HashMap<String, String>, isFresh: Boolean) {
        oneWordFresh.value = FreshBean(map, isFresh)
    }
}