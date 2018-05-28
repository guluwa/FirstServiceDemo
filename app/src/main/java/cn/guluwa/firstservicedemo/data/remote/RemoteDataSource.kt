package cn.guluwa.firstservicedemo.data.remote

import android.arch.lifecycle.LiveData
import cn.guluwa.firstservicedemo.data.bean.ResultBean
import cn.guluwa.firstservicedemo.data.bean.TokenBean
import cn.guluwa.firstservicedemo.data.bean.ViewDataBean
import cn.guluwa.firstservicedemo.data.bean.WordBean
import cn.guluwa.firstservicedemo.data.remote.retrofit.RetrofitWorker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by guluwa on 2018/1/12.
 */

class RemoteDataSource {

    object SingletonHolder {
        //单例（静态内部类）
        val instance = RemoteDataSource()
    }

    companion object {

        fun getInstance() = SingletonHolder.instance
    }

    /**
     * register
     */
    fun register(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<TokenBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.register(mapOf(Pair("param", map["param"]!!)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
    }

    /**
     * login
     */
    fun login(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<TokenBean>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.login(mapOf(Pair("param", map["param"]!!)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
    }

    /**
     * queryWords
     */
    fun queryWords(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<List<WordBean>>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.queryWords(mapOf(Pair("param", map["param"]!!)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .map {
                            for (word in it.data!!) {
                                val dis = System.currentTimeMillis() - word.createTime
                                if (dis < 60 * 1000) {
                                    word.createTimeStr = "片刻之前"
                                } else if (dis < 60 * 60 * 1000) {
                                    word.createTimeStr = "${dis / (60 * 1000)}分钟之前"
                                } else if (dis < 24 * 60 * 60 * 1000) {
                                    word.createTimeStr = "${dis / (60 * 60 * 1000)}小时之前"
                                } else {
                                    word.createTimeStr = "${dis / (24 * 60 * 60 * 1000)}天之前"
                                }
                            }
                            it
                        }
                        .observeOn(AndroidSchedulers.mainThread()))
    }

    /**
     * writeWords
     */
    fun writeWords(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<Any>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.writeWords(mapOf(Pair("param", map["param"]!!)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
    }

    /**
     * uploadThumb
     */
    fun uploadThumb(thumb: String): LiveData<ViewDataBean<ResultBean<String>>> {
        val file = File(thumb)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        val body = MultipartBody.Part.createFormData("thumb", file.name, requestFile)
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.uploadThumb(body)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
    }

    /**
     * one word
     */
    fun oneWord(map: Map<String, String>): LiveData<ViewDataBean<ResultBean<String>>> {
        return LiveDataObservableAdapter.fromObservableViewData(
                RetrofitWorker.retrofitWorker.oneWord(mapOf(Pair("param", map["param"]!!)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()))
    }
}