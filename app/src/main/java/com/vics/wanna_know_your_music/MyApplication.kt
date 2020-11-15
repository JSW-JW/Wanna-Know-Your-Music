package com.vics.wanna_know_your_music

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import java.util.*
import kotlin.collections.ArrayList


class MyApplication : Application() {
    private val mMediaItems: MutableList<MediaBrowserCompat.MediaItem> =
        ArrayList()
    private val mTreeMap: TreeMap<String, MediaMetadataCompat> = TreeMap()
    val mediaItems: List<MediaBrowserCompat.MediaItem>
        get() = mMediaItems

    val treeMap: TreeMap<String, MediaMetadataCompat>
        get() = mTreeMap

    fun setMediaItems(mediaItems: List<MediaMetadataCompat>) {
        mMediaItems.clear()
        for (item in mediaItems) {
            Log.d(
                TAG,
                "setMediaItems: called: adding media item: " + item.description
            )
            mMediaItems.add(
                MediaBrowserCompat.MediaItem(
                    item.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
                )
            )
            item.description.mediaId?.let { mTreeMap.put(it, item) }
        }
    }

    fun getMediaItem(mediaId: String?): MediaMetadataCompat? {
        return mTreeMap[mediaId]
    }

    companion object {
        private const val TAG = "MyApplication"
        private var mInstance: MyApplication? = null
        val instance: MyApplication?
            get() {
                if (mInstance == null) {
                    mInstance = MyApplication()
                }
                return mInstance
            }
    }
}