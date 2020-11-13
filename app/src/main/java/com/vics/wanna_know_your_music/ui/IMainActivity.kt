package com.vics.wanna_know_your_music.ui

import com.vics.wanna_know_your_music.models.Artist

interface IMainActivity {

    fun hideProgressBar()

    fun showProgressBar()

    fun onCategorySelected(category: String)

    fun onArtistSelected(category: String, artist: Artist)

    fun setActionBarTitle(title: String)

    fun playPause()
}