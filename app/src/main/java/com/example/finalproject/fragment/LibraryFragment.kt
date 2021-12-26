package com.example.finalproject.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.activity.DeviceActivity
import com.example.finalproject.activity.FavoriteActivity
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import kotlinx.coroutines.*


class LibraryFragment : Fragment() {

    lateinit var btFavorite: Button
    lateinit var btDevice: Button

    lateinit var rvListHis: RecyclerView
    lateinit var tvTextHis: TextView
    lateinit var allHis: TextView
    lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        val view = inflater.inflate(R.layout.fragment_library, container, false)

        btDevice = view.findViewById(R.id.btDevice)
        btFavorite = view.findViewById(R.id.btFavorite)

        rvListHis = view.findViewById(R.id.rvListHis)
        rvListHis.layoutManager = LinearLayoutManager(view.context)

        tvTextHis = view.findViewById(R.id.tvTextHis)
        allHis = view.findViewById(R.id.allHis)
        progressBar = view.findViewById(R.id.progressBar3)

        btDevice.setOnClickListener {
            startActivity(Intent(activity, DeviceActivity::class.java))
        }

        btFavorite.setOnClickListener {
            startActivity(Intent(activity, FavoriteActivity::class.java))
        }


        val sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("check", false)) {
            val service = ApiAdapter.makeRetrofitService
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = service.allHistory(sharedPreferences.getInt("id", -1), 5)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val listHis = response.body()!!
                            progressBar.visibility = View.GONE
                            if (listHis.isNotEmpty()) {
                                rvListHis.adapter = SongAdapter(listHis, requireContext())
                                tvTextHis.visibility = View.GONE
                            } else {
                                tvTextHis.visibility = View.VISIBLE
                            }

                        }
                    }
                } catch (e: Exception) {
                    e.message?.let { Log.e("ERROR HISTORY", it) }
                }
            }
        } else {
            progressBar.visibility = View.GONE
            allHis.visibility = View.GONE
            rvListHis.visibility = View.GONE
        }

        allHis.setOnClickListener {
            Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.historyFragment)
        }

        return view
    }



}

