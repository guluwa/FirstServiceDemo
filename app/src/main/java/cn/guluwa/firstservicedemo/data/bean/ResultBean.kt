package cn.guluwa.firstservicedemo.data.bean

import com.google.gson.annotations.SerializedName

/**
 * Created by guluwa on 2018/3/1.
 */

class ResultBean<T> : BaseBean() {
    var data: T? = null
}