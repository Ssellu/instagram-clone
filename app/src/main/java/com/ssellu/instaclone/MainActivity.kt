package com.ssellu.instaclone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ssellu.instaclone.navigation.AlarmFragment
import com.ssellu.instaclone.navigation.DetailViewFragment
import com.ssellu.instaclone.navigation.GridFragment
import com.ssellu.instaclone.navigation.UserFragment

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.nav_bottom)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_home ->{
                val fragment = DetailViewFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.view_main, fragment)
                    .commit()
                return true
            }
            R.id.action_account ->{
                val fragment = UserFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.view_main, fragment)
                    .commit()
                return true
            }
            R.id.action_favorite_alarm ->{
                val fragment = AlarmFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.view_main, fragment)
                    .commit()
                return true
            }
            R.id.action_add_photo ->{
                return true
            }
            R.id.action_search ->{
                val fragment = GridFragment()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.view_main, fragment)
                    .commit()
                return true
            }
        } // when
        return false
    }
}