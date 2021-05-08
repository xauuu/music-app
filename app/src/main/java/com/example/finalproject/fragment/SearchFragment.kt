package com.example.finalproject.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import com.example.finalproject.R
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.data.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {
    lateinit var searchView: SearchView
    lateinit var adapter: SongAdapter
    lateinit var songs: ArrayList<Music>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        activity?.title = "Tìm kiếm"

        searchView = view.findViewById(R.id.searchV)
        searchView.setOnQueryTextListener(this)
        return view
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val service = ApiAdapter.makeRetrofitService
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.getSongs()
                withContext(context = Dispatchers.Main) {
                    if (response.isSuccessful) {
                        songs = response.body()!!
                        adapter = SongAdapter(songs,requireContext())
                    }
                }
            } catch (e: Exception) {
                e.message?.run { Log.e("NETWORK", this) }
            }
        }
        return true
    }

}