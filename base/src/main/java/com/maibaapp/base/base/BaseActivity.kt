package com.maibaapp.base.base


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar
import com.maibaapp.base.BuildConfig
import com.maibaapp.base.R
import com.maibaapp.base.config.MMKV
import com.maibaapp.base.databinding.LayoutUserPrivacyBinding
import com.maibaapp.base.yes
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.lang.ref.WeakReference
import kotlin.coroutines.CoroutineContext


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 19-8-15 上午11:22
 * Description: This is BaseActivity
 * Package: com.maibaapp.basewheelsbykotlin
 * Project: BaseWheelsByKotlin
 */
abstract class BaseActivity : AppCompatActivity(),
    CoroutineScope {
    open val isCanShowAgreeDialog = false
    open val isDebugCanShowAgreeDialog = false
    open val dialogMsg = ""
    private var hasShowAgreeDialog = false
    private val job: Job = Job()

    open val agreementDialogClickCallback: AgreementDialogClickCallback?=null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job + CoroutineExceptionHandler() { coroutineContext, throwable ->
            onCoroutineException(coroutineContext, throwable)
        }

    protected fun onCoroutineException(coroutineContext: CoroutineContext, throwable: Throwable) {

    }

    companion object {
        const val PERMISSION_CODE = 0X01
    }

    private var weakRefActivity: WeakReference<Activity>? = null

    /**
     * 判断当前Activity是否在前台。
     */
    protected var isActive: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        weakRefActivity = WeakReference(this)
        initImmersionBar()
        initDataBeforeView()
        initView()
        initListener()
        initData()
        initViewAfterData()
        registerLiveEvent()
    }

    override fun onStart() {
        super.onStart()
        if (isCanShowAgreeDialog && (MMKV.default()
                .getBoolean(MMKV.isShowAgreeDialog, true) || isDebugCanShowAgreeDialog)
        ) {
            if (hasShowAgreeDialog)
                return
            hasShowAgreeDialog = true
            val dialogBuilder = AlertDialog.Builder(this)
            val view = LayoutUserPrivacyBinding.inflate(layoutInflater)
            view.tvPrivacy.setOnClickListener {
                agreementDialogClickCallback?.onPrivacyAgreementClick()
            }
            view.tvUser.setOnClickListener {
                agreementDialogClickCallback?.onUserAgreementClick()
            }
            dialogBuilder.apply {
                setTitle("温馨提示")
                setMessage(dialogMsg)
                setPositiveButton("同意") { dialog, which ->
                    MMKV.default().putBoolean(MMKV.isShowAgreeDialog, false)
                    agreementDialogClickCallback?.onPositiveButtonClick(dialog)
                }
                setNegativeButton("不同意") { dialog, which ->
                    agreementDialogClickCallback?.onNegativeButtonClick(dialog)
                }
                setView(view.root)
                setCancelable(false)
                setOnDismissListener {
                    hasShowAgreeDialog = false
                    agreementDialogClickCallback?.onDismiss()
                }
            }.show().setOnShowListener {
                hasShowAgreeDialog = true
                agreementDialogClickCallback?.onShow()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isActive = true
//        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        isActive = false
//        MobclickAgent.onPause(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterLiveEvent()
        // 关闭页面后，结束所有协程任务
        job.cancel()
    }

    /**
     * 默认为沉浸式状态栏
     */
    private fun initImmersionBar() {
        immersionBar {
            if (!isEnableImmersionBar()||initStatusBarColor()!=null) {
                // 当设置 fitsSystemWindows 为 true 和状态栏颜色后就不是沉浸式状态栏了
                fitsSystemWindows(true)
                initStatusBarColor()?.let {
                    statusBarColor(it)
                }
            }
            navigationBarColor(R.color.white)
            navigationBarDarkIcon(true, 0.2F)
            statusBarDarkFont(true, 0.2F)
        }
    }

    /**
     * 是否设置沉浸式状态栏
     * @return true 为沉浸式状态栏 false 则状态栏的颜色默认为白色，如需修改颜色调用
     * @see BaseActivity.initStatusBarColor
     */
    protected open fun isEnableImmersionBar(): Boolean = true

    /**
     * 设置状态栏颜色(设置后默认取消沉浸式状态栏)
     * 注意：不设置状态栏颜色则默认为沉浸式状态栏,布局会延伸到屏幕顶部
     * (1).当使用封装好的 TitleView 后 它会识别如果是沉浸式 会将标题栏的高度加上状态栏的高度 保持正常
     * (2).当没使用 TitleView 来充当状态栏 会出现状态栏和标题栏重叠情况,这时候重写 isEnableImmersionBar() 返回 false 即可去掉沉浸式
     */
    protected open fun initStatusBarColor(): Int? = null

    open fun setContentView() {
        setContentView(getLayoutId())
    }

    /**
     * 点击事件
     */
    open fun initListener() {}

    /**
     * 设置布局id
     */
    open fun getLayoutId(): Int {
        return 0
    }

    /**
     * 初始化view  before [initData]
     */
    open fun initView() {}

    /**
     * 初始化数据  after [initView]
     */
    open fun initData() {}

    open fun initDataBeforeView() {

    }

    /**
     * 初始化view after [initData]
     */
    open fun initViewAfterData() {}

    /**
     * 注册live data event 事件监听
     */
    protected open fun registerLiveEvent() {}
    protected open fun unRegisterLiveEvent() {}

    protected open fun startActivity(z: Class<*>) {
        startActivity(Intent(applicationContext, z))
    }


    protected open fun showToast(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }
    protected open fun showDebugToast(msg: String) {
        BuildConfig.DEBUG.yes {
            showToast(msg)
        }
    }
    /*功能函数*/

    /**
     * 隐藏软键盘。
     */
    fun hideSoftKeyboard() {
        try {
            val view = currentFocus
            if (view != null) {
                val binder = view.windowToken
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        } catch (e: Exception) {

        }

    }

    /**
     * 显示软键盘。
     */
    fun showSoftKeyboard(editText: EditText?) {
        try {
            if (editText != null) {
                editText.requestFocus()
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.showSoftInput(editText, 0)
            }
        } catch (e: Exception) {

        }

    }

    open override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        (supportFragmentManager.findFragmentByTag("") as BaseFragment?)?.onReenter(data)
    }

}