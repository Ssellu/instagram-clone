package com.ssellu.instaclone

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ssellu.instaclone.general.Constants
import com.ssellu.instaclone.general.hideSoftKeyboard
import com.ssellu.instaclone.navigation.model.ContentDto

// TODO 2
class CommentActivity : AppCompatActivity() {

    private var contentUid: String? = null

    private lateinit var commentMessageEditText: EditText
    private lateinit var commentSubmitButton: Button
    private lateinit var commentRecyclerView: RecyclerView

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)



        contentUid = intent.getStringExtra(Constants.CONTENT_UID)

        commentMessageEditText = findViewById(R.id.et_comment_message)
        commentRecyclerView = findViewById(R.id.rv_comment)
        commentSubmitButton = findViewById(R.id.btn_comment_submit)

        commentSubmitButton.setOnClickListener {
            val comment = ContentDto.Comment().apply {
                userId = FirebaseAuth.getInstance().currentUser?.email
                uid = FirebaseAuth.getInstance().currentUser?.uid
                comment = commentMessageEditText.text.toString()
                timestamp = System.currentTimeMillis()
            }
            FirebaseFirestore.getInstance()
                .collection(Constants.FIRESTORE_PATH).document(contentUid!!)
                .collection(Constants.FIRESTORE_COMMENT_PATH).document().set(comment)
            commentMessageEditText.setText("")
            hideSoftKeyboard()
        }

        commentRecyclerView.adapter = CommentRecyclerViewAdapter()
        commentRecyclerView.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
            reverseLayout = true
        }


//        commentMessageEditText.setOnTouchListener { _, _ ->
//            true.apply {
//
//                with(commentRecyclerView) { scrollToPosition(adapter?.itemCount!! - 1) }
//            }
//        }
    }


    // TODO 4 - Firebase 확인해보기
    // TODO 5 - 댓글 목록 만들기
    inner class CommentRecyclerViewAdapter :
        RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder>() {

        private val mCommentList: ArrayList<ContentDto.Comment> = ArrayList()

        init {
            FirebaseFirestore.getInstance()
                .collection(Constants.FIRESTORE_PATH).document(contentUid!!)
                .collection(Constants.FIRESTORE_COMMENT_PATH).orderBy("timestamp")
                .addSnapshotListener { value, _ ->
                    mCommentList.clear()
                    if (value == null) return@addSnapshotListener
                    value.forEach {
                        mCommentList.add(it.toObject(ContentDto.Comment::class.java))
                    }
                }
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
            return CommentViewHolder(view)
        }

        override fun getItemCount(): Int {
            return mCommentList.size
        }

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {

            holder.commentTextView.text = mCommentList[position].comment
            holder.userEmailTextView.text = mCommentList[position].userId

            FirebaseFirestore.getInstance()
                .collection(Constants.FIRESTORE_PROFILE_IMAGE_PATH)
                .document(mCommentList[position].uid!!)
                .get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        Glide.with(holder.itemView.context).load(
                            it.result!!["image"] ?: R.drawable.ic_user_circle_solid
                        )
                            .apply(RequestOptions().circleCrop()).into(holder.profileImageView)
                    }

                }

        }

        inner class CommentViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            var userEmailTextView: TextView = v.findViewById(R.id.tv_user_email)
            var commentTextView: TextView = v.findViewById(R.id.tv_comment)
            var profileImageView: ImageView = v.findViewById(R.id.iv_profile)
        }
    }
}