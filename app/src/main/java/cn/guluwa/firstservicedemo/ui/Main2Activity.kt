package cn.guluwa.firstservicedemo.ui

import cn.guluwa.firstservicedemo.R
import cn.guluwa.firstservicedemo.base.BaseActivity
import cn.guluwa.firstservicedemo.utils.UkUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup

class Main2Activity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_main2

    override fun initViews() {
        Observable.just("translate.google.cn")
                .map {
                    val document = Jsoup.parse(it)
                    val res = Jsoup.connect("https://translate.google.cn").header("Accept", "*/*")
                            .header("Accept-Encoding", "gzip, deflate")
                            .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                            .header("Content-Type", "application/json;charset=UTF-8")
                            .header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0")
                            .timeout(10000).ignoreContentType(true).execute()
                    val body = res.body()
                    val str = body.substring(body.indexOf("TKK"), body.indexOf("TKK") + 99)
                    val list = str.split(";")
                    val a = list[0].substring(list[0].indexOf("a\\x3d") + 5)
                    val b = list[1].substring(list[1].indexOf("b\\x3d") + 5)
                    val c = list[2].substring(7, list[2].indexOf("+"))
                    val d = String.format("%s.%s", c, a.toLong() + b.toLong())
                    UkUtils.tk("本项目 多源翻译 ，提供了集多种主流的 在线翻译 及 TTS 功能于一身的轻量级服务。通过程序向所支持的在线目标服务器发送 HTTP 请求，获取并解析返回的结果，为使用者提供便利。目前，本项目免费开源，开发者可基于此进行二次开发。", d)
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    println(it)
                }, {
                    println(it.message)
                })

    }
}