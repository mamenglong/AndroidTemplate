package com.maibaapp.sweetly.ui

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.maibaapp.sweetly.MobileNavigationDirections
import com.maibaapp.sweetly.R

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/4 20:58
 * Description: This is Nav
 * Package: com.withu.find.ui
 * Project: With U
 */

fun navOptionsDefault(): NavOptions {
    return NavOptions.Builder().apply {
        setLaunchSingleTop(true)
        setEnterAnim(R.anim.slide_in_right)
        setExitAnim(R.anim.slide_out_left)
        setPopEnterAnim(R.anim.slide_in_left)
        setPopExitAnim(R.anim.slide_out_right)
    }.build()
}

fun navOptionsDefault_FADE(): NavOptions {
    return NavOptions.Builder().apply {
        setLaunchSingleTop(true)
        setEnterAnim(R.anim.fade_in)
        setExitAnim(R.anim.fade_out)
        setPopEnterAnim(R.anim.fade_in)
        setPopExitAnim(R.anim.fade_out)
    }.build()
}
fun NavController.navigateToContainerActivity(destinationFragment:Int,bundle: Bundle?=null){
    val argument = MobileNavigationDirections.actionGlobalNavigationContainerActivity()
    argument.destination = destinationFragment
    bundle?.let {
        argument.arguments.putAll(bundle)
    }
    navigate(R.id.action_global_navigation_container_activity, argument.arguments, navOptionsDefault())
}
fun NavController.navigateToLogin(){
    //navigateToContainerActivity(R.id.navGroupLogin)
}