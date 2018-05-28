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
 * Created by guluwa on 2018/4/25.
 */

class RegisterViewModel : ViewModel() {

    private var registerFresh = MutableLiveData<FreshBean>()

    private var registerResult: LiveData<ViewDataBean<ResultBean<TokenBean>>>? = null

    fun register(): LiveData<ViewDataBean<ResultBean<TokenBean>>>? {
        if (registerResult == null) {
            registerResult = Transformations.switchMap(registerFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().register(it.map)
                } else {
                    null
                }
            }
        }
        return registerResult!!
    }

    fun freshRegister(map: HashMap<String, String>, isFresh: Boolean) {
        registerFresh.value = FreshBean(map, isFresh)
    }

    private var uploadThumbFresh = MutableLiveData<FreshBean>()

    private var uploadThumbResult: LiveData<ViewDataBean<ResultBean<String>>>? = null

    fun uploadThumb(): LiveData<ViewDataBean<ResultBean<String>>>? {
        if (uploadThumbResult == null) {
            uploadThumbResult = Transformations.switchMap(uploadThumbFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().uploadThumb(it.map["thumb"]!!)
                } else {
                    null
                }
            }
        }
        return uploadThumbResult!!
    }

    fun freshUploadThumb(map: HashMap<String, String>, isFresh: Boolean) {
        uploadThumbFresh.value = FreshBean(map, isFresh)
    }
}