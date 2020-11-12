package com.vics.wanna_know_your_music.ui

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.vics.WannaKnowYourMusic.R
import com.vics.wanna_know_your_music.adapters.PlaylistRecyclerAdapter
import com.vics.wanna_know_your_music.adapters.PlaylistRecyclerAdapter.IMediaSelector
import com.vics.wanna_know_your_music.models.Artist
import kotlinx.android.synthetic.main.fragment_home.*


class PlaylistFragment : Fragment(), IMediaSelector {

    private lateinit var mContext: Context
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: PlaylistRecyclerAdapter
    private lateinit var mIMainActivity: IMainActivity

    companion object {
        private const val TAG = "PlaylistFragment"
    }

    // UI Components


    // Vars
    private var mMediaList: ArrayList<MediaMetadataCompat> = ArrayList()
    private var mSelectedMedia: MediaMetadataCompat? = null
    private var mSelectedCategory: String? = ""
    private var mSelectedArtist: Artist? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mSelectedCategory = arguments!!.getString("category")
            mSelectedArtist = arguments!!.getParcelable("artist")
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if(!hidden) {
            if(mSelectedArtist?.title != null) {
                mIMainActivity.setActionBarTitle(mSelectedArtist?.title!!)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(mSelectedArtist?.title != null) {
            mIMainActivity.setActionBarTitle(mSelectedArtist?.title!!)
        }

        initRecyclerView()
    }

    fun newInstance(category: String?, artist: Artist?): PlaylistFragment {
        val playlistFragment = PlaylistFragment()
        val args = Bundle()
        args.putString("category", category)
        args.putParcelable("artist", artist)
        playlistFragment.arguments = args
        return playlistFragment
    }


    private fun initRecyclerView(){
        mRecyclerView = recycler_view
        mRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = PlaylistRecyclerAdapter(mContext, mMediaList, this)
        mRecyclerView.adapter = mAdapter

        if(mMediaList.size == 0) {
            retrieveMedia()
        }
    }

    private fun retrieveMedia() {
        val firestore = FirebaseFirestore.getInstance()
        if (mSelectedArtist != null && mSelectedArtist?.artist_id != null) {
            val query: Query = firestore
                .collection(mContext.resources.getString(R.string.collection_audio))
                .document(mContext.resources.getString(R.string.document_categories))
                .collection(mSelectedCategory!!)
                .document(mSelectedArtist!!.artist_id!!)
                .collection(mContext.resources.getString(R.string.collection_content))
                .orderBy(
                    mContext.resources.getString(R.string.field_date_added),
                    Query.Direction.ASCENDING
                )

            query.get().addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    for (document: QueryDocumentSnapshot in task.result!!) {
                        addToMediaList(document)
                    }
                } else {
                    Log.d(TAG, "onComplete: error getting documents ${task.result}")
                }
                updateDataSet()
            }
        }

    }

    private fun addToMediaList(document: QueryDocumentSnapshot) {

        val media = MediaMetadataCompat.Builder()
            .putString(
                MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                document.getString(getString(R.string.field_media_id))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_ARTIST,
                document.getString(getString(R.string.field_artist))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_TITLE,
                document.getString(getString(R.string.field_title))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_MEDIA_URI,
                document.getString(getString(R.string.field_media_url))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION,
                document.getString(getString(R.string.field_description))
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_DATE,
                document.getDate(getString(R.string.field_date_added)).toString()
            )
            .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, mSelectedArtist!!.image)
            .build()

        mMediaList.add(media)
    }

    private fun updateDataSet() {
        (mContext as MainActivity).hideProgressBar()
        mAdapter.notifyDataSetChanged()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mIMainActivity = context as MainActivity
    }

    override fun onMediaSelected(position: Int) {

    }

}