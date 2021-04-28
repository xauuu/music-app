package com.example.finalproject.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.SongActivity
import com.example.finalproject.data.Music
import com.makeramen.roundedimageview.RoundedImageView

class LibraryAdapter(
        private val data: List<Music>,
        private val context: Context,
): RecyclerView.Adapter<LibraryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryAdapter.ViewHolder, position: Int) {
        var item = data[position]
//        Picasso.with(context).load(item.imageUrl).into(holder.img)
        holder.text1.text = item.name
        holder.text2.text = item.singer

        val image: ByteArray? = holder.getAlbumArt(item.songUrl)

        if (image != null) {
            Glide.with(context).asBitmap().load(image).into(holder.img)
        } else {
            Glide.with(context).load(R.drawable.beauty).into(holder.img)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SongActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("list", 1)
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

        fun getAlbumArt(uri: String): ByteArray? {
            val retriever: MediaMetadataRetriever = MediaMetadataRetriever()
            retriever.setDataSource(uri)
            val art: ByteArray? = retriever.embeddedPicture
            retriever.release()

            return art
        }
    }
}