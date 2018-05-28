package cn.guluwa.firstservicedemo.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout

import cn.guluwa.firstservicedemo.R

/**
 * Created by 俊康 on 2017/11/30.
 */

class GeneratePictureView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    private var mWebView: WebView? = null
    private var isFirstLoad = false
    private var strData: String? = null

    val screen: Bitmap?
        get() {
            var bmp = Bitmap.createBitmap(mWebView!!.width, 1, Bitmap.Config.ARGB_8888)
            val rowBytes = bmp.rowBytes

            if (rowBytes * mWebView!!.height >= availMemory) {
                return null
            }
            bmp = Bitmap.createBitmap(mWebView!!.width, mWebView!!.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            mWebView!!.draw(canvas)
            return bmp
        }

    private// 获取android当前可用内存大小
    val availMemory: Long
        get() = Runtime.getRuntime().maxMemory()

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        initWebView(context)
    }

    private fun initWebView(context: Context) {
        if (Build.VERSION.SDK_INT >= 21) {
            WebView.enableSlowWholeDocumentDraw()
        }
        LayoutInflater.from(context).inflate(R.layout.generate_picture_view, this)
        mWebView = findViewById(R.id.mWebView)

        mWebView!!.settings.javaScriptEnabled = true
        mWebView!!.isVerticalScrollBarEnabled = false
        mWebView!!.isHorizontalScrollBarEnabled = false
        // 屏幕自适应网页
        mWebView!!.settings.defaultZoom = WebSettings.ZoomDensity.FAR
        mWebView!!.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)
    }

    fun initData(data: String) {
        this.strData = data
        if (Build.VERSION.SDK_INT >= 21) {
            isFirstLoad = true
            mWebView!!.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    if (newProgress == 100) {
                        if (isFirstLoad) {
                            isFirstLoad = false
                            Log.e("TAG", "onProgressChanged")
                            changeDay()
                        }
                    }
                }
            }
            mWebView!!.loadUrl("file:///android_asset/generate_pic.html")
        } else {
            isFirstLoad = true
            mWebView!!.visibility = View.INVISIBLE

            mWebView!!.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    if (newProgress == 100) {
                        changeDay()
                        if (!isFirstLoad) {
                            mWebView!!.visibility = View.VISIBLE
                        }
                    }
                }
            }
            mWebView!!.loadUrl("file:///android_asset/generate_pic.html")
        }
    }

    fun changeDay() {
        strData = "<p style=\"color: gray;font-size: small;text-align: center;letter-spacing: 0.5px;\">「 一言 」</p> <br />\n$strData <br /><br />\n"
        mWebView!!.loadUrl("javascript:changeContent(\"" + strData!!.replace("\n", "\\n").replace("\"", "\\\"").replace("'", "\\'") + "\")")
        mWebView!!.setBackgroundColor(Color.WHITE)
        if (Build.VERSION.SDK_INT < 21) {
            if (isFirstLoad) {
                mWebView!!.postDelayed({
                    isFirstLoad = false
                    //为解决部分手机打开不显示图片问题
                    mWebView!!.reload()
                }, 500)
            }
        }
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mWebView!!.visibility = View.GONE
        this.removeView(mWebView)
        mWebView!!.destroy()
        mWebView = null
    }
}
