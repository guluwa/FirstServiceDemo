package cn.guluwa.firstservicedemo.ui

import android.content.Intent
import cn.guluwa.firstservicedemo.R
import cn.guluwa.firstservicedemo.base.BaseActivity
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.utils.AppUtils

class SplashActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_splash

    override fun initViews() {
        if (AppUtils.getString(Contacts.TOKEN, "") == "") {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish()
    }
}