package com.example.finalproject.activity

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PorterDuff.Mode.SRC_IN
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast.LENGTH_SHORT
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.lifecycleScope
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.bumptech.glide.Glide
import com.example.finalproject.R
import com.example.finalproject.api.ApiAdapter
import com.example.finalproject.model.Song
import com.example.finalproject.service.CreateNotification
import com.example.finalproject.service.OnClearFromRecentService
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.coroutines.*
import java.lang.Math.sqrt
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random


class SongActivity : AppCompatActivity(), OnCompletionListener {

    private var mediaPlayer = MediaPlayer()

    private var listSongs: ArrayList<Song> = ArrayList()

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

    lateinit var notificationManager: NotificationManager

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    //tb
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            when (intent?.getStringExtra("actionname")) {
                CreateNotification().ACTION_PREVIOUS -> prevBtnClicked()
                CreateNotification().ACTION_PLAY -> playPauseBtnClicked()
                CreateNotification().ACTION_NEXT -> nextBtnClicked()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_song2)
        //tb
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
            registerReceiver(broadcastReceiver, IntentFilter("XMusicG"))
            startService(Intent(baseContext, OnClearFromRecentService::class.java))

        }

        initView()
        getIntentMethod()
        seekBar()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(sensorListener, sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH
    }

    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            if (acceleration > 12) {
                nextBtnClicked()
                acceleration = 0F
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    //tb
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CreateNotification().CHANNEL_ID,
                "XMusicG",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
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

        btPrev.setOnClickListener {
            prevBtnClicked()
        }

        btPlay.setOnClickListener {
            playPauseBtnClicked()
        }

        btNext.setOnClickListener {
            nextBtnClicked()
        }

        //       Khi click nut ngau nhien
        btShuffle.setOnClickListener {
            if (shuffleBoolean) {
                shuffleBoolean = false
                btShuffle.setColorFilter(
                    getColor(applicationContext, R.color.giangg),
                    SRC_IN
                )
            } else {
                shuffleBoolean = true
                btShuffle.setColorFilter(getColor(applicationContext, R.color.colorAccent), SRC_IN)
                makeText(this, "Ngẫu nhiên", LENGTH_SHORT).show()
            }
        }

//        Khi click nut lap lai
        btRepeat.setOnClickListener {
            if (repeatBoolean) {
                repeatBoolean = false
                btRepeat.setImageResource(R.drawable.ic_repeat)
                btRepeat.setColorFilter(
                    getColor(applicationContext, R.color.giangg),
                    SRC_IN
                )
                makeText(this, "Huỷ lặp bài hát hiện tại", LENGTH_SHORT).show()
            } else {
                repeatBoolean = true
                btRepeat.setImageResource(R.drawable.ic_repeat1)
                btRepeat.setColorFilter(getColor(applicationContext, R.color.colorAccent), SRC_IN)
                makeText(this, "Lặp bài hát hiện tại", LENGTH_SHORT).show()
            }
        }

//        Khi click nut quay lai
        btBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getIntentMethod() {
//        Lấy vị trí bài hát khi click
        position = intent.getIntExtra("position", -1)
        check = intent.getIntExtra("check", -1)
//        Lấy danh sách bài hát gửi qua khi click
        listSongs = intent.getSerializableExtra("list") as ArrayList<Song>
        setAudio(position)

    }

    private fun setAudio(pos: Int) {
        CreateNotification().createNotification(
            this,
            listSongs[pos],
            R.drawable.ic_icons8_pause,
            pos,
            listSongs.size - 1,
            check,
            true,
        )

        addHistory(listSongs[pos].id)

        btPlay.setImageResource(R.drawable.ic_icons8_pause)
        tvTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
        tvTitle.isSelected = true
        tvTitle.text = listSongs[pos].name
        tvArtist.text = listSongs[pos].artist

//        Load ảnh
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
//        set ảnh quay tròn
        rivImg.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.route)
        )
//        Khởi tạo mediaPlayer với đường dẫn
        mediaPlayer = MediaPlayer.create(applicationContext, Uri.parse(listSongs[pos].songUrl))
//        Start
        mediaPlayer.start()
//        Thêm sự kiện khi hoàn thành 1 bài hát
        mediaPlayer.setOnCompletionListener(this)
//        Set seekbar max = thời lượng của bài hát
        seekBar.max = mediaPlayer.duration
        tvTotalTime.text = formattedTime(mediaPlayer.duration / 1000)
    }

    private fun seekBar() {
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
                    val msg = Message()
                    msg.what = mediaPlayer.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(100)
                } catch (e: InterruptedException) {
                }
            }
        }).start()
    }

    private fun addHistory(idSong: Int) {
        val sharedPreferences = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("check", false) && check == 0) {
            val service = ApiAdapter.makeRetrofitService
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    val response = service.addHistory(sharedPreferences.getInt("id", -1), idSong)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val result = response.body()!!
                            Log.e("HISTORY", result.message)
                        }
                    }
                } catch (e: Exception) {
                    e.message?.let { Log.e("ERROR HISTORY", it) }
                }
            }
        }
    }

    private fun updateViews(id: Int) {
        val service = ApiAdapter.makeRetrofitService
        GlobalScope.launch(Dispatchers.IO) {
            try {
                service.updateSong(id)
            } catch (e: Exception) {
                e.message?.let { Log.e("ERROR UPDATE", it) }
            }
        }
    }

    private fun prevBtnClicked() {
        mediaPlayer.stop()
        mediaPlayer.release()

//            Lấy vị trí của bài hát trước đó
        position = if (shuffleBoolean) {
//                Nếu có ngẫu nhiên, thì nó sẽ lấy ngãu nhiên
            Random.nextInt((listSongs.size - 1) + 1)
        } else {
//                Không thì nó sẽ lấy vị trí trước đó
            if (position - 1 < 0) listSongs.size - 1 else position - 1
        }

        setAudio(position)
    }

    private fun nextBtnClicked() {
        mediaPlayer.stop()
        mediaPlayer.release()

        position = if (shuffleBoolean) {
            Random.nextInt((listSongs.size - 1) + 1)
        } else {
            if (position + 1 > listSongs.size - 1) 0 else position + 1
        }

        setAudio(position)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun playPauseBtnClicked() {
//        Nếu đang phát
        if (mediaPlayer.isPlaying) {
            CreateNotification().createNotification(
                this,
                listSongs[position],
                R.drawable.ic_icons8_play,
                position,
                listSongs.size - 1,
                check,
                false
            )
            btPlay.setImageDrawable(resources.getDrawable(R.drawable.pause_to_play))
            val drawable: Drawable = btPlay.drawable
            if (drawable is AnimatedVectorDrawableCompat) {
                drawable.start()
            } else {
                if (drawable is AnimatedVectorDrawable) {
                    drawable.start()
                }
            }
            mediaPlayer.pause()
            rivImg.animation = null
        } else {
            CreateNotification().createNotification(
                this,
                listSongs[position],
                R.drawable.ic_icons8_pause,
                position,
                listSongs.size - 1,
                check,
                true
            )
            btPlay.setImageDrawable(resources.getDrawable(R.drawable.play_to_pause))
            val drawable: Drawable = btPlay.drawable
            if (drawable is AnimatedVectorDrawableCompat) {
                drawable.start()
            } else {
                if (drawable is AnimatedVectorDrawable) {
                    drawable.start()
                }
            }
            mediaPlayer.start()
            rivImg.startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.route)
            )
        }
    }

    //    Lấy ảnh nhạc trong máy
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

    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll()
        }
        unregisterReceiver(broadcastReceiver)
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    //    Khi hoàn thành bài hát
    override fun onCompletion(mp: MediaPlayer?) {
//        Nếu check = 0 thì cập nhật lượt nghe
        if (check == 0) {
            updateViews(listSongs[position].id)
        }
//    Nếu có lặp lại, thì seekBar sẽ về lại 00
        if (repeatBoolean) {
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
        } else {
//            Không thì nó sẽ nhảy tới bài hát tiếp theo
            nextBtnClicked()
            btPlay.setImageResource(R.drawable.ic_icons8_pause)
            mediaPlayer.start()
            mediaPlayer.setOnCompletionListener(this)
        }
    }
}