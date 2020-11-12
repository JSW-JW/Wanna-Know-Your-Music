package com.vics.wanna_know_your_music.players

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util


class MediaPlayerAdapter(context: Context) : PlayerAdapter(context) {
    companion object {
        const val TAG = "MediaPlayerAdapter"
    }

    private val mContext: Context
    private var mCurrentMedia: MediaMetadataCompat? = null
    private var mCurrentMediaPlayedToCompletion = false

    init {
        mContext = context.getApplicationContext()
    }

    // ExoPlayer objects
    private var mExoPlayer: SimpleExoPlayer? = null
    private var mTrackSelector: TrackSelector? = null
    private var mRenderersFactory: DefaultRenderersFactory? = null
    private var mDataSourceFactory: DataSource.Factory? = null
    private fun initializeExoPlayer() {
        if (mExoPlayer == null) {
            mTrackSelector = DefaultTrackSelector()
            mRenderersFactory = DefaultRenderersFactory(mContext)
            mDataSourceFactory =
                DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "AudioStreamer"))
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                mContext,
                mRenderersFactory as DefaultRenderersFactory,
                mTrackSelector as TrackSelector
            )
        }
    }

    private fun release() {
        if (mExoPlayer != null) {
            mExoPlayer!!.release()
            mExoPlayer = null
        }
    }

    override fun onPlay() {}
    override fun onPause() {}
    override fun playFromMedia(metadata: MediaMetadataCompat?) {}
    override val currentMedia: MediaMetadataCompat?
        get() = null

    override val isPlaying: Boolean
        get() = false

    override fun onStop() {}
    override fun seekTo(position: Long) {}
    override fun setVolume(volume: Float) {}

    private fun playFile(metaData: MediaMetadataCompat) {
        val mediaId = metaData.description.mediaId
        var mediaChanged =
            mCurrentMedia == null || mediaId != (mCurrentMedia as MediaMetadataCompat).description.mediaId
        if (mCurrentMediaPlayedToCompletion) {
            // Last audio file was played to completion, the resourceId hasn't changed, but the
            // player was released, so force a reload of the media file for playback.
            mediaChanged = true
            mCurrentMediaPlayedToCompletion = false
        }
        if (!mediaChanged) {
            if (!isPlaying) {
                play()
            }
            return
        } else {
            release()
        }
        mCurrentMedia = metaData
        initializeExoPlayer()
        try {
            val audioSource: MediaSource = ExtractorMediaSource.Factory(mDataSourceFactory!!)
                .createMediaSource(Uri.parse(metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)))
            mExoPlayer!!.prepare(audioSource)
            Log.d(PlayerAdapter.TAG, "onPlayerStateChanged: PREPARE")
        } catch (e: Exception) {
            throw RuntimeException(
                "Failed to play media uri: "
                        + metaData.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI), e
            )
        }
        play()
    }

    private fun startTrackingPlayback() {
        // Begin tracking the playback
    }

}
