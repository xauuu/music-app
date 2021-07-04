package com.example.finalproject.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalproject.R
import com.example.finalproject.adapter.SongAdapter
import com.example.finalproject.api.ApiAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryFragment : Fragment() {

    lateinit var toolbar: Toolbar
    lateinit var rvListHis: RecyclerView
    lateinit var numHis: TextView
    lateinit var btClear: TextView
    lateinit var progressBar: ProgressBar
    lateinit var constraintLayout: ConstraintLayout
    lateinit var noHis: Group

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        toolbar = view.findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            fragmentManager?.popBackStack()
        }

        rvListHis = view.findViewById(R.id.rvListHis1)
        rvListHis.layoutManager = LinearLayoutManager(view.context)

        constraintLayout = view.findViewById(R.id.layoutHis)
        noHis = view.findViewById(R.id.noHis)

        progressBar = view.findViewById(R.id.progressBar)
        numHis = view.findViewById(R.id.tvNumHis)
        btClear = view.findViewById(R.id.btClear)

        val sharedPreferences = requireActivity().getSharedPreferences("user", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getInt("id", -1)
        val service = ApiAdapter.makeRetrofitService
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.allHistory(userId, null)
                withContext(context = Dispatchers.Main) {
                    val listHis = response.body()!!
                    progressBar.visibility = View.GONE
                    if (listHis.isNotEmpty()) {
                        constraintLayout.visibility = View.VISIBLE
                        numHis.text = "${listHis.size} Bài hát"
                        rvListHis.adapter = SongAdapter(listHis, requireContext())
                    } else {
                        noHis.visibility = View.VISIBLE
                    }

                }
            } catch (e: Exception) {
                e.message?.run { Log.e("NETWORK", this) }
            }
        }

        btClear.setOnClickListener {
            val alertDialog: AlertDialog? = activity?.let {
                val builder = AlertDialog.Builder(it, R.style.AlertDialogCustom)
                builder.apply {
                    setTitle("Xoá lịch sử")
                    setMessage("Bạn có chắc chắn muốn xoá tất cả lịch sử nghe nhạc?")
                    setCancelable(true)
                    setPositiveButton(R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val response = service.deleteHistory(userId)
                                    withContext(Dispatchers.Main) {
                                        if (response.isSuccessful) {
                                            val result = response.body()
                                            Toast.makeText(requireContext(),
                                                result?.message,
                                                Toast.LENGTH_SHORT).show()
                                            dialog.cancel()
                                            constraintLayout.visibility = View.GONE
                                            noHis.visibility = View.VISIBLE
                                        }
                                    }
                                } catch (e: Exception) {
                                    e.message?.let { Log.e("ERROR DELETE HISTORY", it) }
                                }
                            }
                        })
                    setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.cancel()
                        })
                }

                builder.create()
            }
            alertDialog?.show()
        }

        return view
    }

}