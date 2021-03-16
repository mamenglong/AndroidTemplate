package com.maibaapp.sweetly

import com.maibaapp.base.base.BaseViewBindActivity
import com.maibaapp.sweetly.databinding.ActivityDebugBinding

class DebugActivity : com.maibaapp.base.base.BaseViewBindActivity<ActivityDebugBinding>() {
    override fun getViewBinding(): ActivityDebugBinding {
        return ActivityDebugBinding.inflate(layoutInflater)
    }
}