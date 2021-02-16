package com.aatech_aplha.news.util

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.aatech_aplha.news.R


fun settingFragment(fm: FragmentManager, fragment: Fragment, tag: String) {
    fm.beginTransaction().replace(R.id.fragment, fragment, tag)
        .commit()
}