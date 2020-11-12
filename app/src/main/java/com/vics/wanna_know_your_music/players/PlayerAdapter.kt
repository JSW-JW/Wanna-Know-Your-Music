package com.vics.wanna_know_your_music.players

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Handler
import android.support.v4.media.MediaMetadataCompat
import android.util.Log

/**
 * Abstract player implementation that handles playing music with proper handling of headphones
 * and audio focus.
 */
// ****************************************************************************************
//  This is a BASE class (abstract) that we can use to make simplify
//  controlling the MediaPlayer of choice (in this case that's an ExoPlayer)
// ****************************************************************************************

abstract class PlayerAdapter(context: Context) {
    private val mApplicationContext: Context
    private val mAudioManager: AudioManager
    private val mAudioFocusHelper: AudioFocusHelper
    private var mPlayOnAudioFocus = false

    init {
        mApplicationContext = context.applicationContext
        mAudioManager = mApplicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioFocusHelper = AudioFocusHelper()
    }

    /**
     * Public methods to handle the NOISY broadcast and AudioFocus
     */
    fun play() {
        if (mAudioFocusHelper.requestAudioFocus()) {
            registerAudioNoisyReceiver()
            onPlay()
        }
    }

    fun stop() {
        mAudioFocusHelper.abandonAudioFocus()
        unregisterAudioNoisyReceiver()
        onStop()
    }

    fun pause() {
        if (!mPlayOnAudioFocus) {
            mAudioFocusHelper.abandonAudioFocus()
        }
        unregisterAudioNoisyReceiver()
        onPause()
    }

    /**
     * Abstract methods for responding to playback changes in the class that extends this one
     */
    protected abstract fun onPlay()
    protected abstract fun onPause()
    abstract fun playFromMedia(metadata: MediaMetadataCompat?)
    abstract val currentMedia: MediaMetadataCompat?
    abstract val isPlaying: Boolean

    protected abstract fun onStop()
    abstract fun seekTo(position: Long)
    abstract fun setVolume(volume: Float)

    companion object {
        private const val TAG = "PlayerAdapter"
        private const val MEDIA_VOLUME_DEFAULT = 1.0f
        private const val MEDIA_VOLUME_DUCK = 0.2f

        /**
         * NOISY broadcast receiver stuff
         */
        private val AUDIO_NOISY_INTENT_FILTER =
            IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY) // pause the playing if device unplugged(e.g when unplugged earphone)
    }

    private var mAudioNoisyReceiverRegistered = false
    private val mAudioNoisyReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                if (isPlaying) {
                    pause()
                }
            }
        }
    }

    private fun registerAudioNoisyReceiver() {
        if (!mAudioNoisyReceiverRegistered) {
            mApplicationContext.registerReceiver(
                mAudioNoisyReceiver,
                AUDIO_NOISY_INTENT_FILTER
            )
            mAudioNoisyReceiverRegistered = true
        }
    }

    private fun unregisterAudioNoisyReceiver() {
        if (mAudioNoisyReceiverRegistered) {
            mApplicationContext.unregisterReceiver(mAudioNoisyReceiver)
            mAudioNoisyReceiverRegistered = false
        }
    }

    /**
     * Helper class for managing audio focus related tasks.
     */
    private inner class AudioFocusHelper : OnAudioFocusChangeListener {


        /*val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_GAME)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(afChangeListener, handler)
            build()
        }*/

        val handler = Handler()  // TODO: set handler config later

        fun requestAudioFocus(): Boolean {
            var requestFocus: AudioFocusRequest? = null
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                requestFocus = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                    setAudioAttributes(AudioAttributes.Builder().run {
                        setUsage(AudioAttributes.USAGE_GAME)
                        setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        build()
                    })
                    setAcceptsDelayedFocusGain(true)
                    setOnAudioFocusChangeListener(this@AudioFocusHelper, handler)
                    build()
                }
            }

            var result: Int? = 0
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                result = mAudioManager.requestAudioFocus(
                    requestFocus!!
                )
            }
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O){
                result = mAudioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            }
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }

        fun abandonAudioFocus() {
            mAudioManager.abandonAudioFocus(this)
        }

        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_GAIN")
                    if (mPlayOnAudioFocus && !isPlaying) {
                        play()
                    } else if (isPlaying) {
                        setVolume(MEDIA_VOLUME_DEFAULT)
                    }
                    mPlayOnAudioFocus = false
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    Log.d(
                        TAG,
                        "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK"
                    )
                    setVolume(MEDIA_VOLUME_DUCK)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    Log.d(
                        TAG,
                        "onAudioFocusChange: AUDIOFOCUS_LOSS_TRANSIENT"
                    )
                    if (isPlaying) {
                        mPlayOnAudioFocus = true
                        pause()
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS -> {

                    Log.d(TAG, "onAudioFocusChange: AUDIOFOCUS_LOSS")
                    mAudioManager.abandonAudioFocus(this)
                    mPlayOnAudioFocus = false
                    //                    stop(); // stop will 'hard-close' everything
                    pause()
                }
            }
        }
    }


}

