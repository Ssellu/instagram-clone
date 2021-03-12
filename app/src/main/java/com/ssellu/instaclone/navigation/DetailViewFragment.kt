package com.ssellu.instaclone.navigation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ssellu.instaclone.R
import com.ssellu.instaclone.navigation.model.ContentDto

class DetailViewFragment : Fragment() {


    private var firestore: FirebaseFirestore? = null

    lateinit var mRecyclerView: RecyclerView

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = LayoutInflater.from(activity).inflate(R.layout.fragment_detail, container, false)


        firestore = FirebaseFirestore.getInstance()
        mRecyclerView = view.findViewById(R.id.rv_detail)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mRecyclerView.adapter = DetailRecyclerViewAdapter()


        return view
    }


    inner class DetailRecyclerViewAdapter :
        RecyclerView.Adapter<DetailRecyclerViewAdapter.DetailViewHolder>() {
        private val mContentList: ArrayList<ContentDto> = ArrayList()
        private val uidList: ArrayList<String> = ArrayList()

        init {
            firestore?.collection(AddPhotoActivity.FIRESTORE_PATH)?.orderBy("timestamp")
                ?.addSnapshotListener { value, _ ->
                    mContentList.clear()
                    uidList.clear()

                    value!!.documents.forEach {
                        uidList.add(it.id)
                        mContentList.add(it.toObject(ContentDto::class.java)!!)
                    }

                    notifyDataSetChanged()
                }
        }

        inner class DetailViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val userImageView: ImageView = view.findViewById(R.id.iv_user)
            val usernameTextView: TextView = view.findViewById(R.id.tv_username)
            val mainImageView: ImageView = view.findViewById(R.id.iv_main)
            val favoriteImageView: ImageView = view.findViewById(R.id.iv_favorite)
            val commentImageView: ImageView = view.findViewById(R.id.iv_comment)
            val favoriteCountTextView: TextView = view.findViewById(R.id.tv_favorite_count)
            val contentTextView: TextView = view.findViewById(R.id.tv_content)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
            return DetailViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mContentList.size
        }

        override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
            holder.apply {
                usernameTextView.text = mContentList[position].userId
                Glide.with(itemView.context).load(mContentList[position].imageUrl)
                    .into(mainImageView)
                favoriteCountTextView.text = "Likes ${mContentList[position].favoriteCount}"
                contentTextView.text = mContentList[position].explain
                // TODO  Glide.with(itemView.context).load(mContentList[position].imageUrl).into(userImageView)
                favoriteImageView.setOnClickListener { toggleFavorite(holder, position) }

                toggleFavoriteImage(this, position)

            }
        }

        private fun toggleFavoriteImage(holder: DetailViewHolder, position: Int) {
            holder.favoriteImageView.setImageResource(
                if (mContentList[position].favorites.containsKey(uid))
                    R.drawable.ic_heart_solid
                else
                    R.drawable.ic_heart_border
            )
        }

        private fun toggleFavorite(holder: DetailViewHolder, position: Int) {
            val tsDoc =
                firestore?.collection(AddPhotoActivity.FIRESTORE_PATH)?.document(uidList[position])
            firestore?.runTransaction {

                val contentDto = it.get(tsDoc!!).toObject(ContentDto::class.java)

                if (contentDto!!.favorites.containsKey(uid)) {
                    // when the button has been clicked already
                    contentDto.favorites.remove(uid)
                    contentDto.favoriteCount -= 1
                } else {
                    contentDto.favorites[uid!!] = true
                    contentDto.favoriteCount += 1
                }
                it.set(tsDoc, contentDto)
            }?.addOnSuccessListener {
                toggleFavoriteImage(holder, position)
            }
        }
    }
}