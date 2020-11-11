package com.vics.wanna_know_your_music.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.ViewHolders.HomeViewHolder


class HomeRecyclerAdapter(
    private var categories: ArrayList<String>,
    private val context: Context,
    private val homeSelector: IHomeSelector
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "HomeRecyclerAdapter"

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.layout_category_list_item, null)
        Log.d(TAG, "onCreateViewHolder: ${categories}")
        return HomeViewHolder(view, homeSelector, context, categories)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        (viewHolder as HomeViewHolder).bind()

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
