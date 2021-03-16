package com.maibaapp.sweetly.manager

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.maibaapp.sweetly.App
import com.maibaapp.sweetly.BuildConfig
import com.maibaapp.sweetly.ConstString
import com.maibaapp.sweetly.bean.User
import com.maibaapp.sweetly.ui.navigateToLogin
import com.maibaapp.sweetly.util.*
import com.maibaapp.sweetly.config.Config
import com.maibaapp.sweetly.log.LogUtil
import com.maibaapp.sweetly.util.Utils.safeHeaderValue


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/29 16:34
 * Description: This is UserManager
 * Package: com.withu.find.user
 * Project: With U
 */

abstract class ActivityLifecycleCallback : Application.ActivityLifecycleCallbacks {
    protected var activityCounter = 0
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        activityCounter++
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        if (activityCounter > 0)
            activityCounter--
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

}

class ShouldVipException:Exception("该功能需要vip才能使用，请先开通")
class ShouldLoginException:Exception("未登陆，请先登录")

object UserManager {

    val KEY_USER = if (ConstString.IS_ONLINE) "KEY_USER" else "KEY_USER_Test"
    const val KEY_AUTHORIZATION: String = "Authorization"
    private val config = Config.UserConfig
    val userInfoObservable = MutableLiveData<User?>(null)
    val otherLoginObserverLiveData = MutableLiveData<Any?>(null)
    private var isInit = false
    private val userInfoObserver = Observer<User?> {
        if (isInit) {
            refreshUserInfo(it)
        } else {
            isInit = true
        }
    }
    private var user: User? = kotlin.runCatching {
        val userString = config.decodeString(KEY_USER, "")
        LogUtil.d("mmkv get userString:$userString", this)
        Gson().fromJson(userString, User::class.java)
    }.fold(
        onSuccess = {
            userInfoObservable.postValue(it)
            it
        }, onFailure = {
            null
        }
    )
    val authentication by lazy { ""}
    var oaid:String = ""
    val userAgent by lazy {
        safeHeaderValue(
            "WithU ("
                    + App.application.packageName + "; "
                    + BuildConfig.VERSION_NAME + "; "
                    + BuildConfig.VERSION_CODE + ") Android ("
                    + Build.VERSION.RELEASE + "; "
                    + Build.VERSION.SDK_INT + "; "
                    + Build.FINGERPRINT + ")"
        )
    }
    private val isLogin: Boolean
        get() = user != null

    fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(object :
            ActivityLifecycleCallback() {
            override fun onActivityStarted(activity: Activity) {
                super.onActivityStarted(activity)
            }


            override fun onActivityStopped(activity: Activity) {
                super.onActivityStopped(activity)
            }
        })
        userInfoObservable.observeForever(userInfoObserver)
        LogUtil.d("init $user", this)
        userInfoObservable.postValue(user)
    }

    fun clearObservable() {
        userInfoObservable.removeObserver(userInfoObserver)
    }

    fun logout() {
        userInfoObservable.postValue(null)
    }

    /**
     * 单点登陆
     */
    fun otherLogin(){
        UserManager.checkLogin(result {
            onSuccess{
                logout()
                otherLoginObserverLiveData.postValue(Any())
            }
        })
    }
    fun login(user: User?) {
        userInfoObservable.postValue(user)
    }

    fun checkLogin(block: Result<User, Any?>) {
        LogUtil.d("isLogin:$isLogin", this)
        if (isLogin) {
            block.doSuccess(user!!)
        } else {
            block.doFailure(ShouldLoginException())
        }
    }

    /***
     * 检查是否vip，先检查登陆状态 在检查vip状态
     * 未登陆先登陆
     * @param isSecondPage 当前界面是已经是二级界面了，区分：当前界面是不是通过 navigateToContainerActivity 跳转来的
     */
    fun checkVip(fragment: Fragment, block: Result<User, Any?>, isSecondPage:Boolean = true){
        checkLogin(result {
            onSuccess{
              /*  it.vip.isVip().yes {
                    block.doSuccess(it)
                }.no {
                    fragment.showToast("该功能需要vip才能使用,请先开通vip.")
                    isSecondPage.yes {
                        fragment.findNavController().navigate(R.id.navVipInfoFragment,null,
                            navOptionsDefault_FADE()
                        )
                    }.no {
                        fragment.findNavController().navigateToContainerActivity(R.id.navVipInfoFragment)
                    }

                }*/
            }
            onFailure{
                fragment.showToast("请先登录.")
                fragment.findNavController().navigateToLogin()
            }
        })
    }
    fun checkVip(block: Result<User, Any?>){
        checkLogin(result {
            onSuccess{
             /*   it.vip.isVip().yes {
                    block.doSuccess(it)
                }.no {
                    block.doFailure(ShouldVipException())
                }*/
            }
            onFailure{
                block.doFailure(ShouldLoginException())
            }
        })
    }
    private fun refreshUserInfo(user: User?) {
        LogUtil.d("user:$user", this)
        UserManager.user = user
        saveUserInfo(user)
    }

    private fun saveUserInfo(user: User?) {
        user?.let {
            val json = Gson().toJson(it)
            LogUtil.d("mmkv set userString:$json", this)
            config.encode(KEY_USER, json)
            //LogUtil.d("mmkv get userString:${config.decodeString(KEY_USER)}",this)
        } ?: kotlin.run {
            config.remove(KEY_USER)
        }
    }

}