package com.example.finalproject


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.finalproject.fragment.HomeFragment
import com.example.finalproject.fragment.LibraryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import okhttp3.*


class MainActivity : AppCompatActivity() {
    lateinit var toolbar: ActionBar
    lateinit var fragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        toolbar = supportActionBar!!
        fragment = HomeFragment()
        loadFragment(fragment)

        val navigation: ChipNavigationBar = findViewById(R.id.bottom_navigation)
        navigation.setItemSelected(R.id.home, true)
        navigation.setOnItemSelectedListener { item ->
            when (item) {
                R.id.home -> {
                    fragment = HomeFragment()
                    loadFragment(fragment)
                }
                R.id.search -> {

                }
                R.id.library -> {
                    fragment = LibraryFragment()
                    loadFragment(fragment)
                }
                R.id.setting -> {

                }
            }
        }
//        fragment = HomeFragment()
//        loadFragment(fragment)
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}