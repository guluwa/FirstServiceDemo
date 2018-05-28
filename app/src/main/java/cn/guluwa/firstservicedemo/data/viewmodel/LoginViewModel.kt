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
 * Created by guluwa on 2018/4/26.
 */

class LoginViewModel : ViewModel() {

    private var loginFresh = MutableLiveData<FreshBean>()

    private var loginResult: LiveData<ViewDataBean<ResultBean<TokenBean>>>? = null

    fun login(): LiveData<ViewDataBean<ResultBean<TokenBean>>>? {
        if (loginResult == null) {
            loginResult = Transformations.switchMap(loginFresh) {
                if (it.isFresh) {
                    RemoteDataSource.getInstance().login(it.map)
                } else {
                    null
                }
            }
        }
        return loginResult!!
    }

    fun freshLogin(map: HashMap<String, String>, isFresh: Boolean) {
        loginFresh.value = FreshBean(map, isFresh)
    }
}