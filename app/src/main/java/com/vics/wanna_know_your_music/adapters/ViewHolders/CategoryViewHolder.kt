package com.vics.wanna_know_your_music.adapters.ViewHolders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.CategoryRecyclerAdapter
import com.vics.wanna_know_your_music.models.Artist

class CategoryViewHolder(
    itemView: View,
    private val categorySelector: CategoryRecyclerAdapter.ICategorySelector,
    private val context: Context,
    private val artists: ArrayList<Artist>
) :
    RecyclerView.ViewHolder(itemView) {

    val title: TextView = itemView.findViewById(R.id.category_title)
    val image: ImageView = itemView.findViewById(R.id.category_icon)

    init {
        itemView.setOnClickListener {
            categorySelector.onArtistSelected(adapterPosition)
        }
    }

    fun bind() {

        title.text = artists[adapterPosition].title

        val options: RequestOptions = RequestOptions()
            .error(R.drawable.ic_launcher_background)
        Glide.with(context)
            .setDefaultRequestOptions(options)
            .load(artists[adapterPosition].image)
            .into(image)
    }
}