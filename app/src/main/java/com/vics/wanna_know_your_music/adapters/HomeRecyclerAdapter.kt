package com.vics.wanna_know_your_music.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.ViewHolders.HomeViewHolder


class HomeRecyclerAdapter(
    var categories: ArrayList<String>,
    val context: Context,
    val homeSelector: IHomeSelector
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_category_list_item, viewGroup, false)
        return HomeViewHolder(view, homeSelector, context, categories[i])
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when(viewHolder) {
            is HomeViewHolder -> {
                viewHolder.bind()
            }
        }

        viewHolder.itemView.setOnClickListener {
            homeSelector.onCategorySelected(position)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    interface IHomeSelector {
        fun onCategorySelected(position: Int)
    }
}
