package cn.guluwa.firstservicedemo.data.remote.retrofit.exception

import cn.guluwa.firstservicedemo.data.remote.retrofit.exception.BaseException

/**
 * Created by Administrator on 2017/12/11.
 */

class OtherException(code: Int, msg: String) : BaseException() {

    init {
        this.code = code
        this.msg = msg
    }
}
