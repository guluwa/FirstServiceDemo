package cn.guluwa.firstservicedemo.data.remote.retrofit.exception

import cn.guluwa.firstservicedemo.data.remote.retrofit.exception.BaseException

/**
 * Created by 俊康 on 2017/8/8.
 */

class NoNetworkException(msg: String) : BaseException() {

    init {
        this.msg = msg
    }

    companion object {

        private val serialVersionUID = -347636838706746243L
    }
}
