package com.vics.wanna_know_your_music.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.CategoryRecyclerAdapter.ICategorySelector
import com.vics.wanna_know_your_music.models.Artist
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), IMainActivity, ICategorySelector {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testPlaylistFragment()
    }

    private fun testHomeFragment() {
        Log.d(TAG, "testFragment: here")
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment().newInstance())
            .commit()
    }

    private fun testCategoryFragment() {
        Log.d(TAG, "testFragment: here")
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, CategoryFragment().newInstance("Music"))
            .commit()
    }

    private fun testPlaylistFragment() {
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.main_container,
                PlaylistFragment().newInstance(
                    "Podcasts",
                    Artist(
                        "CodingWithMitch",
                        "https://assets.blubrry.com/coverart/orig/654497-584077.png",
                        "m2BE0t4z0raEqqqgHXj4"
                    )
                )
            ).commit()
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