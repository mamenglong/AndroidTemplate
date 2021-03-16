package com.maibaapp.sweetly.ui.main

import com.maibaapp.base.base.BaseViewBindFragment
import com.maibaapp.sweetly.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : com.maibaapp.base.base.BaseViewBindFragment<FragmentMainBinding>()  {
    override fun getViewBinding(): FragmentMainBinding {
        return FragmentMainBinding.inflate(layoutInflater)
    }

}