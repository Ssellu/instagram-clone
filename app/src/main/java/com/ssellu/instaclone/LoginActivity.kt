package com.ssellu.instaclone

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    lateinit var mEmailEditText: EditText
    lateinit var mPasswordEditText: EditText
    lateinit var mLoginButton: Button
    lateinit var mGoogleButton: FloatingActionButton
    lateinit var mFacebookButton: FloatingActionButton
    lateinit var mProgressBar: ProgressBar


    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null /// TODO 0

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
        // TODO 2
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // TODO 3
        googleSignInClient = GoogleSignIn.getClient(this, gso)



        mLoginButton.setOnClickListener {
            try {
                signInAndSignUp(mEmailEditText.text.toString(), mPasswordEditText.text.toString())
            } catch (e: IllegalArgumentException) {
                Toast.makeText(this, "Your email and password cannot be empty.", Toast.LENGTH_SHORT)
                    .show()
                mProgressBar.visibility = View.GONE
            }

        }


        // TODO 5
        mGoogleButton.setOnClickListener {
            // step 1
            googleLogin()
        }

    }

    // TODO 4
    private fun googleLogin() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    // TODO 6
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_LOGIN_CODE) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)!!
            if (result.isSuccess) {
                val account = result.signInAccount

                // Step 2
                firebaseAuthWithGoogle(account)
            }
        }
    }

    // TODO 7
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {

        val credential = GoogleAuthProvider.getCredential(account?.idToken!!, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener {
                try {
                    when {
                        it.isSuccessful -> startMainActivity(it.result?.user)
                        else -> throw Exception(it.exception!!.message)
                    }

                } catch (e: Exception) {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
                mProgressBar.visibility = View.GONE

            }

    }

    private fun signInAndSignUp(email: String, password: String) {
        mProgressBar.visibility = View.VISIBLE
        auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            try {

                when {
                    it.isSuccessful -> startMainActivity(it.result?.user)
                    it.exception!!.message!!.isNotEmpty() -> signIn(email, password)
                    else -> throw Exception(it.exception!!.message)
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
            mProgressBar.visibility = View.GONE
        }
    }

    private fun signIn(email: String, password: String) {
        mProgressBar.visibility = View.VISIBLE
        auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {

            try {
                when {
                    it.isSuccessful -> startMainActivity(it.result?.user)
                    else -> throw Exception(it.exception!!.message)
                }
            } catch (e: Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
            mProgressBar.visibility = View.GONE
        }

    }

    private fun startMainActivity(user: FirebaseUser?) {
        Log.d("aa", "user : " + user.toString()!!)
        if (user == null) {
            return
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val GOOGLE_LOGIN_CODE = 9001 /// TODO 1
    }
}