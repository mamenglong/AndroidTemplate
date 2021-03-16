package com.mml.template.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.mml.template.R
import com.mml.template.databinding.ActivityContainerBinding
import com.mml.base.log.LogUtil
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerActivity : com.mml.base.base.BaseViewBindActivity<ActivityContainerBinding>() {
    private var destinationFragmentId = MutableLiveData<Int>(-1)
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var graph:NavGraph
    override fun getViewBinding(): ActivityContainerBinding {
        return ActivityContainerBinding.inflate(layoutInflater)
    }

    override fun initView() {
        super.initView()
        val bundle = intent.extras?.let { ContainerActivityArgs.fromBundle(it) }
        destinationFragmentId.value = bundle?.destination ?:-1
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        val inflater = navController.navInflater
        graph = inflater.inflate(R.navigation.mobile_navigation)
        graph.startDestination = destinationFragmentId.value!!
        navHostFragment.navController.setGraph(graph, intent.extras)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            LogUtil.d(
                "destination:${destination} arguments:${arguments}",
                this.javaClass.simpleName
            )
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onBackPressed() {
        val handel = getFragment(HandelFragmentBackPressed::class.java)
        if (handel==null) {
            if (!navController.navigateUp())
                super.onBackPressed()
        }else{
            if (!handel.onBackPressed()){
                if (!navController.navigateUp())
                    super.onBackPressed()
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    @Suppress("UNCHECKED_CAST")
    fun <F : HandelFragmentBackPressed> AppCompatActivity.getFragment(fragmentClass: Class<F>): F? {
        val navHostFragment = this.supportFragmentManager.fragments.first() as NavHostFragment

        navHostFragment.childFragmentManager.fragments.forEach {
            if (fragmentClass.isAssignableFrom(it.javaClass)) {
                return it as F
            }
        }
        return null
    }
}
interface HandelFragmentBackPressed{
    fun onBackPressed():Boolean
}
