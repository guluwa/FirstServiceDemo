package cn.guluwa.firstservicedemo.base

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import cn.guluwa.firstservicedemo.R
import com.hwangjr.rxbus.RxBus
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.manage.FinishActivityManager
import cn.guluwa.firstservicedemo.utils.ToastUtil
import java.util.*

/**
 * Created by guluwa on 2018/3/14.
 */

abstract class BaseActivity : AppCompatActivity(), IBaseView {

    /**
     * 绑定布局文件
     */
    abstract val viewLayoutId: Int

    /**
     * ViewDataBinding对象
     */
    lateinit var mViewDataBinding: ViewDataBinding

    /**
     * 初始化视图控件
     */
    protected abstract fun initViews()

    /**
     * activity管理类
     */
    protected val finishActivityManager = FinishActivityManager.getInstance()

    /**
     * 需要进行检测的权限数组
     */
    private var needPermissions = arrayOf(
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE)

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private var isNeedCheck = true


    /**
     * 进度对话框
     */
    private var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDataBinding = DataBindingUtil.setContentView(this, viewLayoutId)
        //RxBus注册
        RxBus.get().register(this)
        finishActivityManager.addActivity(this)
        initViews()
        initViewModel()
    }

    /**
     * 显示提示信息
     */
    private fun showMissingPermissionDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.notifyTitle)
        builder.setMessage(R.string.notifyMsg)
        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel, { _, _ -> finish() })
        builder.setPositiveButton(R.string.setting, { _, _ -> startAppSettings() })
        builder.setCancelable(false)
        builder.show()
    }

    /**
     * 启动应用的设置
     */
    private fun startAppSettings() {
        val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if (isNeedCheck) {
            checkPermissions(needPermissions)
        }
    }

    override fun onDestroy() {
        //结束Activity&从栈中移除该Activity
        finishActivityManager.finishActivity(this)
        RxBus.get().unregister(this)
        super.onDestroy()
    }

    /**
     * 权限检查是否全部申请
     */
    private fun checkPermissions(permissions: Array<String>) {
        val needRequestPermissionList = findDeniedPermissions(permissions)
        if (needRequestPermissionList.isNotEmpty()) {
            ActivityCompat.requestPermissions(this,
                    needRequestPermissionList.toTypedArray(),
                    Contacts.PERMISSION_REQUEST_CODE)
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     */
    private fun findDeniedPermissions(permissions: Array<String>): List<String> {
        val needRequestPermissionList = ArrayList<String>()
        for (perm in permissions) {
            if (ContextCompat.checkSelfPermission(this,
                            perm) != PackageManager.PERMISSION_GRANTED || ActivityCompat.shouldShowRequestPermissionRationale(
                            this, perm)) {
                needRequestPermissionList.add(perm)
            }
        }
        return needRequestPermissionList
    }

    /**
     * 权限申请情况
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, paramArrayOfInt: IntArray) {
        if (requestCode == Contacts.PERMISSION_REQUEST_CODE) {
            if (!verifyPermissions(paramArrayOfInt)) {
                showMissingPermissionDialog()
                isNeedCheck = false
            }
        }
    }

    /**
     * 检测是否所有的权限都已经授权
     */
    private fun verifyPermissions(grantResults: IntArray): Boolean {
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    /**
     * 点击空白位置 隐藏软键盘
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (null != this.currentFocus) {
            val mInputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            return mInputMethodManager.hideSoftInputFromWindow(this.currentFocus!!.windowToken, 0)
        }
        return super.onTouchEvent(event)
    }

    /**
     * 弹出Toast
     */
    override fun showToastMsg(msg: String) {
        ToastUtil.getInstance().showToast(msg)
    }

    override fun showProgressDialog(msg: String) {
        if (mProgressDialog == null) {
            mProgressDialog = ProgressDialog(this)
            mProgressDialog!!.setCancelable(false)
            mProgressDialog!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        }
        mProgressDialog!!.setMessage(msg)
        mProgressDialog!!.show()
    }

    override fun dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }

    open fun initViewModel() {

    }
}