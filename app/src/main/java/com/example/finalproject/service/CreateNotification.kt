package com.example.finalproject.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.finalproject.R
import com.example.finalproject.activity.SongActivity
import com.example.finalproject.model.Music

class CreateNotification {
    val CHANNEL_ID = "channel1"
    val ACTION_PREVIOUS = "actionprevious"
    val ACTION_PLAY = "actionplay"
    val ACTION_NEXT = "actionnext"

    lateinit var notification: Notification

    @SuppressLint("WrongConstant")
    fun createNotification(
        context: Context,
        music: Music,
        playButton: Int,
        pos: Int,
        size: Int,
        check: Int,
        isPlaying: Boolean
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            val mediaSessionCompat = MediaSessionCompat(context, "tag")

            val pendingIntentPrevious: PendingIntent?
            val drw_previous: Int
            if (pos == 0) {
                pendingIntentPrevious = null
                drw_previous = 0
            } else {
                val intentPrevious = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_PREVIOUS)
                pendingIntentPrevious = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentPrevious,
                    PendingIntent.FLAG_UPDATE_CURRENT,
                )
                drw_previous = R.drawable.ic_icons8_skip_to_start
            }   
            val intentPlay = Intent(context, NotificationActionService::class.java)
                .setAction(ACTION_PLAY)
            val pendingIntentPlay = PendingIntent.getBroadcast(
                context,
                0,
                intentPlay,
                PendingIntent.FLAG_UPDATE_CURRENT,
            )

            val pendingIntentNext: PendingIntent?
            val drw_next: Int
            if (pos == size) {
                pendingIntentNext = null
                drw_next = 0
            } else {
                val intentNext = Intent(context, NotificationActionService::class.java)
                    .setAction(ACTION_NEXT)
                pendingIntentNext = PendingIntent.getBroadcast(
                    context,
                    0,
                    intentNext,
                    PendingIntent.FLAG_UPDATE_CURRENT,
                )
                drw_next = R.drawable.ic_icons8_end
            }

            val style = androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSessionCompat.sessionToken)

            val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_heart)
                .setContentTitle(music.name)
                .setContentText(music.artist)
                .setOnlyAlertOnce(true)
                .setShowWhen(false)
                .addAction(drw_previous, "Previous", pendingIntentPrevious)
                .addAction(playButton, "Play", pendingIntentPlay)
                .addAction(drw_next, "Next", pendingIntentNext)
                .setStyle(style)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(isPlaying)

            if (check == 0) {
                Glide.with(context).asBitmap().load(music.imageUrl).into(object :
                    CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                        notificationBuilder.setLargeIcon(resource)
                        val notification = notificationBuilder.build()
                        notificationManagerCompat.notify(1, notification)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
            } else {
                val img = music.imageUrl?.let { getAlbumArt(it) }
                Glide.with(context).asBitmap().load(img).into(object :
                    CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {

                        notificationBuilder.setLargeIcon(resource)
                        val notification = notificationBuilder.build()
                        notificationManagerCompat.notify(1, notification)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
            }


        }
    }

    private fun getAlbumArt(uri: String): ByteArray? {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val art: ByteArray? = retriever.embeddedPicture
        retriever.release()

        return art
    }
}