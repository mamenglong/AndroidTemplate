package com.maibaapp.sweetly.net.time

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/29 17:24
 * Description: This is Console
 * Package: com.withu.find.time
 * Project: With U
 */
class Console {
    fun log(msg:(String)->Unit){

    }
    fun err(tr: Throwable){}
    fun warn(s: String) {

    }
}
val <T : Any?> T.console: Console
get() = Console()