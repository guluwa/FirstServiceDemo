package cn.guluwa.firstservicedemo.data.remote.gson

import android.util.Log
import cn.guluwa.firstservicedemo.data.bean.ResultBean
import cn.guluwa.firstservicedemo.manage.Contacts

import com.google.gson.Gson
import cn.guluwa.firstservicedemo.data.remote.retrofit.exception.NoDataException
import cn.guluwa.firstservicedemo.data.remote.retrofit.exception.OtherException
import cn.guluwa.firstservicedemo.data.remote.retrofit.exception.TokenException


import java.io.IOException
import java.lang.reflect.Type

import okhttp3.ResponseBody
import retrofit2.Converter

/**
 * Created by 俊康 on 2017/8/8.
 */

class MyGsonResponseBodyConverter<T> internal constructor(private val gson: Gson, private val type: Type) : Converter<ResponseBody, T> {

    /**
     * 针对数据返回成功、错误不同类型字段处理
     */
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): T? {
        val jsonStr = value.string()
        Log.d("yishuju", jsonStr)
        val resultBean = gson.fromJson(jsonStr, ResultBean::class.java)
        try {
            if ("" == jsonStr || resultBean == null) {
                throw NoDataException()
            } else if (resultBean.code == 200) {
                return gson.fromJson<T>(jsonStr, type)
            } else if (resultBean.code == 202) {
                Contacts.TOKEN_NEED_FRESH = true
                throw TokenException()
            } else {
                throw OtherException(resultBean.code, resultBean.message)
            }
        } finally {
            value.close()
        }
    }

    /**
     * 数据解析
     *
     * @param jsonStr JSON字符串
     * @return UniApiResult<GoodsInfoModel> 数据对象
    </GoodsInfoModel> */
    //    public ResultBean<HeadBean> parseJson(String jsonStr) {
    //        Gson gson = new Gson();
    //        Type jsonType = new TypeToken<ResultBean<HeadBean>>() {
    //        }.getType();
    //        return gson.fromJson(jsonStr, jsonType);
    //    }
}