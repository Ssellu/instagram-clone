package com.ssellu.instaclone.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ssellu.instaclone.R
import com.ssellu.instaclone.general.Consts
import com.ssellu.instaclone.navigation.model.ContentDto

class UserFragment :Fragment(){

    private lateinit var fragmentView : View
    private var firestore: FirebaseFirestore? = null
    private var uid:String? = null
    private var auth:FirebaseAuth? = null

    lateinit var userImageView : ImageView
    lateinit var gridRecyclerView: RecyclerView
    lateinit var postCountTextView: TextView
    lateinit var followerCountTextView:TextView
    lateinit var followingCountTextView:TextView
    lateinit var followButton:TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        uid = arguments?.getString(Consts.AUTHENTICATED_UID)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        fragmentView = LayoutInflater.from(activity).inflate(R.layout.fragment_user, container,false)

        userImageView = fragmentView.findViewById(R.id.iv_user_image)

        postCountTextView =fragmentView.findViewById(R.id.tv_post_count)

        followerCountTextView = fragmentView.findViewById(R.id.tv_follower_count)

        followingCountTextView = fragmentView.findViewById(R.id.tv_following_count)

        followButton = fragmentView.findViewById(R.id.btn_follow)

        gridRecyclerView = fragmentView.findViewById(R.id.rv_grid)
        gridRecyclerView.adapter = UserFragmentRecyclerViewAdapter()
        gridRecyclerView.layoutManager = GridLayoutManager(activity, 3)

        return fragmentView
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

        private val contentDtoList : ArrayList<ContentDto> = arrayListOf()
        init{
            firestore?.collection(AddPhotoActivity.FIRESTORE_PATH)?.whereEqualTo("uid", uid)?.addSnapshotListener{
                value, error ->
                if(value == null) return@addSnapshotListener
                value.forEach {
                    contentDtoList.add(it.toObject(ContentDto::class.java))
                }
                postCountTextView.text = contentDtoList.size.toString()
                notifyDataSetChanged()
            }
        }

        inner class UserViewHolder(val imageView:ImageView) : RecyclerView.ViewHolder(imageView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val width = resources.displayMetrics.widthPixels / 3
            val imageView = ImageView(parent.context)
            imageView.layoutParams = LinearLayoutCompat.LayoutParams(width, width)
            return UserViewHolder(imageView)
        }

        override fun getItemCount(): Int {
            return contentDtoList.size
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val imageView = (holder as UserViewHolder).imageView
            Glide
                .with(imageView.context)
                .load(contentDtoList[position].imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(imageView)
        }
    }
}