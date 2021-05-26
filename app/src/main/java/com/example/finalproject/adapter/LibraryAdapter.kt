package com.example.finalproject.adapter

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.activity.SongActivity
import com.example.finalproject.model.Song
import com.makeramen.roundedimageview.RoundedImageView
import java.util.*
import kotlin.collections.ArrayList

class LibraryAdapter(
    private val data: ArrayList<Song>,
    private val context: Context,
): RecyclerView.Adapter<LibraryAdapter.ViewHolder>(), Filterable {

    var songFilterList = ArrayList<Song>()
    init {
        songFilterList = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_song, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryAdapter.ViewHolder, position: Int) {
        var item = songFilterList[position]
//        Picasso.with(context).load(item.imageUrl).into(holder.img)
        holder.text1.text = item.name
        holder.text2.text = item.artist

        val image: ByteArray? = item.songUrl?.let { holder.getAlbumArt(it) }

        if (image != null) {
            Glide.with(context).asBitmap().load(image).placeholder(R.drawable.loading_anim).into(holder.img)
        } else {
            Glide.with(context).load(R.drawable.beauty).placeholder(R.drawable.loading_anim).into(holder.img)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SongActivity::class.java)
            intent.putExtra("position", position)
            intent.putExtra("check", 1)
            intent.putExtra("list", data)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return songFilterList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                songFilterList = if (charSearch.isEmpty()) {
                    data
                } else {
                    val resultList = ArrayList<Song>()
                    for (row in data) {
                        if (row.name?.toLowerCase(Locale.ROOT)?.contains(charSearch.toLowerCase(Locale.ROOT)) == true) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = songFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                songFilterList = results?.values as ArrayList<Song>
                notifyDataSetChanged()
            }

        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: RoundedImageView = itemView.findViewById(R.id.ivItemImage)
        val text1: TextView = itemView.findViewById(R.id.songName)
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