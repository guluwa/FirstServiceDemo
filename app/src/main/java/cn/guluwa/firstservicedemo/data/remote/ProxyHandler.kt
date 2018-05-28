package cn.guluwa.firstservicedemo.data.remote

import cn.guluwa.firstservicedemo.data.remote.retrofit.RetrofitWorker
import cn.guluwa.firstservicedemo.data.remote.retrofit.exception.TokenException
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.utils.AppUtils
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.internal.operators.single.SingleMap
import retrofit2.http.FieldMap
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.util.*
import kotlin.collections.HashMap

/**
 * Created by guluwa on 2018/4/14.
 */

class ProxyHandler(private val proxyObject: Any) : InvocationHandler {

    private var mIsTokenNeedRefresh = false

    private var tokenFreshing = false

    override fun invoke(proxy: Any, method: Method, args: Array<Any>): Any {
        return Observable.just<Any>(true)
                .flatMap({
                    if (Contacts.TOKEN_NEED_FRESH && !tokenFreshing) {
                        tokenFreshing = true
                        Observable.error(TokenException())
                    } else {
                        if (mIsTokenNeedRefresh) {
                            args[0] = updateMethodToken(method, args[0] as Map<String, String>)!!
                        }
                        method.invoke(proxyObject, args[0]) as Observable<Any>?
                    }
                }).retryWhen({ observable ->
                    observable.flatMap({ throwable ->
                        if (throwable is TokenException) {
                            val map = HashMap<String, String>()
                            map[Contacts.TOKEN] = AppUtils.getString(Contacts.TOKEN, "")
                            map["id"] = AppUtils.getString("id", "")
                            map["username"] = AppUtils.getString("username", "")
                            map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
                            map["sign"] = AppUtils.getMapString(map)
                            map["param"] = Gson().toJson(TreeMap(map).descendingMap())
                            RetrofitWorker.retrofitWorker.freshToken(map)
                                    .doOnNext({
                                        AppUtils.setString(Contacts.TOKEN, it.data!!.token)
                                        mIsTokenNeedRefresh = true
                                        Contacts.TOKEN_NEED_FRESH = false
                                        tokenFreshing = false
                                    })
                        } else
                            Observable.error(throwable)
                    })
                })
    }

    /**
     * Update the token of the args in the method.
     *
     * PS： 因为这里使用的是 GET 请求，所以这里就需要对 Query 的参数名称为 token 的方法。
     * 若是 POST 请求，或者使用 Body ，自行替换。因为 参数数组已经知道，进行遍历找到相应的值，进行替换即可（更新为新的 token 值）。
     */
    private fun updateMethodToken(method: Method, args: Map<String, String>): Map<String, String>? {
        if (mIsTokenNeedRefresh) {
            val annotationsArray = method.parameterAnnotations
            var annotations: Array<Annotation>
            if (annotationsArray != null && annotationsArray.isNotEmpty()) {
                for (i in annotationsArray.indices) {
                    annotations = annotationsArray[i]
                    for (annotation in annotations) {
                        if (annotation is FieldMap) {
                            val map = AppUtils.parseParam(args["param"] as String)
                            map!!.remove(Contacts.TOKEN)
                            map.remove("sign")
                            map.remove("param")
                            map[Contacts.TOKEN] = AppUtils.getString(Contacts.TOKEN, "")
                            map["sign"] = AppUtils.getMapString(map)
                            map["param"] = Gson().toJson(TreeMap(map).descendingMap())
                            return mapOf(Pair("param", map["param"]!!))
                        }
                    }
                }
            }
            mIsTokenNeedRefresh = false
        }
        return null
    }
}