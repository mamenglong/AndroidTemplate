package com.mml.template.net.concurrent

import java.util.concurrent.Future

open class FeatureTaker {
    var future: Future<*>? = null
        internal set
}