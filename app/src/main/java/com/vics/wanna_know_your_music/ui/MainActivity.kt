package com.vics.wanna_know_your_music.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.CategoryRecyclerAdapter.ICategorySelector
import com.vics.wanna_know_your_music.models.Artist
import com.vics.wanna_know_your_music.util.MainActivityFragmentManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), IMainActivity, ICategorySelector {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    private fun loadFragment(fragment: Fragment, lateralMovement: Boolean) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        var tag: String? = null

        if (lateralMovement) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }

        if (fragment is HomeFragment) {
            tag = getString(R.string.fragment_home)
        } else if (fragment is CategoryFragment) {
            tag = getString(R.string.fragment_category)
        } else if (fragment is PlaylistFragment) {
            tag = getString(R.string.fragment_playlist)
        }

        transaction.add(R.id.main_container, fragment, tag)
        transaction.commit()

        MainActivityFragmentManager.instance!!.addFragment(fragment)

        showFragment(fragment, false)
    }

    private fun showFragment(fragment: Fragment, backwardsMovement: Boolean) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()

        if (backwardsMovement) {
            transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right)
        }  // flag to execute animation

        transaction.show(fragment)  // let the fragment which exists to be at the front
        transaction.commit()

        for(f: Fragment in MainActivityFragmentManager.instance!!.fragments) {
            if(f != null) {
                if(!f.tag?.equals(fragment.tag)!!) {
                    val t: FragmentTransaction = supportFragmentManager.beginTransaction()
                    t.hide(f)
                    t.commit()
                }
            }
        }
    }

    override fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }

    override fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun onArtistSelected(position: Int) {
        /* supportFragmentManager.beginTransaction()
             .replace(R.id.main_container, CategoryFragment().newInstance())
             .commit()*/
    }
}