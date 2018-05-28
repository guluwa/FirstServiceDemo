package cn.guluwa.firstservicedemo.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.text.TextUtils
import cn.guluwa.firstservicedemo.R
import cn.guluwa.firstservicedemo.base.BaseActivity
import cn.guluwa.firstservicedemo.data.viewmodel.LoginViewModel
import cn.guluwa.firstservicedemo.databinding.ActivityLoginBinding
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.utils.AppUtils
import com.google.gson.Gson
import com.megglife.chaoren.data.bean.PageStatus
import kotlinx.android.synthetic.main.activity_login.*
import java.util.*

class LoginActivity : BaseActivity() {

    private var loginBinding: ActivityLoginBinding? = null

    private var mViewModel: LoginViewModel? = null

    override val viewLayoutId: Int get() = R.layout.activity_login

    override fun initViews() {
        loginBinding = mViewDataBinding as ActivityLoginBinding
        loginBinding!!.setClickListener { view ->
            when (view.id) {
                R.id.tvSignIn -> signIn()
                R.id.tvSignUp -> startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    //登录
    private fun signIn() {
        if (TextUtils.isEmpty(etUserName.text)) {
            showToastMsg("用户名格式不正确")
            return
        }
        if (TextUtils.isEmpty(etPassWord.text)) {
            showToastMsg("密码格式不正确")
            return
        }
        val map = HashMap<String, String>()
        map["username"] = etUserName.text.toString().trim()
        map["password"] = etPassWord.text.toString().trim()
        map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        map["sign"] = AppUtils.getMapString(map)
        map["param"] = Gson().toJson(TreeMap(map).descendingMap())
        mViewModel!!.freshLogin(map, true)
    }

    override fun initViewModel() {
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        }
        if (!mViewModel!!.login()!!.hasObservers()) {
            mViewModel!!.login()!!.observe(this, Observer {
                if (it == null) {
                    showToastMsg("数据出错啦")
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog("正在登录")
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshLogin(hashMapOf(), false)
                        showToastMsg("登录失败," + it.error!!.message)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshLogin(hashMapOf(), false)
                        showToastMsg("数据出错啦")
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshLogin(hashMapOf(), false)
                        if (it.data!!.code == 200) {
                            showToastMsg("登录成功")
                            AppUtils.setString(Contacts.TOKEN, it.data.data!!.token)
                            AppUtils.setString("id", it.data.data!!.id.toString())
                            AppUtils.setString("username", it.data.data!!.username)
                            startActivity(Intent(this,MainActivity::class.java))
                            finish()
                        } else
                            showToastMsg("登录失败")
                    }
                }
            })
        }
    }
}