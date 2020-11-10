package com.vics.wanna_know_your_music.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.vics.WannaKnowYourMusic.R


class MediaControllerFragment : Fragment() {

    companion object {
        private const val TAG = "MediaControllerFragment"
    }

    // UI Components

    // Vars

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_media_controller, container, false)
    }
}
