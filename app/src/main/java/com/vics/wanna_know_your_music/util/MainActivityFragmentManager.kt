package com.vics.wanna_know_your_music.util

import androidx.fragment.app.Fragment

class MainActivityFragmentManager {
    private var mFragments: ArrayList<Fragment> = ArrayList()
    private var instance: MainActivityFragmentManager? = null

    fun getInstance(): MainActivityFragmentManager {
        if(instance == null) {
            instance = MainActivityFragmentManager()
        }
        return instance as MainActivityFragmentManager
    }

    fun addFragment(fragment: Fragment) {
        mFragments.add(fragment)
    }

    fun removeFragment(fragment: Fragment) {
        mFragments.remove(fragment)
    }

    fun removeFragment(position: Int) {
        mFragments.removeAt(position)
    }

    fun getFragments(): ArrayList<Fragment> {
        return mFragments
    }

    fun removeAllFragments() {
        mFragments.clear()
    }
}