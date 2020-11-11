package com.vics.wanna_know_your_music.adapters.ViewHolders

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.HomeRecyclerAdapter

class HomeViewHolder(
    itemView: View,
    homeSelector: HomeRecyclerAdapter.IHomeSelector,
    val context: Context,
    val categories: ArrayList<String>
) : RecyclerView.ViewHolder(itemView) {

    private val  categoryText : TextView = itemView.findViewById(R.id.category_title)
    private val category_icon : ImageView = itemView.findViewById(R.id.category_icon)
    private val iHomeSelector: HomeRecyclerAdapter.IHomeSelector = homeSelector

    init {
        itemView.setOnClickListener {
            iHomeSelector.onCategorySelected(adapterPosition)
        }
    }

    fun bind() {
        categoryText.text = categories[adapterPosition]
        val options: RequestOptions = RequestOptions()
            .error(R.drawable.ic_launcher_background)
        var iconResource: Drawable? = null
        when (categories[adapterPosition]) {
            "Music" -> {
                iconResource =
                    ContextCompat.getDrawable(context, R.drawable.ic_audiotrack_white_24dp)
            }
            "Podcasts" -> {
                iconResource = ContextCompat.getDrawable(context, R.drawable.ic_mic_white_24dp)
            }
        }
        Glide.with(context)
            .setDefaultRequestOptions(options)
            .load(iconResource)
            .into(category_icon)
    }
}