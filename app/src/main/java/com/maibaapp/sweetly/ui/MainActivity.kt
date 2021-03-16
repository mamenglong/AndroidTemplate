package com.maibaapp.sweetly.ui

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.maibaapp.sweetly.R
import com.maibaapp.base.base.BaseViewBindActivity
import com.maibaapp.sweetly.databinding.ActivityMainBinding
import com.maibaapp.base.log.LogUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : com.maibaapp.base.base.BaseViewBindActivity<ActivityMainBinding>() {
    companion object{
        const val MAIN_ACTION = "com.maibaapp.com.maibaapp.android.sweetly.action.MAIN"
    }
    private lateinit var navController: NavController
    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
    override fun initView() {
        super.initView()
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            LogUtil.d("destination:${destination} arguments:${arguments}", this.javaClass.simpleName)
        }
    }
    var lastClickBackKey = 0L
    override fun onBackPressed() {
        if (System.currentTimeMillis()-lastClickBackKey<=3000){
            super.onBackPressed()
        }else{
            lastClickBackKey = System.currentTimeMillis()
            showToast("再次点击返回键退出APP.")
        }
        /* if (!navController.navigateUp()) {
             super.onBackPressed()
         }*/
    }

}