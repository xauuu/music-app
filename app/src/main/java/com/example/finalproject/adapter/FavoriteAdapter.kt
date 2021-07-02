package com.example.finalproject.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.activity.SongActivity
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Song
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteAdapter(
    private val data: ArrayList<Song>,
    private val context: Context,
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_favorite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position] // lấy từng bài hát

        Glide.with(context).load(item.imageUrl).placeholder(R.drawable.loading_anim)
            .into(holder.img)
        holder.songName.text = item.name
        holder.songArtist.text = item.artist

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SongActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("check", 0)
            intent.putExtra("list", data)
            context.startActivity(intent)
        }

        holder.more.setOnClickListener {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.fragment_bottom_sheet_dialog, null)

            val dialog = BottomSheetDialog(context, R.style.DialogCustomTheme)

            Glide.with(context).load(item.imageUrl).placeholder(R.drawable.loading_anim)
                .into(view.findViewById<RoundedImageView>(R.id.imSong))

            view.findViewById<TextView>(R.id.tvSongName).text = item.name
            view.findViewById<TextView>(R.id.tvSongArtist).text = item.artist

            view.findViewById<Button>(R.id.play).setOnClickListener {
                val intent = Intent(context, SongActivity::class.java)
                intent.putExtra("position", position)
                intent.putExtra("check", 0)
                intent.putExtra("list", data)
                context.startActivity(intent)
            }

            view.findViewById<Button>(R.id.delete).setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    val service = ApiAdapter.makeRetrofitService
                    try {
                        val response = service.deleteFavorite(data[position].idFavorite)
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                val result = response.body()
                                Toast.makeText(context, result?.message, Toast.LENGTH_SHORT).show()
                                data.removeAt(position)
                                notifyDataSetChanged()
                                dialog.dismiss()
                            }
                        }
                    } catch (e: Exception) {
                        e.message?.let { Log.e("ERROR DELETE FAVORITE", it) }
                    }
                }
            }

            view.findViewById<Button>(R.id.share).setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Nghe bài hát ${item.name} tại ứng dụng XmusicG")
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(sendIntent, null)
                context.startActivity(shareIntent)
            }

            view.findViewById<Button>(R.id.close).setOnClickListener {
                dialog.dismiss()
            }

            dialog.setContentView(view)
            dialog.show()
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: RoundedImageView = itemView.findViewById(R.id.ivItemImage)
        val songName: TextView = itemView.findViewById(R.id.songName)
        val songArtist: TextView = itemView.findViewById(R.id.songArtist)
        val more: ImageButton = itemView.findViewById(R.id.more)
    }
}