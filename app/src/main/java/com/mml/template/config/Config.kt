package com.mml.template.config

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/10/29 16:44
 * Description: This is Config
 * Package: com.withu.find.config
 * Project: With U
 */
object Config {

     val UserConfig by lazy {
         com.mml.base.config.MMKV.mmkvWithID("user")
     }
}