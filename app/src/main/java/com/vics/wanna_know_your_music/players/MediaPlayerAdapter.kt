package com.vics.wanna_know_your_music.players

import android.content.Context
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util


class MediaPlayerAdapter(context: Context) : PlayerAdapter(context) {
    companion object {
        const val TAG = "MediaPlayerAdapter"
        private var mStartTime: Long = 0
    }

    private val mContext: Context
    private var mCurrentMedia: MediaMetadataCompat? = null
    private var mCurrentMediaPlayedToCompletion = false
    private var mState: Int = 0

    init {
        mContext = context.applicationContext
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
            mExoPlayer as SimpleExoPlayer
        }
    }

    private fun release() {
        if (mExoPlayer != null) {
            mExoPlayer!!.release()
            mExoPlayer = null
        }
    }

    override fun onPlay() {
        if(mExoPlayer != null && !(mExoPlayer as SimpleExoPlayer).playWhenReady) {
            (mExoPlayer as SimpleExoPlayer).playWhenReady = true // tells the exoPlayer to play media.
            setNewState(PlaybackStateCompat.STATE_PLAYING)

        }
    }

    override fun onPause() {
        if(mExoPlayer != null && (mExoPlayer as SimpleExoPlayer).playWhenReady) {
            (mExoPlayer as SimpleExoPlayer).playWhenReady = false // tells the exoPlayer to play media.
            setNewState(PlaybackStateCompat.STATE_PAUSED)

        }
    }
    override fun playFromMedia(metadata: MediaMetadataCompat?) {}
    override val currentMedia: MediaMetadataCompat?
        get() = mCurrentMedia

    override val isPlaying: Boolean
        get() = if(mExoPlayer != null) (mExoPlayer as SimpleExoPlayer).playWhenReady else false

    override fun onStop() {
        if(mExoPlayer != null) {
            setNewState(PlaybackStateCompat.STATE_STOPPED)
            release()
        }
    }
    override fun seekTo(position: Long) {
        if(mExoPlayer != null) {
            (mExoPlayer as SimpleExoPlayer).seekTo(position)
            setNewState(mState)
        }
    }
    override fun setVolume(volume: Float) {

    }

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

    private fun setNewState(@PlaybackStateCompat.State newPlayerState: Int) {
        mState = newPlayerState

        // Whether playback goes to completion, or whether it is stopped, the
        // mCurrentMediaPlayedToCompletion is set to true.
        if (mState == PlaybackStateCompat.STATE_STOPPED) {
            mCurrentMediaPlayedToCompletion = true
        }
        val reportPosition =
            if (mExoPlayer == null) 0 else mExoPlayer!!.currentPosition

        // Send playback state information to service
    }

    /**
     * Set the current capabilities available on this session. Note: If a capability is not
     * listed in the bitmask of capabilities then the MediaSession will not handle it. For
     * example, if you don't want ACTION_STOP to be handled by the MediaSession, then don't
     * included it in the bitmask that's returned.
     */
    @PlaybackStateCompat.Actions
    private fun getAvailableActions(): Long {
        var actions = (PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        actions = when (mState) {
            PlaybackStateCompat.STATE_STOPPED -> actions or (PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE)
            PlaybackStateCompat.STATE_PLAYING -> actions or (PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_SEEK_TO)
            PlaybackStateCompat.STATE_PAUSED -> actions or (PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_STOP)
            else -> actions or (PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE)
        }
        return actions
    }

    inner class ExoPlayerEventListener : Player.EventListener {
        override fun onTimelineChanged(
            timeline: Timeline,
            manifest: Any?,
            reason: Int
        ) {
        }

        override fun onTracksChanged(
            trackGroups: TrackGroupArray,
            trackSelections: TrackSelectionArray
        ) {
        }

        override fun onLoadingChanged(isLoading: Boolean) {}
        override fun onPlayerStateChanged(
            playWhenReady: Boolean,
            playbackState: Int
        ) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    setNewState(PlaybackStateCompat.STATE_PAUSED)
                }
                Player.STATE_BUFFERING -> {
                    Log.d(
                        PlayerAdapter.TAG,
                        "onPlayerStateChanged: BUFFERING"
                    )
                    mStartTime = System.currentTimeMillis()
                }
                Player.STATE_IDLE -> {
                }
                Player.STATE_READY -> {
                    Log.d(
                        PlayerAdapter.TAG,
                        "onPlayerStateChanged: READY"
                    )
                    Log.d(
                        PlayerAdapter.TAG,
                        "onPlayerStateChanged: TIME ELAPSED: " + (System.currentTimeMillis() - mStartTime)
                    )
                }
            }
        }

        override fun onRepeatModeChanged(repeatMode: Int) {}
        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {}
        override fun onPlayerError(error: ExoPlaybackException) {}
        override fun onPositionDiscontinuity(reason: Int) {}
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {}
        override fun onSeekProcessed() {}
    }


}
