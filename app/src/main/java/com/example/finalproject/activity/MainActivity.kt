package com.example.finalproject.activity


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.example.finalproject.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import okhttp3.*


class MainActivity : AppCompatActivity() {

    lateinit var fragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation: ChipNavigationBar = findViewById(R.id.bottom_navigation)
        navigation.setItemSelected(R.id.home, true)
//        set khi click
        navigation.setOnItemSelectedListener { item ->
            when (item) {
//                Nếu click vô home
                R.id.home -> {
//                    load homeFragment vô fragment
                    Navigation.findNavController(
                        this@MainActivity, R.id.nav_host_fragment
                    ).navigate(
                        R.id.homeFragment,
                        null,
                        getNavOptions()
                    )
                }
                R.id.search -> {
                    Navigation.findNavController(
                        this@MainActivity,
                        R.id.nav_host_fragment
                    ).navigate(
                        R.id.searchFragment,
                        null,
                        getNavOptions()
                    )
                }
                R.id.library -> {
                    Navigation.findNavController(
                        this@MainActivity,
                        R.id.nav_host_fragment
                    ).navigate(
                        R.id.libraryFragment,
                        null,
                        getNavOptions()
                    )
                }
                R.id.user -> {
                    Navigation.findNavController(
                        this@MainActivity,
                        R.id.nav_host_fragment
                    ).navigate(
                        R.id.userFragment,
                        null,
                        getNavOptions()
                    )
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