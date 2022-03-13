package com.test.newsapp.extensions

import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.test.newsapp.ui.mainActivity.MainActivity

fun Fragment.applyAppBarLayout(block: (AppBarLayout).() -> Unit) {
    (activity as MainActivity?)?.appBarLayout?.apply(block)
}

fun Fragment.applyDefaultScrollFlags() {
    val defaultScrollFlags: Int = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
            AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED or
            AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP
    applyScrollFlags(defaultScrollFlags)
}

fun Fragment.applyScrollFlags(scrollFlags: Int) {
    val layout = (activity as MainActivity?)?.binding?.collapsingToolbarLayout
    val params = layout?.layoutParams as? AppBarLayout.LayoutParams

    params?.scrollFlags = scrollFlags
}

fun Fragment.applySupportActionBar(block: (ActionBar).() -> Unit) {
    (activity as AppCompatActivity?)?.supportActionBar?.apply(block)
}