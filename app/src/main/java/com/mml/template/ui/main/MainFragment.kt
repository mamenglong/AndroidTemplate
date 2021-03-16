package com.mml.template.ui.main

import com.mml.template.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : com.mml.base.base.BaseViewBindFragment<FragmentMainBinding>()  {
    override fun getViewBinding(): FragmentMainBinding {
        return FragmentMainBinding.inflate(layoutInflater)
    }

}