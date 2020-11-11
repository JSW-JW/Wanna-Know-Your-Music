package com.vics.wanna_know_your_music.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.ViewHolders.CategoryViewHolder
import com.vics.wanna_know_your_music.models.Artist
import kotlinx.android.synthetic.main.layout_artist_list_item.view.*


class CategoryRecyclerAdapter(
    private val context: Context,
    private val artists: ArrayList<Artist>,
    private val categorySelector: ICategorySelector
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_category_list_item, null)
        return CategoryViewHolder(view, categorySelector, context, artists)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val categoryViewHolder = viewHolder as CategoryViewHolder
        categoryViewHolder.bind()

        viewHolder.itemView.setOnClickListener {
            categorySelector.onArtistSelected(position)
        }
    }

    override fun getItemCount(): Int {
        return artists.size
    }

    interface ICategorySelector {
        fun onArtistSelected(position: Int)
    }
}