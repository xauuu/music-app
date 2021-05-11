package com.example.finalproject.fragment

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.data.Music
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {
    lateinit var searchView: SearchView
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var adapter: SongAdapter
    lateinit var songs: ArrayList<Music>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        activity?.title = "Tìm kiếm"

        recyclerView = view.findViewById(R.id.listSearch)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        progressBar = view.findViewById(R.id.progressBar)

        searchView = view.findViewById(R.id.searchV)
        searchView.setOnQueryTextListener(this)

        return view
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.clearFocus()
        recyclerView.adapter = null
        val service = ApiAdapter.makeRetrofitService
        val call = service.search(query.toString())
        progressBar.visibility = View.VISIBLE
        call.enqueue(object : Callback<ArrayList<Music>> {
            override fun onResponse(
                call: Call<ArrayList<Music>>,
                response: Response<ArrayList<Music>>
            ) {
                if (response.body()?.isEmpty() == false) {
                    progressBar.visibility = View.GONE
                    songs = response.body()!!
                    adapter = SongAdapter(songs, requireContext())

                    adapter.notifyDataSetChanged()
                    recyclerView.adapter = adapter
                } else {
                    progressBar.visibility = View.GONE
                    Log.e("SEARCH", "No tim thay")
                }
            }

            override fun onFailure(call: Call<ArrayList<Music>>, t: Throwable) {
                t.message?.let { Log.e("ERROR_SEARCH", it) }
            }
        })
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        return false
    }

}