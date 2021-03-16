package com.maibaapp.sweetly.ui.main

import com.maibaapp.sweetly.base.base.BaseViewBindFragment
import com.maibaapp.sweetly.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : BaseViewBindFragment<FragmentMainBinding>()  {
    override fun getViewBinding(): FragmentMainBinding {
        return FragmentMainBinding.inflate(layoutInflater)
    }

}