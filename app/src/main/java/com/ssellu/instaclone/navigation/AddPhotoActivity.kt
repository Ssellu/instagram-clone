package com.ssellu.instaclone.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.ssellu.instaclone.R
import com.ssellu.instaclone.general.Constants
import com.ssellu.instaclone.navigation.model.ContentDto
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    companion object {
        const val PICK_IMAGE_FROM_ALBUM = 0

    }

    private lateinit var mButton: Button
    lateinit var mImageView: ImageView
    lateinit var mEditText: EditText

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        mButton = findViewById(R.id.btn_add_photo)
        mImageView = findViewById(R.id.iv_photo_preview)
        mEditText = findViewById(R.id.et_description)


        // Initiate Firebase storage, auth, firestore
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()


        // Open Album
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        mButton.setOnClickListener {
            contentUpload()
        }

    }

    private fun contentUpload() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMAGE_$timeStamp.png"
        val storageRef = storage?.reference?.child(Constants.FIRESTORE_PATH)?.child(imageFileName)

        // https://firebase.google.com/docs/storage/android/upload-files?hl=ko#kotlin+ktx
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageRef.downloadUrl
        }?.addOnSuccessListener {  uri ->
            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show()
            val contentDto = ContentDto().apply {
                imageUrl = uri.toString()
                uid = auth?.currentUser?.uid
                userId = auth?.currentUser?.email
                explain = mEditText.text.toString()
                timestamp = System.currentTimeMillis()
            }

            firestore?.collection(Constants.FIRESTORE_PATH)?.document()?.set(contentDto)
            setResult(Activity.RESULT_OK)
            finish()
        }?.addOnFailureListener{
            Toast.makeText(this, getString(R.string.upload_fail), Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                photoUri = data?.data
                mImageView.setImageURI(photoUri)
            } else {
                finish()
            }
        }
    }
}