package com.example.finalproject.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.data.ApiAdapter
import com.example.finalproject.data.Music
import kotlinx.coroutines.*

class HomeFragment : Fragment() {
    lateinit var v: View
    private lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var songs: ArrayList<Music>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_home, container, false)
        activity?.title = "Trang chủ"

        progressBar = v.findViewById(R.id.progressBar)

        recyclerView = v.findViewById(R.id.rvList)
        recyclerView.layoutManager = LinearLayoutManager(v.context)


        val service = ApiAdapter.makeRetrofitService
        CoroutineScope(Dispatchers.IO).launch {
            progressBar.visibility = View.VISIBLE
            try {
                val response = service.getSongs()
                withContext(context = Dispatchers.Main) {
                    if (response.isSuccessful) {
                        songs = response.body()!!
                        recyclerView.adapter = SongAdapter(songs, requireContext())
                        progressBar.visibility = View.GONE
                    } else {
                        progressBar.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                e.message?.run { Log.e("NETWORK", this) }
            }

        }

        return v
    }
}

