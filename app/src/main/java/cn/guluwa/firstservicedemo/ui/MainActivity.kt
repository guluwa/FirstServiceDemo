package cn.guluwa.firstservicedemo.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.support.constraint.ConstraintLayout
import android.support.constraint.solver.widgets.ConstraintTableLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import cn.guluwa.firstservicedemo.R
import cn.guluwa.firstservicedemo.R.id.*
import cn.guluwa.firstservicedemo.base.BaseActivity
import cn.guluwa.firstservicedemo.data.bean.PageTipBean
import cn.guluwa.firstservicedemo.data.bean.WordBean
import cn.guluwa.firstservicedemo.data.viewmodel.MainViewModel
import cn.guluwa.firstservicedemo.databinding.ActivityMainBinding
import cn.guluwa.firstservicedemo.manage.Contacts
import cn.guluwa.firstservicedemo.ui.viewbinder.PageTipViewBinder
import cn.guluwa.firstservicedemo.ui.viewbinder.WordViewBinder
import cn.guluwa.firstservicedemo.utils.AppUtils
import com.google.gson.Gson
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.megglife.chaoren.data.bean.PageStatus
import kotlinx.android.synthetic.main.activity_main.*
import me.drakeet.multitype.Items
import me.drakeet.multitype.MultiTypeAdapter
import java.util.*


class MainActivity : BaseActivity() {

    private var mViewModel: MainViewModel? = null

    override val viewLayoutId: Int get() = R.layout.activity_main

    private var page = 0

    override fun initViews() {
        initToolBar()
        initRecyclerView()
        initReFreshLayout()
        initClickEvent()
    }

    private fun initToolBar() {
        mToolBar.title = "咕噜咕噜"
    }

    private fun initRecyclerView() {
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MultiTypeAdapter(arrayListOf(PageTipBean("", 0)))
        adapter.register(WordBean::class.java, WordViewBinder())
        adapter.register(PageTipBean::class.java, PageTipViewBinder())
        mRecyclerView.adapter = adapter
    }

    private fun initReFreshLayout() {
        mRefreshLayout.setOnRefreshListener {
            page = 0
            initData()
        }
    }

    private fun initClickEvent() {
        (mViewDataBinding as ActivityMainBinding).setClickListener { view ->
            when (view.id) {
//                R.id.fabAddMatch -> startActivity(Intent(this, WriteWordActivity::class.java))
                R.id.fabAddMatch -> startActivity(Intent(this, Main2Activity::class.java))
                R.id.flLogOut -> {
                    AppUtils.setString(Contacts.TOKEN, "")
                    AppUtils.setString("id", "")
                    AppUtils.setString("username", "")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
                R.id.flOneWord -> {
                    mDrawerLayout.closeDrawer(Gravity.START)
                    startActivity(Intent(this, OneWordActivity::class.java))
                }
            }
        }
    }

    override fun initViewModel() {
        if (mViewModel == null) {
            mViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        }
        if (!mViewModel!!.queryWords()!!.hasObservers()) {
            mViewModel!!.queryWords()!!.observe(this, Observer {
                if (it == null) {
                    handleError("数据出错啦")
                    return@Observer
                }
                when (it.status) {
                    PageStatus.Loading -> {

                    }
                    PageStatus.Error -> {
                        mViewModel!!.freshQueryWords(hashMapOf(), false)
                        handleError("获取失败," + it.error!!.message)

                    }
                    PageStatus.Empty -> {
                        mViewModel!!.freshQueryWords(hashMapOf(), false)
                        handleError("数据出错啦")
                    }
                    PageStatus.Content -> {
                        mViewModel!!.freshQueryWords(hashMapOf(), false)
                        if (it.data!!.code == 200) {
                            handleContent(it.data.data!!)
                        } else
                            showToastMsg("获取失败")
                    }
                }
            })
            initData()
        }
    }

    private fun handleError(msg: String) {
        mRefreshLayout.isRefreshing = false
        if (page == 0 && (mRecyclerView.adapter as MultiTypeAdapter).items[0] is PageTipBean) {
            ((mRecyclerView.adapter as MultiTypeAdapter).items[0] as PageTipBean).tip = msg
            ((mRecyclerView.adapter as MultiTypeAdapter).items[0] as PageTipBean).status = 1
            mRecyclerView.adapter.notifyItemChanged(0)
        } else {
            showToastMsg(msg)
        }
    }

    private fun handleContent(data: List<WordBean>) {
        mRefreshLayout.isRefreshing = false
        if (data.isEmpty()) {
            handleError("还没有数据哦")
        } else {
            if (page == 0) {
                (mRecyclerView.adapter as MultiTypeAdapter).items.clear()
            }
            (mRecyclerView.adapter as MultiTypeAdapter).items.addAll(data as Collection<Nothing>)
            mRecyclerView.adapter.notifyDataSetChanged()
            page++
        }
    }

    private fun initData() {
        val map = HashMap<String, String>()
        map[Contacts.TOKEN] = AppUtils.getString(Contacts.TOKEN, "")
        map["page"] = page.toString()
        map["timestamp"] = (System.currentTimeMillis() / 1000).toString()
        map["sign"] = AppUtils.getMapString(map)
        map["param"] = Gson().toJson(TreeMap(map).descendingMap())
        mViewModel!!.freshQueryWords(map, true)
    }

    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [(Tag("fresh"))])
    fun receiveData(data: String) {
        page = 0
        initData()
    }
}
