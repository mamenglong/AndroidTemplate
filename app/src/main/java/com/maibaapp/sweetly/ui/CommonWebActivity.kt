package com.maibaapp.sweetly.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.maibaapp.sweetly.databinding.ActivityCommonWebBinding
import com.maibaapp.base.log.LogUtil
import com.maibaapp.sweetly.util.Utils
import com.maibaapp.sweetly.util.startActivitySafely

class CommonWebActivity : com.maibaapp.base.base.BaseViewBindActivity<ActivityCommonWebBinding>() {
    companion object {
        private const val PARAM_URL = "param_url"
        private const val PARAM_TITLE = "param_title"
        private const val PARAM_FROM = "param_from"

        /**
         * 不带自定义标题的网页启动方法，页面标题将获取 url 网页的标题
         */
        fun start(context: Context, url: String): Boolean = start(context, url, "")

        /**
         * 带自定义标题的网页启动方法，页面标题将展示自定义标题
         */
        fun start(
            context: Context,
            url: String?,
            title: String? = null,
            from: String? = null
        ): Boolean =
            context.startActivitySafely(Intent().apply {
                setClass(context, CommonWebActivity::class.java)
                putExtra(PARAM_URL, url?:"")
                putExtra(PARAM_TITLE, title ?: "")
                putExtra(PARAM_FROM, from)
            })
    }

    private lateinit var webView: WebView
    private val url by lazy { intent.getStringExtra(PARAM_URL) ?: "" }
    private val title by lazy { intent.getStringExtra(PARAM_TITLE) ?: "网页" }
    private val from by lazy { intent.getStringExtra(PARAM_FROM) ?: "" }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        LogUtil.d("onResume",this)
    }
    override fun onPause() {
        kotlin.runCatching {
            super.onPause()
        }
        LogUtil.d("onPause",this)
    }
    override fun onStop() {
        super.onStop()
        LogUtil.d("onStop",this)
    }

    override fun isEnableImmersionBar(): Boolean {
        return false
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        val webView = WebView(this.createConfigurationContext(Configuration()))
        activityBinding.webViewWrapper.addView(
            webView, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        this.webView = webView
        setSupportActionBar(activityBinding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        LogUtil.d("url:$url", this)
        if (url.isEmpty()) {
            setTitle("链接为空")
            showToast("链接为空")
            finish()
            return
        }

        setTitle(title)
        webView.loadUrl(url)
        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                activityBinding.pbWeb.visibility = View.VISIBLE
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                url?.let {
                    LogUtil.d("shouldOverrideUrlLoading url:$url", this)
                    return if (it.startsWith("tel:", true)) {
                        Utils.dialTo(this@CommonWebActivity, it)
                    } else if (it.startsWith("mailto:", true)) {
                        Utils.emailTo(this@CommonWebActivity, it)
                    } else if (it.startsWith("mqqwpa:", true) ||
                        it.startsWith("mqqapi:", true)
                    ) {
                        Utils.openQQ(this@CommonWebActivity, it)
                    } else if (it.startsWith("alipays", true)) {
                        Utils.openBrowser(this@CommonWebActivity, it)
                    } else if (it.startsWith("weixin", true)) {
                        Utils.openBrowser(this@CommonWebActivity, it)
                        false
                    } else {
                        false
                    }
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                activityBinding.pbWeb.visibility = View.GONE
                if (title.isEmpty()) setTitle(view?.title ?: title)
            }
        }
        webView.settings.javaScriptEnabled = true
    }

    private fun canGoBack(): Boolean {
        return webView.canGoBack()
    }

    override fun onDestroy() {
        super.onDestroy()
        webView.webViewClient = object : WebViewClient(){}
    }
    override fun getViewBinding(): ActivityCommonWebBinding {
        return ActivityCommonWebBinding.inflate(layoutInflater)
    }
}