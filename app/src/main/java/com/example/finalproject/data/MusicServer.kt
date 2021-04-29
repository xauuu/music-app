package com.example.finalproject.data

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MusicServer {

    fun getAllSongs() : List<Music> {
        val list = mutableListOf<Music>()
        try {
            val task = GetSongs()
            val result = task.execute("http://192.168.123.162:8000/api/music").get()
            val jsonArray: JSONArray = JSONArray(result)
            for (song in 0 until jsonArray.length()) {
                var obj: JSONObject = jsonArray.getJSONObject(song)
                val id = obj.getInt("id")
                val name = obj.getString("name")
                val album = obj.getString("album")
                val singer = obj.getString("singer")
                val imageUrl = obj.getString("imageUrl")
                val songUrl = obj.getString("songUrl")

                list.add(Music(id, name, album, singer, imageUrl, songUrl))
            }
        } catch (e: Exception) {
            Log.e("Can't Load Song", e.toString())
        }
        return list
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetSongs : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String? = getContent(params[0])

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

        }

        private fun getContent(url: String?): String {
            val content = StringBuilder()
            var line = ""
            try {
                val url: URL = URL(url)
                val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                val inputStreamReader = InputStreamReader(urlConnection.inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                do {
                    line = bufferedReader.readLine()
                    if (line != null)
                        content.append(line)
                } while (line != null)
                bufferedReader.close()
            } catch (e: Exception) {
                Log.d("ERROR URL", e.toString())
            }
            return content.toString()
        }
    }

}
