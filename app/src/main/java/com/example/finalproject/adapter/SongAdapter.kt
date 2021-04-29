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
import com.example.finalproject.SongActivity
import com.example.finalproject.data.Music
import com.makeramen.roundedimageview.RoundedImageView

class SongAdapter(
        private val data: ArrayList<Music>,
        private val context: Context
): RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.song_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = data[position]
        Glide.with(context).load(item.imageUrl).into(holder.img)
        holder.text1.text = item.name
        holder.text2.text = item.singer

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
        val text1: TextView = itemView.findViewById(R.id.songTitle)
        val text2: TextView = itemView.findViewById(R.id.songArtist)
    }
}