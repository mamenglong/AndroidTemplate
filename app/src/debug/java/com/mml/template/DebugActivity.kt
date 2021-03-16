package com.mml.template

import com.mml.template.databinding.ActivityDebugBinding

class DebugActivity : com.mml.base.base.BaseViewBindActivity<ActivityDebugBinding>() {
    override fun getViewBinding(): ActivityDebugBinding {
        return ActivityDebugBinding.inflate(layoutInflater)
    }
}