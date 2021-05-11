package com.example.finalproject


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.finalproject.fragment.AlbumFragment
import com.example.finalproject.fragment.HomeFragment
import com.example.finalproject.fragment.LibraryFragment
import com.example.finalproject.fragment.SearchFragment
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import okhttp3.*


class MainActivity : AppCompatActivity() {
    lateinit var toolbar: ActionBar
    lateinit var fragment: Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation: ChipNavigationBar = findViewById(R.id.bottom_navigation)
        navigation.setItemSelected(R.id.home, true)
        navigation.setOnItemSelectedListener { item ->
            when (item) {
                R.id.home -> {
                    Navigation.findNavController(
                        this@MainActivity, R.id.nav_host_fragment).navigate(R.id.homeFragment, null, getNavOptions())
                }
                R.id.search -> {
                    Navigation.findNavController(
                        this@MainActivity, R.id.nav_host_fragment).navigate(R.id.searchFragment, null, getNavOptions())
                }
                R.id.library -> {
                    Navigation.findNavController(
                        this@MainActivity, R.id.nav_host_fragment).navigate(R.id.libraryFragment, null, getNavOptions())
                }
                R.id.setting -> {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }


//    private fun loadFragment(fragment: Fragment) {
//         load fragment
//        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.frame_container, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//    }
    private fun getNavOptions(): NavOptions? {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.default_enter_anim)
            .setExitAnim(R.anim.default_exit_anim)
            .build()
    }
}