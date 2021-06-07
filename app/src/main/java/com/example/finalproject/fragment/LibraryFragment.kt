package com.example.finalproject.fragment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.activity.DeviceActivity
import com.example.finalproject.activity.FavoriteActivity
import com.example.finalproject.adapter.LibraryAdapter
import com.example.finalproject.model.Song
import com.github.ybq.android.spinkit.style.Wave
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryFragment : Fragment() {



    lateinit var btFavorite: Button
    lateinit var btDevice: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_library, container, false)

        btDevice = view.findViewById(R.id.btDevice)
        btFavorite = view.findViewById(R.id.btFavorite)

        btDevice.setOnClickListener {
            startActivity(Intent(activity, DeviceActivity::class.java))
        }

        btFavorite.setOnClickListener {
            startActivity(Intent(activity, FavoriteActivity::class.java))
        }

        return view
    }



}

