package cn.guluwa.firstservicedemo.data.remote.retrofit

import cn.guluwa.firstservicedemo.data.bean.ResultBean
import cn.guluwa.firstservicedemo.data.bean.TokenBean
import cn.guluwa.firstservicedemo.data.bean.WordBean
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * Created by guluwa on 2018/1/11.
 */

interface ApiService {

    /**
     * register
     */
    @POST("user/register")
    @FormUrlEncoded
    fun register(@FieldMap map: Map<String, String>): Observable<ResultBean<TokenBean>>

    /**
     * login
     */
    @POST("user/login")
    @FormUrlEncoded
    fun login(@FieldMap map: Map<String, String>): Observable<ResultBean<TokenBean>>

    /**
     * freshToken
     */
    @POST("user/freshToken")
    @FormUrlEncoded
    fun freshToken(@FieldMap map: Map<String, String>): Observable<ResultBean<TokenBean>>

    /**
     * queryWords
     */
    @POST("word/queryWords")
    @FormUrlEncoded
    fun queryWords(@FieldMap map: Map<String, String>): Observable<ResultBean<List<WordBean>>>

    /**
     * writeWords
     */
    @POST("word/writeWords")
    @FormUrlEncoded
    fun writeWords(@FieldMap map: Map<String, String>): Observable<ResultBean<Any>>

    /**
     * uploadThumb
     */
    @POST("user/uploadThumb")
    @Multipart
    fun uploadThumb(@Part file: MultipartBody.Part): Observable<ResultBean<String>>

    /**
     * one word
     */
    @POST("word/oneWord")
    @FormUrlEncoded
    fun oneWord(@FieldMap map: Map<String, String>): Observable<ResultBean<String>>
}
