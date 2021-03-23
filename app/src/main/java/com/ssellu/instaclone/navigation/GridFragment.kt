package com.ssellu.instaclone.navigation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.ssellu.instaclone.R
import com.ssellu.instaclone.general.Constants
import com.ssellu.instaclone.navigation.model.ContentDto

class GridFragment : Fragment() {
    private var firestore: FirebaseFirestore? = null
    private var fragment: View? = null

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragment = LayoutInflater.from(activity).inflate(R.layout.fragment_grid, container, false)
        firestore = FirebaseFirestore.getInstance()
        recyclerView = fragment?.findViewById(R.id.rv_grid)!!
        recyclerView.adapter = GridFragmentRecyclerViewAdapter()
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        return fragment
    }

    inner class GridFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val contentDtoList: ArrayList<ContentDto> = arrayListOf()

        init {
            firestore?.collection(Constants.FIRESTORE_PATH)?.addSnapshotListener { value, _ ->
                if (value == null) return@addSnapshotListener
                value.forEach {
                    contentDtoList.add(it.toObject(ContentDto::class.java))
                }
                Log.d("my", "$contentDtoList")
                notifyDataSetChanged()
            }
        }

        inner class GridViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3
            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return GridViewHolder(imageView)
        }

        override fun getItemCount(): Int {
            return contentDtoList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val imageView = (holder as GridViewHolder).imageView
            Glide
                .with(imageView.context)
                .load(contentDtoList[position].imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageView)
        }
    }
}