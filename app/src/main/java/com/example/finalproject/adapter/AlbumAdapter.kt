package com.example.finalproject.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.activity.AlbumSongsActivity
import com.example.finalproject.R
import com.example.finalproject.model.Album

class AlbumAdapter(
    private val list_album: ArrayList<Album>,
    private val context: Context,
) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.album_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list_album[position]
        Glide.with(context).load(item.imageUrl).into(holder.imageAlbum)
        holder.albumName.text = item.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, AlbumSongsActivity::class.java)
            intent.putExtra("id", item.id)
            intent.putExtra("name", item.name)
            intent.putExtra("image", item.imageUrl)
            intent.putExtra("year", item.year)
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = list_album.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageAlbum: ImageView = itemView.findViewById(R.id.imAlbum)
        val albumName: TextView = itemView.findViewById(R.id.albumName)
    }
}