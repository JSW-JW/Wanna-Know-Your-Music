package com.vics.wanna_know_your_music.services

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.TextUtils
import android.util.Log
import androidx.media.MediaBrowserServiceCompat


class MediaService : MediaBrowserServiceCompat() {
    companion object {
        private const val TAG = "MediaService"
    }
    private var mSession: MediaSessionCompat? = null
    override fun onCreate() {
        super.onCreate()

        //Build the MediaSession
        mSession = MediaSessionCompat(this, TAG)
        mSession!!.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or  // https://developer.android.com/guide/topics/media-apps/mediabuttons#mediabuttons-and-active-mediasessions
                    // Media buttons on the device
                    // (handles the PendingIntents for MediaButtonReceiver.buildMediaButtonPendingIntent)
                    MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS
        ) // Control the items in the queue (aka playlist)
        // See https://developer.android.com/guide/topics/media-apps/mediabuttons for more info on flags
        mSession!!.setCallback(MediaSessionCallback())

        // A token that can be used to create a MediaController for this session
        sessionToken = mSession!!.sessionToken
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d(TAG, "onTaskRemoved: stopped")
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        mSession!!.release()
        Log.d(
            TAG,
            "onDestroy: MediaPlayerAdapter stopped, and MediaSession released"
        )
    }

    override fun onGetRoot(
        s: String,
        i: Int,
        bundle: Bundle?
    ): BrowserRoot? {
        if (s == applicationContext.packageName) {
            // Allowed to browse media
        }
        return BrowserRoot("empty_media", null) // return no media
    }

    override fun onLoadChildren(
        s: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        Log.d(TAG, "onLoadChildren: called: $s, $result")

        //  Browsing not allowed
        if (TextUtils.equals("empty_media", s)) {
            result.sendResult(null)
            return
        }
        result.sendResult(null) // return all available media
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


