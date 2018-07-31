package vinova.intern.nhomxnxx.mexplorer.service



import android.annotation.TargetApi
import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import okhttp3.ResponseBody

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.home.HomeActivity
import vinova.intern.nhomxnxx.mexplorer.model.Download
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Path


class DownloadService : IntentService("Download Service") {

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var totalFileSize: Int = 0
    lateinit var url:String
    lateinit var name:String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        name = intent?.getStringExtra("name").toString()
        return super.onStartCommand(intent, flags, startId)
    }
    @TargetApi(Build.VERSION_CODES.O)
    override fun onHandleIntent(intent: Intent?) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val id = "my_channel_01"
        val name1 = getString(R.string.facebook_app_id)
        val description = getString(R.string.boxsdk_ssl_error_warning_ID_MISMATCH)
        val importance = NotificationManager.IMPORTANCE_LOW
        val mChannel =  NotificationChannel(id, name1,importance)
        mChannel.description = description
        mChannel.enableLights(true)
        mChannel.lightColor = Color.RED
        mChannel.enableVibration(true)
        mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager!!.createNotificationChannel(mChannel)
        val CHANNEL_ID = "my_channel_01"

        notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Download")
                .setContentText("Downloading File")
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
        notificationManager!!.notify(0, notificationBuilder!!.build())
        initDownload(name)

    }

    private fun initDownload(name:String) {

        val retrofit = Retrofit.Builder()
                .baseUrl("https://mexplorer.herokuapp.com")
                .build()

        val retrofitInterface = retrofit.create<RetrofitInterface>(RetrofitInterface::class.java)

        retrofitInterface.downloadFile(name).enqueue(object:Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val thread = Thread(Runnable {
                    try {
                        downloadFile(response?.body()!!, name)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
                    }
                })
                thread.start()

            }
        })

    }

    @Throws(IOException::class)
    private fun downloadFile(body: ResponseBody, name:String) {

        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis = BufferedInputStream(body.byteStream(), 1024 * 8)
        val currentPath = Environment.getExternalStorageDirectory().path
        val folderPath = currentPath + File.separator + "Temp"
        if (!File(folderPath).exists())
            File(folderPath).mkdirs()

        val outputFile = File(folderPath, name)
        val output = FileOutputStream(outputFile)
        var count: Int = bis.read(data)
        var total: Long = 0
        val startTime = System.currentTimeMillis()
        var timeCount = 1
        while (count  != -1) {

            total += count.toLong()
            totalFileSize = (fileSize / Math.pow(1024.0, 2.0)).toInt()
            val current = Math.round(total / Math.pow(1024.0, 2.0)).toDouble()

            val progress = (total * 100 / fileSize).toInt()

            val currentTime = System.currentTimeMillis() - startTime

            val download = Download()
            download.totalFileSize = totalFileSize

            if (currentTime > 1000 * timeCount) {

                download.currentFileSize = current.toInt()
                download.progress =progress
                sendNotification(download)
                timeCount++
            }

            output.write(data, 0, count)
            count = bis.read(data)
        }
        onDownloadComplete()
        output.flush()
        output.close()
        bis.close()

    }
    private fun sendNotification(download: Download) {

        notificationBuilder!!.setProgress(100, download.progress, false)
        notificationBuilder!!.setContentText("Downloaded ${((download.progress))}% ")
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }


    private fun onDownloadComplete() {

        val download = Download()
        download.progress = 100

        notificationManager!!.cancel(0)
        notificationBuilder!!.setProgress(0, 0, false)
        notificationBuilder!!.setContentText("File Downloaded")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_SOUND)
        notificationManager!!.notify(0, notificationBuilder!!.build())

    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager!!.cancel(0)
    }

}

interface RetrofitInterface {
    @GET("/files/2/{name}")
    @Streaming
    fun downloadFile(@Path("name") name:String) :Call<ResponseBody>
}


