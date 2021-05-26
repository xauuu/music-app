package com.example.finalproject.fragment

import android.app.Activity.*
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.example.finalproject.activity.LoginActivity
import com.example.finalproject.R
import com.example.finalproject.activity.WebActivity
import com.google.android.material.navigation.NavigationView


class UserFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var btLogin: Button
    lateinit var layoutAcc: ConstraintLayout
    lateinit var layoutLogout: ConstraintLayout
    lateinit var nvShare: NavigationView
    lateinit var navigationView: NavigationView
    lateinit var tvName: TextView
    lateinit var tvEmail: TextView
    lateinit var sharePref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user, container, false)
        initView(view)
        checkSession()
        return view
    }

    private fun initView(view: View) {
        btLogin = view.findViewById(R.id.btLog)
        layoutAcc = view.findViewById(R.id.layoutAcc)
        layoutLogout = view.findViewById(R.id.layoutLogout)
        tvName = view.findViewById(R.id.tvName)
        tvEmail = view.findViewById(R.id.email)
        navigationView = view.findViewById(R.id.nvLogout)
        navigationView.setNavigationItemSelectedListener(this)
        nvShare = view.findViewById(R.id.nvShare)
        nvShare.setNavigationItemSelectedListener(this)
        sharePref = requireContext().getSharedPreferences("user", Context.MODE_PRIVATE)

        btLogin.setOnClickListener {
            startActivityForResult(
                Intent(
                    activity,
                    LoginActivity::class.java
                ), 111
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 111 && resultCode == RESULT_OK) {
            checkSession()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun checkSession() {
        val check = sharePref.getBoolean("check", false)
        if (check) {
            layoutAcc.visibility = View.VISIBLE
            layoutLogout.visibility = View.VISIBLE
            btLogin.visibility = View.GONE
            tvName.text = sharePref.getString("name", "Name")
            tvEmail.text = sharePref.getString("email", "Email")
        } else {
            layoutAcc.visibility = View.GONE
            layoutLogout.visibility = View.GONE
            btLogin.visibility = View.VISIBLE
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                sharePref.edit().clear().apply()
                checkSession()
            }

            R.id.nav_about -> {
                val intent = Intent(activity, WebActivity::class.java)
                intent.putExtra("url", "http://xmusicg.herokuapp.com/download")
                startActivity(intent)
            }

            R.id.nav_share -> {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Tải xuống ứng dụng tại http://xmusicg.herokuapp.com/download")
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        }
        return true
    }

}

