package com.vics.wanna_know_your_music.util

import androidx.fragment.app.Fragment

class MainActivityFragmentManager {
    val fragments: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }

    fun removeFragment(fragment: Fragment?) {
        fragments.remove(fragment)
    }

    fun removeFragment(position: Int) {
        fragments.removeAt(position)
    }

    fun removeAllFragments() {
        fragments.clear()
    }

    companion object {
        var instance: MainActivityFragmentManager? = null
            get() {
                if (field == null) {
                    field = MainActivityFragmentManager()
                }
                return field
            }
            private set
    }
}