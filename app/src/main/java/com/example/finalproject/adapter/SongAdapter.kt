package com.example.finalproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.activity.SongActivity
import com.example.finalproject.model.Song
import com.makeramen.roundedimageview.RoundedImageView
import kotlin.collections.ArrayList

class SongAdapter(
    private val data: ArrayList<Song>,
    private val context: Context
): RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position] // lấy từng bài hát
        Glide.with(context).load(item.imageUrl).placeholder(R.drawable.loading_anim).into(holder.img)
        holder.songName.text = item.name
        holder.songArtist.text = item.artist

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SongActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("check", 0)
            intent.putExtra("list", data)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: RoundedImageView = itemView.findViewById(R.id.ivItemImage)
        val songName: TextView = itemView.findViewById(R.id.songName)
        val songArtist: TextView = itemView.findViewById(R.id.songArtist)
    }
}