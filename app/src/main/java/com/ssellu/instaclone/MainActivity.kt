package com.ssellu.instaclone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.ssellu.instaclone.general.Constants
import com.ssellu.instaclone.navigation.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var bottomNavigationView: BottomNavigationView

    lateinit var backImageView: ImageView
    var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.nav_bottom)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        firebaseAuth = FirebaseAuth.getInstance()
        backImageView = findViewById(R.id.iv_back_to_login)
        backImageView.setOnClickListener{

        }



        // Permissions
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        // default fragment
        bottomNavigationView.selectedItemId = R.id.action_home

        setToolbarDefault()
    }

    fun setToolbarDefault(){
        backImageView.visibility = View.GONE
    }
    fun attachFragment(clazz:Class<out Fragment>, bundle: Bundle?){
        val fragment = clazz.newInstance()
        if(bundle != null){
            fragment.arguments = bundle
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.view_main, fragment)
            .commit()
    }


    private fun attachFragment(clazz:Class<out Fragment>){
        attachFragment(clazz, null)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_home ->{
                attachFragment(DetailViewFragment::class.java)
                return true
            }
            R.id.action_account ->{
                val bundle = Bundle()
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val email = FirebaseAuth.getInstance().currentUser?.email
                bundle.putString(Constants.TARGET_USER_EMAIL_FOR_DETAIL_PAGE, email)
                bundle.putString(Constants.TARGET_USER_UID_FOR_DETAIL_PAGE, uid)
                attachFragment(UserFragment::class.java, bundle)
                return true
            }
            R.id.action_favorite_alarm ->{
                attachFragment(AlarmFragment::class.java)
                return true
            }
            R.id.action_add_photo ->{
                // Checking permission
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    startActivity(Intent(this, AddPhotoActivity::class.java))
                }
                return true
            }
            R.id.action_search ->{
                attachFragment(GridFragment::class.java)
                return true
            }
            else -> return false
        }
    }
}