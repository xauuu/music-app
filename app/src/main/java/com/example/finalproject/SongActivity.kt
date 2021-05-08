package com.example.finalproject

import android.annotation.SuppressLint
import android.graphics.PorterDuff.Mode.*
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.*
import com.bumptech.glide.Glide
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.data.Music
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class SongActivity : AppCompatActivity(), OnCompletionListener {

    private var mediaPlayer = MediaPlayer()

    private var listSongs: ArrayList<Music> = ArrayList()

    lateinit var tvTitle: TextView
    lateinit var tvArtist: TextView
    lateinit var rivImg: RoundedImageView
    lateinit var btPlay: ImageButton
    lateinit var btPrev: ImageButton
    lateinit var btNext: ImageButton
    lateinit var btRepeat: ImageButton
    lateinit var btShuffle: ImageButton
    lateinit var btBack: ImageButton
    lateinit var seekBar: SeekBar
    lateinit var tvCurrentTime: TextView
    lateinit var tvTotalTime: TextView

    private var repeatBoolean: Boolean = false
    private var shuffleBoolean: Boolean = false

    private var position = -1
    private var check = -1

    lateinit var playThread: Thread
    lateinit var prevThread: Thread
    lateinit var nextThread: Thread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song2)

        initView()
        getIntentMethod()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        @SuppressLint("HandlerLeak")
        val handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val currentPosition = msg.what

                seekBar.progress = currentPosition
                tvCurrentTime.text = formattedTime(currentPosition / 1000)
            }
        }
        Thread(Runnable {
            while (true) {
                try {
                    var msg = Message()
                    msg.what = mediaPlayer.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                }
            }
        }).start()

        btShuffle.setOnClickListener {
            if (shuffleBoolean) {
                shuffleBoolean = false
                btShuffle.setColorFilter(getColor(applicationContext, R.color.colorPrimaryText), SRC_IN)
            } else {
                shuffleBoolean = true
                btShuffle.setColorFilter(getColor(applicationContext, R.color.colorAccent), SRC_IN)
                Toast.makeText(this, "Ngẫu nhiên", Toast.LENGTH_SHORT).show()
            }

        }

        btRepeat.setOnClickListener {
            if (repeatBoolean) {
                repeatBoolean = false
                btRepeat.setImageResource(R.drawable.ic_repeat)
                btRepeat.setColorFilter(getColor(applicationContext, R.color.colorPrimaryText), SRC_IN)
                Toast.makeText(this, "Huỷ lặp bài hát hiện tại", Toast.LENGTH_SHORT).show()
            } else {
                repeatBoolean = true
                btRepeat.setImageResource(R.drawable.ic_repeat1)
                btRepeat.setColorFilter(getColor(applicationContext, R.color.colorAccent), SRC_IN)
                Toast.makeText(this, "Lặp bài hát hiện tại", Toast.LENGTH_SHORT).show()
            }
        }

        btBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initView() {
        tvTitle = findViewById(R.id.tvTitle)
        tvArtist = findViewById(R.id.tvArtist)
        rivImg = findViewById(R.id.rivImg)
        btPlay = findViewById(R.id.btPlay)
        btNext = findViewById(R.id.btNext)
        btPrev = findViewById(R.id.btPrev)
        btRepeat = findViewById(R.id.btRepeat)
        btShuffle = findViewById(R.id.btShuffle)
        btBack = findViewById(R.id.btBack)
        seekBar = findViewById(R.id.playerSeekBar)
        tvCurrentTime = findViewById(R.id.tvCurrentTime)
        tvTotalTime = findViewById(R.id.tvTotalTime)
//        visualizer = findViewById(R.id.blast)
    }

    private fun getIntentMethod() {
        position = intent.getIntExtra("position", -1)
        check = intent.getIntExtra("check", -1)
        listSongs = intent.getSerializableExtra("list") as ArrayList<Music>

        setAudio(position)
    }

    private fun setAudio(pos: Int) {
        btPlay.setImageResource(R.drawable.ic_baseline_pause_24)

        tvTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        tvTitle.isSelected = true
        tvTitle.text = listSongs[pos].name
        tvArtist.text = listSongs[pos].artist

        if (check == 0) {
            Glide.with(this).load(listSongs[pos].imageUrl).into(rivImg)
        } else {
            val image: ByteArray? = listSongs[pos].songUrl?.let { getAlbumArt(it) }
            if (image != null) {
                Glide.with(this).asBitmap().load(image).into(rivImg)
            } else {
                Glide.with(this).load(R.drawable.beauty).into(rivImg)
            }
        }

        rivImg.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.route)
        )
        mediaPlayer = MediaPlayer.create(applicationContext, Uri.parse(listSongs[pos].songUrl))
        mediaPlayer.start()
        mediaPlayer.setOnCompletionListener(this)
        seekBar.max = mediaPlayer.duration

        tvTotalTime.text = formattedTime(mediaPlayer.duration / 1000)

    }

    private fun updateViews(id: Int) {
        val service = ApiAdapter.makeRetrofitService
        CoroutineScope(Dispatchers.IO).launch {
            service.updateSong(id)
            Log.e("Updated", "Đã cập nhật lươt nghe bài hát có id = ${id.toString()}")
        }
    }

    override fun onResume() {
        playThreadBtn()
        nextThreadBtn()
        prevThreadBtn()
        super.onResume()
    }

    private fun prevThreadBtn() {
        prevThread = Thread {
            btPrev.setOnClickListener {
                prevBtnClicked()
            }
        }
        prevThread.start()
    }

    private fun prevBtnClicked() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()

            position = if (shuffleBoolean) {
                Random.nextInt((listSongs.size - 1) + 1)
            } else {
                if (position - 1 < 0) listSongs.size - 1 else position - 1
            }
            Log.e("pos", position.toString())
            setAudio(position)
            mediaPlayer.setOnCompletionListener(this)
        } else {
            mediaPlayer.stop()
            mediaPlayer.release()

            position = if (shuffleBoolean) {
                Random.nextInt((listSongs.size - 1) + 1)
            } else {
                if (position - 1 < 0) listSongs.size - 1 else position - 1
            }

            setAudio(position)
            btPlay.setImageResource(R.drawable.ic_play_24)
            mediaPlayer.pause()
        }
    }

    private fun nextThreadBtn() {
        nextThread = Thread {
            btNext.setOnClickListener {
                nextBtnClicked()
            }
        }
        nextThread.start()
    }

    private fun nextBtnClicked() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()

            position = if (shuffleBoolean) {
                Random.nextInt((listSongs.size - 1) + 1)
            } else {
                if (position + 1 > listSongs.size - 1) 0 else position + 1
            }

            Log.e("pos", position.toString())

            setAudio(position)
            mediaPlayer.setOnCompletionListener(this)
        } else {
            mediaPlayer.stop()
            mediaPlayer.release()

            position = if (shuffleBoolean) {
                Random.nextInt((listSongs.size - 1) + 1)
            } else {
                if (position + 1 > listSongs.size - 1) 0 else position + 1
            }

            setAudio(position)
            btPlay.setImageResource(R.drawable.ic_play_24)
            mediaPlayer.pause()
        }
    }

    private fun playThreadBtn() {
        playThread = Thread {
            btPlay.setOnClickListener {
                playPauseBtnClicked()
            }
        }
        playThread.start()
    }

    private fun playPauseBtnClicked() {
        if (mediaPlayer.isPlaying) {
            btPlay.setImageResource(R.drawable.ic_play_24)
            mediaPlayer.pause()
            rivImg.animation = null
        } else {
            btPlay.setImageResource(R.drawable.ic_baseline_pause_24)
            mediaPlayer.start()
            rivImg.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.route)
            )
        }
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever: MediaMetadataRetriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art: ByteArray? = retriever.embeddedPicture
        retriever.release()

        return art
    }

    private fun formattedTime(mCurrent: Int): String {
        var totalOut = ""
        var totalNew = ""
        val second = (mCurrent % 60).toString()
        val minutes = (mCurrent / 60).toString()
        totalOut = "$minutes:$second"
        totalNew = "$minutes:0$second"

        return if (second.length == 1) {
            totalNew
        } else {
            totalOut
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun onCompletion(mp: MediaPlayer?) {
        if (check == 1) {
            updateViews(listSongs[position].id)
        }
        if (repeatBoolean) {
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
        } else {
            nextBtnClicked()
            btPlay.setImageResource(R.drawable.ic_baseline_pause_24)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener(this)
        }

    }


}