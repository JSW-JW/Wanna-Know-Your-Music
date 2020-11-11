package com.vics.wanna_know_your_music.adapters

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vics.WannaKnowYourMusic.R


class PlaylistRecyclerAdapter(
    val context: Context,
    var mediaList: ArrayList<MediaMetadataCompat>,
    val iMediaSelector: IMediaSelector
) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val TAG = "PlaylistRecyclerAdapter"
    }

    private var mSelectedIndex: Int

    init {
        Log.d(TAG, "PlaylistRecyclerAdapter: called.")
        mSelectedIndex = -1
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_playlist_list_item, null)
        return PlaylistViewHolder(view, iMediaSelector)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val playlistViewHolder = viewHolder as PlaylistViewHolder
        playlistViewHolder.title.text = mediaList[i].description.title
        playlistViewHolder.artist.text = mediaList[i].description.subtitle

        if (i == mSelectedIndex) {
            viewHolder.title.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.green
                )
            )
        } else {
            viewHolder.title.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.white
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    fun setSelectedIndex(index: Int) {
        mSelectedIndex = index
        notifyDataSetChanged()
    }

    fun getIndexOfItem(mediaItem: MediaMetadataCompat): Int {
        for (i in 0 until mediaList.size) {
            if (mediaList[i].description.mediaId
                    .equals(mediaItem.description.mediaId)
            ) {
                return i
            }
        }
        return -1
    }

    class PlaylistViewHolder(
        itemView: View,
        val iMediaSelector : IMediaSelector
    ) :
        ViewHolder(itemView) {
        val title: TextView
        val artist: TextView

        init {
            title = itemView.findViewById(R.id.media_title)
            artist = itemView.findViewById(R.id.media_artist)
            itemView.setOnClickListener {
                iMediaSelector.onMediaSelected(adapterPosition)
            }
        }
    }

    interface IMediaSelector {
        fun onMediaSelected(position: Int)
    }
}
