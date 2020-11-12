package com.vics.wanna_know_your_music.services

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.annotation.Nullable
import androidx.media.MediaBrowserServiceCompat


class MediaService : MediaBrowserServiceCompat() {

    companion object {
        private const val TAG = "MediaService"
    }
    private val mSession: MediaSessionCompat? = null

    @Nullable
    override fun onGetRoot(
        s: String,
        i: Int,
        @Nullable bundle: Bundle?
    ): BrowserRoot? {
        return null
    }

    override fun onLoadChildren(
        s: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        override fun onPrepare() {
            super.onPrepare()
        }

        override fun onPlayFromMediaId(mediaId: String, extras: Bundle) {
            super.onPrepareFromMediaId(mediaId, extras)
        }

        override fun onPlay() {
            super.onPlay()
        }

        override fun onPause() {
            super.onPause()
        }

        override fun onSkipToNext() {
            super.onSkipToNext()
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
        }

        override fun onStop() {
            super.onStop()
        }

        override fun onSeekTo(pos: Long) {
            super.onSeekTo(pos)
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat) {
            super.onAddQueueItem(description)
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
            super.onRemoveQueueItem(description)
        }
    }

}





