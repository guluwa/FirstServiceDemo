package cn.guluwa.firstservicedemo.data.remote

import android.arch.lifecycle.LiveData
import android.util.Log
import cn.guluwa.firstservicedemo.data.bean.ErrorBean
import cn.guluwa.firstservicedemo.data.bean.ViewDataBean
import cn.guluwa.firstservicedemo.data.remote.retrofit.exception.*
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import java.lang.ref.WeakReference
import java.net.SocketTimeoutException
import java.net.UnknownHostException


/**
 * Created by guluwa on 2018/1/4.
 */

class ObservableViewLiveData<T>(private val mObservable: Observable<T>) : LiveData<ViewDataBean<T>>() {

    private var mDisposableRef: WeakReference<Disposable>? = null
    private val mLock = Any()

    override fun onActive() {
        super.onActive()
        mObservable
                .subscribe(object : Observer<T> {
                    override fun onSubscribe(d: Disposable) {
                        synchronized(mLock) {
                            mDisposableRef = WeakReference(d)
                        }
                        postValue(ViewDataBean.loading())
                    }

                    override fun onNext(t: T) {
                        if (t == null) {
                            postValue(ViewDataBean.empty())
                        } else {
                            postValue(ViewDataBean.content(t))
                        }
                    }

                    override fun onError(e: Throwable) {
                        println(e.message)
                        synchronized(mLock) {
                            mDisposableRef = null
                        }
                        postValue(ViewDataBean.error(handleException(e)))
                    }

                    override fun onComplete() {
                        synchronized(mLock) {
                            mDisposableRef = null
                        }
                    }
                })
    }

    override fun onInactive() {
        super.onInactive()

        synchronized(mLock) {
            val disposableWeakReference = mDisposableRef
            if (disposableWeakReference != null) {
                val disposable = disposableWeakReference.get()
                disposable?.dispose()
                mDisposableRef = null
            }
        }
    }

    private fun handleException(t: Throwable): ErrorBean {
        Log.e("yjk", t.toString())
        return if (t is NoNetworkException) {
            ErrorBean("没有网络", 1)
        } else if (t is NoDataException) {
            ErrorBean("没有数据", 2)
        } else if (t is HttpException) {
            ErrorBean("网络请求错误", 3)
        } else if (t is OtherException) {
            ErrorBean(t.msg, 4)
        } else if (t is SocketTimeoutException || t is UnknownHostException) {
            ErrorBean("请求超时", 5)
        } else if (t is TokenException) {
            ErrorBean("用户信息过期，请重试", 7)
        } else {
            if (t is BaseException)
                ErrorBean("其他错误," + t.msg, 6)
            else
                ErrorBean("其他错误," + t.message, 6)
        }
    }
}
