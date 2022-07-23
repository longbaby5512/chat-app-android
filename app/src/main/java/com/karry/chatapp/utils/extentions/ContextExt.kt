@file:Suppress("SpellCheckingInspection", "unused", "NOTHING_TO_INLINE")

package com.karry.chatapp.utils.extentions

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.Toast
import androidx.annotation.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.use
import androidx.core.text.HtmlCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import com.karry.chatapp.R
import com.karry.chatapp.utils.CHANNEL_ID
import com.karry.chatapp.utils.NOTIFICATION_ID
import com.karry.chatapp.utils.NOTIFICATION_ID_BIG_IMAGE
import com.karry.chatapp.utils.appendNotificationMessages
import com.karry.chatapp.utils.storage.NotificationPreferencesStorage
import javax.inject.Inject
import kotlin.math.roundToInt


val Context.isOrientationPortrait get() = this.resources.configuration.orientation == ORIENTATION_PORTRAIT

@ColorInt
inline fun Context.getColorBy(@ColorRes id: Int) = ContextCompat.getColor(this, id)

inline fun Context.getDrawableBy(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

@ColorInt
@SuppressLint("Recycle")
inline fun Context.themeColor(@AttrRes themeAttrId: Int): Int =
    obtainStyledAttributes(intArrayOf(themeAttrId)).use { it.getColor(0, Color.TRANSPARENT) }


inline fun Context.uriFromResourceId(@AnyRes resId: Int): Uri? {
    return runCatching {
        val res = this@uriFromResourceId.resources
        Uri.parse(
            ContentResolver.SCHEME_ANDROID_RESOURCE +
                    "://" + res.getResourcePackageName(resId) +
                    '/' + res.getResourceTypeName(resId) +
                    '/' + res.getResourceEntryName(resId)
        )
    }.getOrNull()
}

inline fun Context.dpToPx(dp: Int): Int {
    val displayMetrics = resources.displayMetrics
    return (dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

inline fun Context.toast(
    @StringRes messageRes: Int,
    short: Boolean = true,
) = this.toast(getString(messageRes), short)

inline fun Context.toast(
    message: String,
    short: Boolean = true,
) =
    Toast.makeText(
        this,
        message,
        if (short) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
    ).apply { show() }!!

@SuppressLint("Recycle")
inline fun Context.themeInterpolator(@AttrRes attr: Int): Interpolator {
    return AnimationUtils.loadInterpolator(
        this,
        obtainStyledAttributes(intArrayOf(attr)).use {
            it.getResourceId(0, android.R.interpolator.fast_out_slow_in)
        }
    )
}

@Suppress("unused")
inline val Any?.unit
    get() = Unit



/**
 * Start enter transitions that were postponed for this fragment when its content has been redrawn.
 * This is meant to be used when the data backing a RecyclerView
 * has been updated for the first time.
 *
 * See [https://developer.android.com/training/basics/fragments/animate#recyclerview]
 */
inline fun Fragment.startPostponedEnterTransitionWhenDrawn() {
    (requireView().parent as? ViewGroup)?.doOnPreDraw {
        startPostponedEnterTransition()
    }
}


private fun Context.createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelName = "Karry"
        val channelId = CHANNEL_ID
        val channelDescription = "Karry"
        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        channel.description = channelDescription
        channel.setShowBadge(true)
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun Context.showSmallNotification(
    notificationBuilder: NotificationCompat.Builder,
    @DrawableRes icon: Int,
    title: String,
    message: String,
    timestamp: String,
    pendingIntent: PendingIntent,
    alarmSound: Uri,
    storage: NotificationPreferencesStorage,
) {
    val inboxStyle = NotificationCompat.InboxStyle()
    if (appendNotificationMessages) {
        val oldNotification = storage.notifications
        val messages = oldNotification.split("\\|")
        for (i in messages.size - 1 downTo 0) {
            inboxStyle.addLine(messages[i])
        }
    } else {
        inboxStyle.addLine(message)
    }
    val notification = notificationBuilder
        .setSmallIcon(R.mipmap.ic_launcher)
        .setLargeIcon(BitmapFactory.decodeResource(resources, icon))
        .setContentTitle(title)
        .setContentText(message)
        .setSound(alarmSound)
        .setContentIntent(pendingIntent)
        .setStyle(inboxStyle)
        .setWhen(timestamp.toTimeMiliSec())
        .build()
    with(NotificationManagerCompat.from(this)) {
        notify(NOTIFICATION_ID, notification)
    }
}

private fun Context.showBigNotification(
    bitmap: Bitmap,
    notificationBuilder: NotificationCompat.Builder,
    @DrawableRes icon: Int,
    title: String,
    message: String,
    timestamp: String,
    pendingIntent: PendingIntent,
    alarmSound: Uri
) {
    val bigPictureStyle = NotificationCompat.BigPictureStyle()
        .setBigContentTitle(title)
        .setSummaryText(Html.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY).toString())
        .bigPicture(bitmap)

    val notification = notificationBuilder
        .setSmallIcon(R.mipmap.ic_launcher)
        .setTicker(title)
        .setWhen(0)
        .setAutoCancel(true)
        .setContentTitle(title)
        .setContentIntent(pendingIntent)
        .setSound(alarmSound)
        .setStyle(bigPictureStyle)
        .setWhen(timestamp.toTimeMiliSec())
        .setSmallIcon(R.mipmap.ic_launcher)
        .setLargeIcon(BitmapFactory.decodeResource(resources, icon))
        .setContentText(message)
        .build()
    with(NotificationManagerCompat.from(this)) {
        notify(NOTIFICATION_ID_BIG_IMAGE, notification)
    }
}

fun Context.showNotification(
    title: String,
    message: String,
    timestamp: String,
    intent: Intent,
    imageUrl: String? = null,
    storage: NotificationPreferencesStorage
) {
    if (message.isEmpty()) {
        return
    }
    createNotificationChannel()

    val icon = R.mipmap.ic_launcher

    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
    val pendingIntent =
        PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    val alarmSound =
        Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/raw/notification")
    val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
    if (!TextUtils.isEmpty(imageUrl)) {
        if (imageUrl.isNotNull() && imageUrl!!.length > 4 && Patterns.WEB_URL.matcher(imageUrl)
                .matches()
        ) {
            val bitmap = imageUrl.getBitmapFromURL()
            if (bitmap != null) {
                showBigNotification(
                    bitmap = bitmap,
                    notificationBuilder = notificationBuilder,
                    title = title,
                    message = message,
                    timestamp = timestamp,
                    pendingIntent = pendingIntent,
                    alarmSound = alarmSound,
                    icon = icon
                )
            } else {
                showSmallNotification(
                    notificationBuilder = notificationBuilder,
                    title = title,
                    message = message,
                    timestamp = timestamp,
                    pendingIntent = pendingIntent,
                    alarmSound = alarmSound,
                    icon = icon,
                    storage = storage
                )
            }
        }
    } else {
        showSmallNotification(
            notificationBuilder = notificationBuilder,
            title = title,
            message = message,
            timestamp = timestamp,
            pendingIntent = pendingIntent,
            alarmSound = alarmSound,
            icon = icon,
            storage = storage
        )
        playNotificationSound()
    }
}

fun Context.clearAllNotifications() {
    with(NotificationManagerCompat.from(this)) {
        cancelAll()
    }
}

fun Context.playNotificationSound() {
    try {
        val alarmSound = Uri.parse(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://" +
                    "${packageName}/raw/notification"
        )
        val ringtone = RingtoneManager.getRingtone(this, alarmSound)
        ringtone.play()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

val Context.isInBackground: Boolean
    get() {
        var isInBackground = true
        val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        am.runningAppProcesses?.forEach { processInfo ->
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                processInfo.pkgList?.forEach { pkgName ->
                    if (pkgName == packageName) {
                        isInBackground = false
                    }
                }
            }
        }
        return isInBackground
    }

val Context.isConnectInternet: Boolean
    get() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }