package cn.guluwa.firstservicedemo.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Environment
import android.preference.PreferenceManager
import android.support.design.widget.TabLayout
import android.util.DisplayMetrics
import android.widget.LinearLayout
import android.widget.TextView
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.manage.MyApplication
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Field
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


/**
 * Created by guluwa on 2018/3/15.
 */
object AppUtils {

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    fun getCurrentTime(): Int {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat("HH", Locale.getDefault())
        return Integer.valueOf(simpleDateFormat.format(date))
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    //手机屏幕宽高
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val metric = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(metric)
        return metric
    }

    //设置TabLayout的指示条margin,反射
    fun setIndicator(context: Context, tabs: TabLayout, leftDip: Int, rightDip: Int) {
        try {
            val tabLayout = tabs.javaClass
            val tabStrip: Field?
            tabStrip = tabLayout.getDeclaredField("mTabStrip")
            tabStrip!!.isAccessible = true

            val ll_tab: LinearLayout?
            ll_tab = tabStrip.get(tabs) as LinearLayout

            val left = (getDisplayMetrics(context).density * leftDip).toInt()
            val right = (getDisplayMetrics(context).density * rightDip).toInt()

            for (i in 0 until ll_tab.childCount) {

                val child = ll_tab.getChildAt(i)

                //拿到tabView的mTextView属性
                var mTextViewField: Field?
                mTextViewField = child.javaClass.getDeclaredField("mTextView")
                mTextViewField!!.isAccessible = true
                val mTextView = mTextViewField.get(child) as TextView

                //因为我想要的效果是字多宽线就多宽，所以测量mTextView的宽度
                var width: Int
                width = mTextView.width
                if (width == 0) {
                    mTextView.measure(0, 0)
                    width = mTextView.measuredWidth
                }
                child.setPadding(0, 0, 0, 0)
                val params = child.layoutParams as LinearLayout.LayoutParams
                params.width = width
                params.leftMargin = left
                params.rightMargin = right
                child.layoutParams = params
                child.invalidate()
            }
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }
    }

    /**
     * 检测网络是否连接
     */
    val isNetConnected: Boolean
        get() {
            val cm = MyApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            if (networkInfo != null) {
                return true
            }
            return false
        }

    /**
     * MD5加密
     */
    private fun encryptMD5(password: String): String {
        try {
            val instance: MessageDigest = MessageDigest.getInstance("MD5")//获取md5加密对象
            val digest: ByteArray = instance.digest(password.toByteArray())//对字符串加密，返回字节数组
            val sb = StringBuffer()
            for (b in digest) {
                val i: Int = b.toInt() and 0xff//获取低八位有效值
                var hexString = Integer.toHexString(i)//将整数转化为16进制
                if (hexString.length < 2) {
                    hexString = "0$hexString"//如果是一位的话，补0
                }
                sb.append(hexString)
            }
            return sb.toString()

        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 键值对转字符串
     */
    fun getMapString(paramsMap: Map<String, String>): String {
        val tm = TreeMap(paramsMap)
        val buffer = StringBuffer()
        tm.descendingMap().map {
            buffer.append(it.key).append("=").append(it.value).append("&")
        }
        buffer.append(Contacts.SERRET_VERSEC)
        return AppUtils.encryptMD5(buffer.toString())
    }

    /*
     * 将时间转换为时间戳
     */
    @Throws(ParseException::class)
    private fun dateToStamp(s: String): String {
        val res: String
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = simpleDateFormat.parse(s)
        val ts = date.time / 1000
        res = ts.toString()
        return res
    }

    fun getDayStamp(index: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, index)
        cal.set(Calendar.HOUR_OF_DAY, 12)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        val time = cal.time
        return dateToStamp(SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(time))
    }

    /**
     * 获取sharePreference String类型的值
     */
    fun getString(key: String, defaultValue: String): String {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getContext())
        return settings.getString(key, defaultValue)
    }

    /**
     * 获取sharePreference String类型的值
     */
    fun setString(key: String, value: String) {
        val settings = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getContext())
        settings.edit().putString(key, value).apply()
    }

    // 将时间戳转为字符串
    fun getStrTime(stampTime: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return sdf.format(Date(stampTime.toLong()))
    }

    //将ascii码转字符
    fun asciiToString(value: String): String {
        val sbu = StringBuffer()
        val chars = value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (i in chars.indices) {
            sbu.append(Integer.parseInt(chars[i]).toChar())
        }
        return sbu.toString()
    }

    /**
     * 验证Email
     *
     * @param email email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */
    fun checkEmail(email: String): Boolean {
        val regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?"
        return Pattern.matches(regex, email)
    }

    fun parseParam(param: String): MutableMap<String, String>? {
        val params = param.substring(1, param.length - 1).split(",")
        val map = HashMap<String, String>()
        var sign = ""
        for (p in params) {
            if (p.substring(1, p.indexOf(":") - 1) != "sign") {
                map[p.substring(1, p.indexOf(":") - 1)] = p.substring(p.indexOf(":") + 2, p.length - 1)
            } else {
                sign = p.substring(p.indexOf(":") + 2, p.length - 1)
            }
        }
        return if (getMapString(map) != sign) null else map
    }

     fun saveBitmap(bitmap: Bitmap, fileName: String): File {
        val file = File(
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED, ignoreCase = true))
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + "/Guluji" else "")
        if (!file.exists()) {
            file.mkdirs()
        }
        val realFile = File(file, fileName)
        if (!realFile.exists()) {
            realFile.createNewFile()
        }
        val fos = FileOutputStream(realFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        if (!bitmap.isRecycled) {
            bitmap.recycle()
            System.gc() // 通知系统回收
        }
        return realFile
    }
}