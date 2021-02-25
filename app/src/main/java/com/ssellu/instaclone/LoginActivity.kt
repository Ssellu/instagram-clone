package com.ssellu.instaclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.lang.IllegalArgumentException

class LoginActivity : AppCompatActivity() {

    lateinit var mEmailEditText: EditText
    lateinit var mPasswordEditText: EditText
    lateinit var mLoginButton: Button
    lateinit var mGoogleButton: FloatingActionButton
    lateinit var mFacebookButton: FloatingActionButton
    lateinit var mProgressBar: ProgressBar

    var auth:FirebaseAuth? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mEmailEditText = findViewById(R.id.et_email)
        mPasswordEditText = findViewById(R.id.et_password)
        mLoginButton = findViewById(R.id.btn_login)
        mGoogleButton = findViewById(R.id.btn_google)
        mFacebookButton = findViewById(R.id.btn_facebook)
        mProgressBar = findViewById(R.id.pb_login)


        auth = FirebaseAuth.getInstance()

        mLoginButton.setOnClickListener{

            signInAndSignUp(mEmailEditText.text.toString(), mPasswordEditText.text.toString())

        }

    }

    private fun signInAndSignUp(email:String, password:String) {
        mProgressBar.visibility = View.VISIBLE
        auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener{

            try {
                when {
                    it.isSuccessful -> startMainActivity()
                    it.exception!!.message!!.isNotEmpty() -> signIn(email, password)
                    else -> throw Exception(it.exception!!.message)
                }
            } catch(e:Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
            mProgressBar.visibility = View.GONE
        }
    }

    private fun signIn(email: String, password: String) {
        mProgressBar.visibility = View.VISIBLE
        auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener{

            try {
                when {
                    it.isSuccessful -> startMainActivity()
                    else -> throw Exception(it.exception!!.message)
                }
            } catch(e:Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
            mProgressBar.visibility = View.GONE
        }

    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }




}