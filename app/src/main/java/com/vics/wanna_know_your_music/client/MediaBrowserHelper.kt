package com.vics.wanna_know_your_music.client

import android.content.ComponentName
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import java.lang.RuntimeException


class MediaBrowserHelper(
    context: Context,
    serviceClass: Class<out MediaBrowserServiceCompat?>
) {
    companion object {
        private const val TAG = "MediaBrowserHelper"
    }
    private val mContext: Context
    private val mMediaBrowserServiceClass: Class<out MediaBrowserServiceCompat?>
    private var mMediaBrowser: MediaBrowserCompat? = null
    private var mMediaController: MediaControllerCompat? = null
    private val mMediaBrowserConnectionCallback: MediaBrowserConnectionCallback
    private val mMediaBrowserSubscriptionCallback: MediaBrowserSubscriptionCallback
    init {
        mContext = context
        mMediaBrowserServiceClass = serviceClass
        mMediaBrowserConnectionCallback = MediaBrowserConnectionCallback()
        mMediaBrowserSubscriptionCallback = MediaBrowserSubscriptionCallback()
    }

    fun onStart() {
        if (mMediaBrowser == null) {
            mMediaBrowser = MediaBrowserCompat(
                mContext,
                ComponentName(mContext, mMediaBrowserServiceClass),
                mMediaBrowserConnectionCallback,
                null
            )
            mMediaBrowser!!.connect()
        }
        Log.d(
            TAG,
            "onStart: CALLED: Creating MediaBrowser, and connecting"
        )
    }

    fun onStop() {
        if (mMediaController != null) {
            mMediaController = null
        }
        if (mMediaBrowser != null && mMediaBrowser!!.isConnected) {
            mMediaBrowser!!.disconnect()
            mMediaBrowser = null
        }
        Log.d(
            TAG,
            "onStop: CALLED: Releasing MediaController, Disconnecting from MediaBrowser"
        )
    }

    // Receives callbacks from the MediaBrowser when it has successfully connected to the
    // MediaBrowserService (MusicService).
    private inner class MediaBrowserConnectionCallback :
        MediaBrowserCompat.ConnectionCallback() {
        // Happens as a result of onStart().
        override fun onConnected() {
            Log.d(TAG, "onConnected: CALLED")
            mMediaController = try {
                // Get a MediaController for the MediaSession.
                MediaControllerCompat(mContext, mMediaBrowser!!.sessionToken)
            } catch (e: RemoteException) {
                Log.d(
                    TAG,
                    java.lang.String.format("onConnected: Problem: %s", e.toString())
                )
                throw RuntimeException(e)
            }
            mMediaBrowser!!.subscribe(mMediaBrowser!!.root, mMediaBrowserSubscriptionCallback)
            Log.d(
                TAG,
                "onConnected: CALLED: subscribing to: " + mMediaBrowser!!.root
            )
        }
    }

    // Receives callbacks from the MediaBrowser when the MediaBrowserService has loaded new media
    // that is ready for playback.
    inner class MediaBrowserSubscriptionCallback :
        MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(
            parentId: String,
            children: List<MediaBrowserCompat.MediaItem>
        ) {
            Log.d(
                TAG,
                "onChildrenLoaded: CALLED: $parentId, $children"
            )
            for (mediaItem in children) {
                Log.d(
                    TAG,
                    "onChildrenLoaded: CALLED: queue item: " + mediaItem.mediaId
                )
                mMediaController!!.addQueueItem(mediaItem.description)
            }
        }
    }

}

