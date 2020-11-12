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
import com.google.android.gms.dynamic.SupportFragmentWrapper
import com.google.api.LogDescriptor
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.HomeRecyclerAdapter


class HomeFragment : Fragment(), HomeRecyclerAdapter.IHomeSelector {

    private lateinit var mContext: Context
    private lateinit var mAdapter: HomeRecyclerAdapter
    private lateinit var mIMainActivity: IMainActivity

    companion object {
        private const val TAG = "HomeFragment"
    }

    // UI Components
    private var mRecyclerView: RecyclerView? = null

    // Vars
    private var mCategories: ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView(view)
        Log.d(TAG, "onViewCreated: here")
    }

    private fun retrieveCategories() {
        Log.d(TAG, "retrieveCategories: here")
        mIMainActivity.showProgressBar()

        val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
        val ref: DocumentReference =
            fireStore.collection(context?.resources!!.getString(R.string.collection_audio))
                .document(context?.resources!!.getString(R.string.document_categories))

        ref.get().addOnCompleteListener {
            Log.d(TAG, "retrieveCategories: here")
            if (it.isSuccessful) {
                val doc = it.result
                Log.d(TAG, "retrieveCategories: $doc")
                var categoriesMap: HashMap<String, String> =
                    (doc?.data?.get("categories") as HashMap<String, String>)
                mCategories.addAll(categoriesMap.keys)
            }
            mIMainActivity.hideProgressBar()
            mAdapter.notifyDataSetChanged()
        }
    }

    fun newInstance(): HomeFragment {
        return HomeFragment()
    }

    private fun initRecyclerView(view: View) {
        if (mRecyclerView == null) {
            Log.d(TAG, "initRecyclerView: here")
            mRecyclerView = view?.findViewById(R.id.recycler_view)
            mRecyclerView?.layoutManager = LinearLayoutManager(activity)
            mAdapter = HomeRecyclerAdapter(mCategories, mContext, this)
            mRecyclerView!!.adapter = mAdapter
            retrieveCategories()
        }
    }

    override fun onCategorySelected(position: Int) {
        Log.d(TAG, "onCategorySelected: Category list item is clicked.")
        mIMainActivity.onCategorySelected(mCategories[position])
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mIMainActivity = activity as IMainActivity
    }

}