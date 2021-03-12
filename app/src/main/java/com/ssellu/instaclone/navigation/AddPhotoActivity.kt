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
import com.google.firebase.storage.FirebaseStorage
import com.ssellu.instaclone.R
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {

    companion object {
        private const val PICK_IMAGE_FROM_ALBUM = 0
        private const val IMAGE_PATH = "images"
    }

    private lateinit var mButton: Button
    lateinit var mImageView: ImageView
    lateinit var mEditText: EditText


    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        mButton = findViewById(R.id.btn_add_photo)
        mImageView = findViewById(R.id.iv_photo_preview)
        mEditText = findViewById(R.id.et_description)


        // Initiate Storage
        storage = FirebaseStorage.getInstance()

        // Open Album
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        mButton.setOnClickListener{
            contentUpload()
        }

    }

    private fun contentUpload() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "IMAGE_$timeStamp.png"
        val storageRef = storage?.reference?.child(IMAGE_PATH)?.child(imageFileName)

        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                photoUri = data?.data
                mImageView.setImageURI(photoUri)
            }
            else{
                finish()
            }
        }
    }
}