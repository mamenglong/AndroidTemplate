package com.mml.template.ui

import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mml.template.R
import com.mml.template.databinding.ActivityMainBinding
import com.mml.base.log.LogUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : com.mml.base.base.BaseViewBindActivity<ActivityMainBinding>() {
    companion object{
        const val MAIN_ACTION = "com.mml.com.mml.android.android.template.action.MAIN"
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