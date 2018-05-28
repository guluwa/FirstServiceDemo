package cn.guluwa.firstservicedemo.ui

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil.setContentView
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import cn.guluwa.firstservicedemo.R
import cn.guluwa.firstservicedemo.R.id.etInputText
import cn.guluwa.firstservicedemo.R.id.mToolBar
import cn.guluwa.firstservicedemo.base.BaseActivity
import cn.guluwa.firstservicedemo.data.viewmodel.WriteWordViewModel
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.utils.AppUtils
import com.google.gson.Gson
import com.hwangjr.rxbus.RxBus
import com.megglife.chaoren.data.bean.PageStatus
import kotlinx.android.synthetic.main.activity_write_word.*
import java.util.*

class WriteWordActivity : BaseActivity() {

    override val viewLayoutId = R.layout.activity_write_word

    private var mViewModel: WriteWordViewModel? = null

    override fun initViews() {
        initToolBar()
    }

    private fun initToolBar() {
        mToolBar.title = "编辑"
        setSupportActionBar(mToolBar)
        supportActionBar!!.setHomeButtonEnabled(true) //设置返回键可用
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * 菜单
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.write_word_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.action_finish -> finishWrite()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun finishWrite() {
        if (TextUtils.isEmpty(etInputText.text)) {
            showToastMsg("请填写内容")
            return
        }
        val map = HashMap<String, String>()
        map[Contacts.TOKEN] = AppUtils.getString(Contacts.TOKEN, "")
        map["content"] = etInputText.text.toString().trim()
        map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        println(map)
        map["sign"] = AppUtils.getMapString(map)
        map["param"] = Gson().toJson(TreeMap(map).descendingMap())
        mViewModel!!.freshWriteWords(map, true)
    }

    override fun initViewModel() {
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(this).get(WriteWordViewModel::class.java)
        }
        if (!mViewModel!!.writeWords()!!.hasObservers()) {
            mViewModel!!.writeWords()!!.observe(this, android.arch.lifecycle.Observer {
                if (it == null) {
                    showToastMsg("数据出错啦")
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {
                        showProgressDialog("正在保存")
                    }
                    PageStatus.Error -> {
                        dismissProgressDialog()
                        mViewModel!!.freshWriteWords(hashMapOf(), false)
                        showToastMsg("保存失败," + it.error!!.message)
                    }
                    PageStatus.Empty -> {
                        dismissProgressDialog()
                        mViewModel!!.freshWriteWords(hashMapOf(), false)
                        showToastMsg("数据出错啦")
                    }
                    PageStatus.Content -> {
                        dismissProgressDialog()
                        mViewModel!!.freshWriteWords(hashMapOf(), false)
                        if (it.data!!.code == 200) {
                            showToastMsg("保存成功")
                            RxBus.get().post("fresh", "fresh")
                            finish()
                        } else
                            showToastMsg("保存失败")
                    }
                }
            })
        }
    }
}
