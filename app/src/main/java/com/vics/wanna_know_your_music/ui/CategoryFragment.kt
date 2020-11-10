package com.vics.wanna_know_your_music.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.vics.WannaKnowYourMusic.R


class CategoryFragment : Fragment() {

    companion object {
        private const val TAG = "CategoryFragment"
    }

    // UI Components

    // Vars

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

}