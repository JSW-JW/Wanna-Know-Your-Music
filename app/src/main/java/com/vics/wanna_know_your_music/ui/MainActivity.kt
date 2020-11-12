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


class MainActivity : AppCompatActivity(), IMainActivity {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadFragment(HomeFragment().newInstance(), true)
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
            transaction.addToBackStack(tag)
        } else if (fragment is PlaylistFragment) {
            tag = getString(R.string.fragment_playlist)
            transaction.addToBackStack(tag)
        }

        transaction.add(R.id.main_container, fragment, tag)
        transaction.commit()
        Log.d(TAG, "loadFragment: $tag")

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

        val fragments: ArrayList<Fragment> =
            ArrayList(MainActivityFragmentManager.instance?.fragments)
        Log.d(TAG, "showFragment: $fragments")
        for (f: Fragment in fragments) {
            if (f != null) {
                if (!f.tag?.equals(fragment.tag)!!) {
                    val t: FragmentTransaction = supportFragmentManager.beginTransaction()
                    t.hide(f)
                    t.commit()
                }
            }
        }
    }

    override fun onBackPressed() {
        val fragments: ArrayList<Fragment> =
            ArrayList(MainActivityFragmentManager.instance!!.fragments)
        if (fragments.size > 1) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.remove(fragments[fragments.size - 1])
            transaction.commit()

            MainActivityFragmentManager.instance!!.removeFragment(fragments.size - 1)
            showFragment(fragments[fragments.size - 2], true)
        } else {
            MainActivityFragmentManager.instance!!.removeFragment(fragments.size - 1)
        }
        super.onBackPressed()
    }

    override fun hideProgressBar() {
        progress_bar.visibility = View.GONE
    }

    override fun showProgressBar() {
        progress_bar.visibility = View.VISIBLE
    }

    override fun onCategorySelected(category: String) {
        loadFragment(CategoryFragment().newInstance(category), true)
    }

    override fun onArtistSelected(category: String, artist: Artist) {
        loadFragment(PlaylistFragment().newInstance(category, artist), true)
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

}