package com.codingwithmitch.wanna_know_your_music

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.codingwithmitch.audiostreamer.R


class HomeFragment : Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
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