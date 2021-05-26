package com.example.finalproject.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Song
import com.github.ybq.android.spinkit.style.FadingCircle
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(), SearchView.OnQueryTextListener {

    lateinit var searchView: SearchView
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var adapter: SongAdapter
    lateinit var songs: ArrayList<Song>
    lateinit var noResultFound: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.listSearch)
        recyclerView.layoutManager = LinearLayoutManager(view.context)

        progressBar = view.findViewById(R.id.progressBar)
        val wave = FadingCircle()
        wave.color = resources.getColor(R.color.colorAccent)
        progressBar.indeterminateDrawable = wave

        searchView = view.findViewById(R.id.searchV)
        searchView.setOnQueryTextListener(this)

        noResultFound = view.findViewById(R.id.no_result_layout)

        return view
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        searchView.clearFocus()
        recyclerView.adapter = null
        val service = ApiAdapter.makeRetrofitService
        val call = service.search(query.toString())
        progressBar.visibility = View.VISIBLE
        noResultFound.visibility = View.GONE
        call.enqueue(object : Callback<ArrayList<Song>> {
            override fun onResponse(
                call: Call<ArrayList<Song>>,
                response: Response<ArrayList<Song>>
            ) {
                if (response.body()?.isEmpty() == false) {
                    progressBar.visibility = View.GONE
                    songs = response.body()!!
                    adapter = SongAdapter(songs, requireContext())

//                    adapter.notifyDataSetChanged()
                    recyclerView.adapter = adapter
                } else {
                    progressBar.visibility = View.GONE
                    noResultFound.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<ArrayList<Song>>, t: Throwable) {
                t.message?.let { Log.e("ERROR_SEARCH", it) }
            }
        })
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        return false
    }

}