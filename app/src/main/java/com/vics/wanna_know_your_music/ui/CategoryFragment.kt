package com.vics.wanna_know_your_music.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.CategoryRecyclerAdapter
import com.vics.wanna_know_your_music.adapters.CategoryRecyclerAdapter.*
import com.vics.wanna_know_your_music.models.Artist


class CategoryFragment : Fragment(), ICategorySelector {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CategoryRecyclerAdapter
    private lateinit var mIMainActivity: IMainActivity
    private lateinit var mContext: Context

    companion object {
        private const val TAG = "CategoryFragment"
    }

    private var mICategorySelector: ICategorySelector = this

    // UI Components

    // Vars
    private var mArtists: ArrayList<Artist> = ArrayList()
    private var mSelectedCategory: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mSelectedCategory = arguments!!.getString("category")
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden) {
            mIMainActivity.setActionBarTitle(mSelectedCategory!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_home,
            container,
            false
        )  // 3 Fragment use the same 'fragment_home' layout. But name each of them differently for clarification.
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mIMainActivity.setActionBarTitle(mSelectedCategory!!)
        initRecyclerView(view)
    }

    private fun retrieveArtists() {
        mIMainActivity.showProgressBar()

        val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

        val query = mSelectedCategory?.let {
            firestore
                .collection(mContext.resources.getString(R.string.collection_audio))
                .document(mContext.resources.getString(R.string.document_categories))
                .collection(it)
        }

        query?.get()?.addOnCompleteListener { task ->
            if(task.isSuccessful && task.result != null) {
                for(queryDocumentSnapshot: DocumentSnapshot in task.result!!) {
                    val artist = queryDocumentSnapshot.toObject(Artist::class.java)
                    artist?.let {
                        mArtists.add(artist!!)
                    }
                }
            }
            else {
                Log.d(TAG, "retrieveArtists: error getting documents." + task.exception)
            }
            updateDataSet()
        }

    }

    private fun updateDataSet(){
        mIMainActivity.hideProgressBar()
        mAdapter.notifyDataSetChanged()
    }

    fun newInstance(category: String): CategoryFragment {
        val categoryFragment = CategoryFragment()
        val args = Bundle()
        args.putString("category", category)
        categoryFragment.arguments = args
        return categoryFragment
    }

    private fun initRecyclerView(view: View) {
        mRecyclerView = view.findViewById(R.id.recycler_view)
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = CategoryRecyclerAdapter(mContext, mArtists, mICategorySelector)
        mRecyclerView.adapter = mAdapter
        retrieveArtists()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mIMainActivity = context as IMainActivity
    }

    override fun onArtistSelected(position: Int) {
        mSelectedCategory?.let { selectedCategory ->
            mIMainActivity.onArtistSelected(selectedCategory, mArtists[position])
        }
    }
}