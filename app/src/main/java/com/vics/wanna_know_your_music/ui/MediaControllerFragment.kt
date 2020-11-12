package com.vics.wanna_know_your_music.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.vics.WannaKnowYourMusic.R
import kotlinx.android.synthetic.main.fragment_media_controller.*


class MediaControllerFragment : Fragment() {

    private lateinit var mPlayPause: ImageView
    private lateinit var mContext: Context
    private lateinit var mSongTitle: TextView

    companion object {
        private const val TAG = "MediaControllerFragment"
    }

    // UI Components

    // Vars
    private var mSelectedMedia: MediaMetadataCompat? = null


    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_media_controller, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPlayPause = play_pause

        mPlayPause.setOnClickListener {  }
    }

    fun setIsPlaying(isPlaying: Boolean) {
        if(isPlaying) {
            Glide.with(mContext)
                .load(R.drawable.ic_play_circle_outline_white_24dp)
                .into(mPlayPause)
        }
        else {
            Glide.with(mContext)
                .load(R.drawable.ic_play_circle_outline_white_24dp)
                .into(mPlayPause)
        }
    }

    fun setMediaTitle(mediaItem: MediaMetadataCompat) {
        mSongTitle.text = mediaItem.description.title
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }
}
