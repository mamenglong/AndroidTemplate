package com.maibaapp.sweetly

import com.maibaapp.sweetly.base.base.BaseViewBindActivity
import com.maibaapp.sweetly.databinding.ActivityDebugBinding

class DebugActivity : BaseViewBindActivity<ActivityDebugBinding>() {
    override fun getViewBinding(): ActivityDebugBinding {
        return ActivityDebugBinding.inflate(layoutInflater)
    }
}