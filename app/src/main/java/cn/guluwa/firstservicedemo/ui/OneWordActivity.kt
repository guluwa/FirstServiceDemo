package cn.guluwa.firstservicedemo.ui

import android.app.PendingIntent.getActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import cn.guluwa.firstservicedemo.R
import cn.guluwa.firstservicedemo.R.id.mPictureView
import cn.guluwa.firstservicedemo.R.id.mToolBar
import cn.guluwa.firstservicedemo.base.BaseActivity
import cn.guluwa.firstservicedemo.data.viewmodel.OneWordViewModel
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.utils.AppUtils
import com.google.gson.Gson
import com.megglife.chaoren.data.bean.PageStatus
import kotlinx.android.synthetic.main.activity_one_word.*
import java.util.*

class OneWordActivity : BaseActivity() {

    override val viewLayoutId: Int get() = R.layout.activity_one_word

    private var mViewModel: OneWordViewModel? = null

    override fun initViews() {
        initToolBar()
    }

    private fun initToolBar() {
        mToolBar.title = "一言"
        setSupportActionBar(mToolBar)
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * 菜单
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.one_word_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_share -> shareOneWord()
            R.id.action_save -> println("save")
        }
        return super.onOptionsItemSelected(item)
    }

    private fun shareOneWord() {
        var intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(AppUtils.saveBitmap(mPictureView.screen!!, String.format("%s.jpg", System.currentTimeMillis()))))
        intent = Intent.createChooser(intent, "分享一言")
        startActivity(intent)
    }

    override fun initViewModel() {
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(this).get(OneWordViewModel::class.java)
        }
        if (!mViewModel!!.oneWord()!!.hasObservers()) {
            mViewModel!!.oneWord()!!.observe(this, Observer {
                if (it == null) {
                    showToastMsg("数据出错啦")
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog("正在获取数据")
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshOneWord(hashMapOf(), false)
                        showToastMsg("获取失败," + it.error!!.message)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshOneWord(hashMapOf(), false)
                        showToastMsg("数据出错啦")
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshOneWord(hashMapOf(), false)
                        if (it.data!!.code == 200) {
                            mPictureView.initData(it.data.data!!)
                        } else
                            showToastMsg("获取失败")
                    }
                }
            })
        }
        initData()
    }

    private fun initData() {
        val map = HashMap<String, String>()
        map[Contacts.TOKEN] = AppUtils.getString(Contacts.TOKEN, "")
        map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        map["sign"] = AppUtils.getMapString(map)
        map["param"] = Gson().toJson(TreeMap(map).descendingMap())
        mViewModel!!.freshOneWord(map, true)
    }
}
