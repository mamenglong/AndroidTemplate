package com.maibaapp.sweetly.base.base

import androidx.lifecycle.*
import com.maibaapp.sweetly.bean.User
import com.maibaapp.sweetly.manager.UserManager
import com.maibaapp.sweetly.util.Result
import kotlinx.coroutines.*
import java.lang.Exception


open class BaseViewModel : ViewModel(), LifecycleObserver {
    open var TIME_OUT = 5*1000L

    private val error by lazy { MutableLiveData<Exception?>() }

    private val finally by lazy { MutableLiveData<Int>() }

    //运行在UI线程的协程
    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch(Dispatchers.Main) {
        try {
            withTimeout(TIME_OUT){
                block()
            }
        } catch (e: Exception) {
            error.value = e
        } finally {
            finally.value = 200
        }
    }
    //运行在UI线程的协程
    fun launchIO(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch (Dispatchers.IO){
        try {
            withTimeout(TIME_OUT){
                block()
            }
        } catch (e: Exception) {
           println("error:${e}")
            error.postValue(e)
        } finally {
            println(200)
            finally.postValue(200)
        }
    }
    /**
     * 请求失败，出现异常
     */
    fun getError(): LiveData<Exception?> {
        return error
    }

    /**
     * 请求完成，在此处做一些关闭操作
     */
    fun getFinally(): LiveData<Int> {
        return finally
    }
    fun checkLogin(block: Result<User, Any?>){
        UserManager.checkLogin(block)
    }
}