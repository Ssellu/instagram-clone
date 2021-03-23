package com.ssellu.instaclone.navigation

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.ssellu.instaclone.LoginActivity
import com.ssellu.instaclone.MainActivity
import com.ssellu.instaclone.R
import com.ssellu.instaclone.general.Constants
import com.ssellu.instaclone.navigation.model.ContentDto

class UserFragment : Fragment() {


    private lateinit var fragmentView: View
    private var firestore: FirebaseFirestore? = null
    private var targetUid: String? = null
    private var targetEmail: String? = null
    private var auth: FirebaseAuth? = null

    lateinit var userImageView: ImageView
    lateinit var gridRecyclerView: RecyclerView
    lateinit var postCountTextView: TextView
    lateinit var followerCountTextView: TextView
    lateinit var followingCountTextView: TextView
    lateinit var followButton: TextView
    lateinit var userEmailTextView: TextView

    var currentUid: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        targetUid = arguments?.getString(Constants.TARGET_USER_UID_FOR_DETAIL_PAGE)
        targetEmail = arguments?.getString(Constants.TARGET_USER_EMAIL_FOR_DETAIL_PAGE)
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        fragmentView =
            LayoutInflater.from(activity).inflate(R.layout.fragment_user, container, false)

        userImageView = fragmentView.findViewById(R.id.iv_user_image)

        postCountTextView = fragmentView.findViewById(R.id.tv_post_count)

        followerCountTextView = fragmentView.findViewById(R.id.tv_follower_count)

        followingCountTextView = fragmentView.findViewById(R.id.tv_following_count)

        followButton = fragmentView.findViewById(R.id.btn_follow)
        userEmailTextView = fragmentView.findViewById(R.id.tv_user_email_user)

        gridRecyclerView = fragmentView.findViewById(R.id.rv_grid)
        gridRecyclerView.layoutManager = GridLayoutManager(activity, 3)
        gridRecyclerView.adapter = UserFragmentRecyclerViewAdapter()




        userEmailTextView.text = targetEmail


        val mainActivity = (activity as MainActivity).apply {
            userEmailTextView.visibility = View.VISIBLE
            backImageView.visibility = View.VISIBLE
            backImageView.setOnClickListener {
                this.bottomNavigationView.selectedItemId = R.id.action_home
            }
        }

        currentUid = auth?.currentUser?.uid
        if (targetUid == currentUid) { // When user opens this page of himself
            followButton.text = getString(R.string.signout)  // sign out
            followButton.setOnClickListener {
                activity?.finish()
                startActivity(Intent(activity, LoginActivity::class.java))
                auth?.signOut()
            }
        } else { // opens page of the other
            followButton.let {
                it.text = getString(R.string.follow)  // follow
                it.setOnClickListener {
                    mainActivity.bottomNavigationView.selectedItemId = R.id.action_home
                }
            }
        }
        //////////////////////////////////////////

        return fragmentView
    }

    inner class UserFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val contentDtoList: ArrayList<ContentDto> = arrayListOf()

        init {
            Log.d("My", "UID : $targetUid / post : ${contentDtoList.size}")
            firestore?.collection(Constants.FIRESTORE_PATH)?.whereEqualTo("uid", targetUid)
                ?.addSnapshotListener { value, _ ->

                    if (value == null) return@addSnapshotListener
                    value.forEach {
                        contentDtoList.add(it.toObject(ContentDto::class.java))
                    }
                    postCountTextView.text = contentDtoList.size.toString()


                    notifyDataSetChanged()
                }
        }

        inner class UserViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

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

    override fun onDetach() {
        super.onDetach()
        (activity as MainActivity).setToolbarDefault()
    }
}