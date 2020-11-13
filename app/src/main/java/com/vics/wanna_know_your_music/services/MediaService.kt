package com.vics.wanna_know_your_music.services

import android.content.Intent
import android.media.session.MediaSession
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.text.TextUtils
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.google.api.LogDescriptor
import com.vics.wanna_know_your_music.players.MediaPlayerAdapter
import com.vics.wanna_know_your_music.players.PlayerAdapter


class MediaService : MediaBrowserServiceCompat() {
    companion object {
        private const val TAG = "MediaService"
    }
    private var mSession: MediaSessionCompat? = null
    private var mPlayback: PlayerAdapter? = null

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
        mPlayback = MediaPlayerAdapter(this)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.d(TAG, "onTaskRemoved: stopped")
        super.onTaskRemoved(rootIntent)
        mPlayback?.stop()
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

        private val mPlaylist: ArrayList<MediaSessionCompat.QueueItem> = ArrayList()
        private var mPreparedMedia: MediaMetadataCompat? = null
        private var mQueueIndex = -1

        override fun onPrepare() {
            if(mQueueIndex < 0 && mPlaylist.isEmpty()) {
                return
            }

            mPreparedMedia = null // TODO: need to retrieve the selected media here.

            if((mSession?.isActive) == false) {
                mSession?.isActive = true
            }
        }

        override fun onPlayFromMediaId(mediaId: String, extras: Bundle) {
            Log.d(TAG, "onPlayFromMediaId: Called.")
        }

        override fun onPlay() {
            if(!isReadyToPlay()) {
                return
            }
            if(mPreparedMedia == null){
                onPrepare()
            }

            mPlayback?.playFromMedia(mPreparedMedia)
        }

        override fun onPause() {
            mPlayback?.pause()
        }

        override fun onSkipToNext() {
            Log.d(TAG, "onSkipToNext: SKIP TO NEXT")
            mQueueIndex = (++mQueueIndex % mPlaylist.size)
            Log.d(TAG, "onSkipToNext: queue index: $mQueueIndex")
            onPlay()
        }

        override fun onSkipToPrevious() {
            Log.d(TAG, "onSkipToPrevious: SKIP TO PREVIOUS")

            mQueueIndex = if(mQueueIndex > 0) mQueueIndex -1 else mPlaylist.size - 1
            mPreparedMedia = null
            onPlay()
        }

        override fun onStop() {
            mPlayback?.stop()
            mSession?.isActive = false
        }

        override fun onSeekTo(pos: Long) {
            mPlayback?.seekTo(pos)
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat) {
            Log.d(TAG, "onAddQueueItem: called: position in list: ${mPlaylist.size}")
            mPlaylist.add(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
            mQueueIndex = if(mQueueIndex == -1) 0 else mQueueIndex
            mSession?.setQueue(mPlaylist)
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
            Log.d(TAG, "onRemoveQueueItem: called: position in list: ${mPlaylist.size}")
            mPlaylist.remove(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
            mQueueIndex = if(mPlaylist.isEmpty()) -1 else mQueueIndex
            mSession?.setQueue(mPlaylist)
        }

        private fun isReadyToPlay(): Boolean {
            return (mPlaylist.isNotEmpty())
        }
    }

}


