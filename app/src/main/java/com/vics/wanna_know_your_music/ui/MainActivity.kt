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

    private fun loadFragment(fragment: Fragment, lateralMovement: Boolean){
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        var tag: String? = null

        if(lateralMovement) {
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)
        }

        if(fragment is HomeFragment) {
            tag = getString(R.string.fragment_home)
        }
        else if(fragment is CategoryFragment) {
            tag = getString(R.string.fragment_category)
        }
        else if(fragment is PlaylistFragment) {
            tag = getString(R.string.fragment_playlist)
        }

        transaction.add(R.id.main_container, fragment, tag)
        transaction.commit()

        MainActivityFragmentManager().getInstance().addFragment(fragment)
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