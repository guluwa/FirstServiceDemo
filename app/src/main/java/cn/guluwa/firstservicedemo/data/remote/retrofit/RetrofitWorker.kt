package cn.guluwa.firstservicedemo.data.remote.retrofit


import android.support.annotation.NonNull
import android.util.Log
import cn.guluwa.firstservicedemo.data.remote.ProxyHandler
import cn.guluwa.firstservicedemo.data.remote.gson.MyGsonConverterFactory

import cn.guluwa.firstservicedemo.data.remote.retrofit.exception.NoNetworkException
import cn.guluwa.firstservicedemo.data.remote.retrofit.exception.ServiceException
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.manage.MyApplication
import cn.guluwa.firstservicedemo.utils.AppUtils

import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.*
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.reflect.Proxy
import java.net.URLDecoder
import java.nio.file.attribute.AclEntry.newBuilder
import java.util.concurrent.TimeUnit

object RetrofitWorker {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(Contacts.BASEURL)
                .client(OkHttpClient.Builder()
                        .addInterceptor(ChuckInterceptor(MyApplication.getContext()))//打印
                        .addInterceptor(sLoggingInterceptor)
                        .addInterceptor { chain ->
                            val connected = AppUtils.isNetConnected
                            if (connected) {
                                chain.proceed(chain.request())
                            } else {
                                throw NoNetworkException("没有网络哦~~~")
                            }
                        }
                        .addInterceptor { chain ->
                            val proceed = chain.proceed(chain.request())
                            if (proceed.code() == 404) {
                                throw ServiceException("服务器好像出了点小问题~~~")
                            } else {
                                proceed
                            }
                        }
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(20, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true).build())
                .addConverterFactory(MyGsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    val retrofitWorker: ApiService by lazy {
        getProxy(ApiService::class.java) as ApiService
    }

    /**
     * 打印返回的json数据拦截器
     */
    private val sLoggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        val requestBuffer = Buffer()
        if (request.body() != null) {
            request.body()!!.writeTo(requestBuffer)
        } else {
            Log.d("LogTAG", "request.body() == null")
        }
        //打印url信息
        Log.w("LogTAG", request.url().toString() + if (request.body() != null) "?" + parseParams(request.body(), requestBuffer) else "")
        chain.proceed(request)
    }

    @Throws(UnsupportedEncodingException::class)
    private fun parseParams(body: RequestBody?, requestBuffer: Buffer): String {
        return if (body!!.contentType() != null && !body.contentType()!!.toString().contains("multipart")) {
            URLDecoder.decode(requestBuffer.readUtf8(), "UTF-8")
        } else "null"
    }

    private fun <T> getProxy(tClass: Class<T>): Any {
        val t = retrofit.create(tClass)
        return Proxy.newProxyInstance(tClass.classLoader, arrayOf<Class<*>>(tClass), ProxyHandler(t as Any))
    }
}
