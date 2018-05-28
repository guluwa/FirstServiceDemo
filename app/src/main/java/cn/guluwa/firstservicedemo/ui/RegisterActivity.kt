package cn.guluwa.firstservicedemo.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat.startActivity
import android.text.TextUtils
import android.view.View
import cn.guluwa.firstservicedemo.R
import cn.guluwa.firstservicedemo.R.id.*
import cn.guluwa.firstservicedemo.base.BaseActivity
import cn.guluwa.firstservicedemo.data.viewmodel.RegisterViewModel
import cn.guluwa.firstservicedemo.databinding.ActivityRegisterBinding
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.utils.AppUtils
import com.bilibili.boxing.Boxing
import com.bilibili.boxing.model.config.BoxingConfig
import com.bilibili.boxing.model.entity.impl.ImageMedia
import com.bilibili.boxing.utils.ImageCompressor
import com.bilibili.boxing_impl.ui.BoxingActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import com.megglife.chaoren.data.bean.PageStatus
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : BaseActivity() {

    private var registerBinding: ActivityRegisterBinding? = null

    private var mViewModel: RegisterViewModel? = null

    override val viewLayoutId: Int get() = R.layout.activity_register

    private val REQUEST_CODE = 101

    private var mThumbPath = ""

    override fun initViews() {
        registerBinding = mViewDataBinding as ActivityRegisterBinding
        Glide.with(this)
                .load(R.mipmap.ic_launcher_round)
                .apply(RequestOptions.circleCropTransform())
                .into(ivUserImage)
        registerBinding!!.setClickListener { view ->
            when (view.id) {
                R.id.ivBack -> finish()
                R.id.tvSignUp -> signUp()
                R.id.ivUserImage -> {
                    val videoConfig = BoxingConfig(BoxingConfig.Mode.SINGLE_IMG).needCamera(R.drawable.camera_white_icon)
                    Boxing.of(videoConfig).withIntent(this, BoxingActivity::class.java).start(this, REQUEST_CODE)
                }
            }
        }
    }

    //注册
    private fun signUp() {
        if (mThumbPath == "") {
            showToastMsg("请选择用户头像")
            return
        }
        if (TextUtils.isEmpty(etUserName.text)) {
            showToastMsg("用户名格式不正确")
            return
        }
        if (TextUtils.isEmpty(etPassWord.text)) {
            showToastMsg("密码格式不正确")
            return
        }
        if (TextUtils.isEmpty(etEmail.text) || !AppUtils.checkEmail(etEmail.text.toString().trim())) {
            showToastMsg("邮箱格式不正确")
            return
        }
        val map = HashMap<String, String>()
        map["username"] = etUserName.text.toString().trim()
        map["password"] = etPassWord.text.toString().trim()
        map["email"] = etEmail.text.toString().trim()
        map["thumb"] = mThumbPath
        map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        map["sign"] = AppUtils.getMapString(map)
        map["param"] = Gson().toJson(TreeMap(map).descendingMap())
        mViewModel!!.freshRegister(map, true)
    }

    override fun initViewModel() {
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        }
        if (!mViewModel!!.register()!!.hasObservers()) {
            mViewModel!!.register()!!.observe(this, Observer {
                if (it == null) {
                    showToastMsg("数据出错啦")
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog("正在注册")
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshRegister(hashMapOf(), false)
                        showToastMsg("注册失败," + it.error!!.message)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshRegister(hashMapOf(), false)
                        showToastMsg("数据出错啦")
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshRegister(hashMapOf(), false)
                        if (it.data!!.code == 200) {
                            showToastMsg("注册成功")
                            AppUtils.setString(Contacts.TOKEN, it.data.data!!.token)
                            AppUtils.setString("id", it.data.data!!.id.toString())
                            AppUtils.setString("username", it.data.data!!.username)
                            startActivity(Intent(this, MainActivity::class.java))
                            finishActivityManager.finishActivity(LoginActivity::class.java)
                            finish()
                        } else
                            showToastMsg("注册失败")
                    }
                }
            })
            mViewModel!!.uploadThumb()!!.observe(this, Observer {
                if (it == null) {
                    showToastMsg("数据出错啦")
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog("正在上传")
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadThumb(hashMapOf(), false)
                        showToastMsg("上传失败," + it.error!!.message)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadThumb(hashMapOf(), false)
                        showToastMsg("数据出错啦")
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshUploadThumb(hashMapOf(), false)
                        if (it.data!!.code == 200) {
                            showToastMsg("上传成功")
                            mThumbPath = it.data.data!!
                            Glide.with(this)
                                    .load(Contacts.BASEURL + mThumbPath)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(ivUserImage)
                        } else
                            showToastMsg("上传失败")
                    }
                }
            })
        }
    }

    //头像选择回调
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 0) {
            return
        }
        if (data == null) {
            return
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                val medias = Boxing.getResult(data)
                if (medias != null) {
                    (medias[0] as ImageMedia).compress(ImageCompressor(this))
                    val map = HashMap<String, String>()
                    map["thumb"] = (medias[0] as ImageMedia).compressPath
                    mViewModel!!.freshUploadThumb(map, true)
                }
            }
        }
    }

}