package vinova.intern.nhomxnxx.mexplorer.service



import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationCompat
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Streaming
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.Download


class DownloadService : IntentService("Download Service") {

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    private var totalFileSize: Int = 0
    lateinit var url:String
    lateinit var name:String
    lateinit var ctype:String

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        url = intent?.getStringExtra("url").toString()
        name = intent?.getStringExtra("name").toString()
        ctype = intent?.getStringExtra("ctype").toString()
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onHandleIntent(intent: Intent?) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = "my_channel_01"
            val name1 = "Channel"
            val description = "My channel"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name1, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager!!.createNotificationChannel(mChannel)
        }
        val CHANNEL_ID = "my_channel_01"

        notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo_img)
                .setContentTitle(name)
                .setContentText("Downloading File")
                .setChannelId(CHANNEL_ID)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
        notificationManager!!.notify(0, notificationBuilder!!.build())
        initDownload()

    }

    private fun initDownload() {

        val retrofit = Retrofit.Builder()
                .baseUrl("$url/")
                .build()

        val retrofitInterface = retrofit.create<RetrofitInterface>(RetrofitInterface::class.java)

        retrofitInterface.downloadFile().enqueue(object:Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                Toast.makeText(applicationContext,t.toString(),Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                val thread = Thread(Runnable {
                    try {
                        downloadFile(response?.body()!!)
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
    private fun downloadFile(body: ResponseBody) {

        val data = ByteArray(1024 * 4)
        val fileSize = body.contentLength()
        val bis = BufferedInputStream(body.byteStream(), 1024 * 8)
        var folderPath = Environment.getExternalStorageDirectory().path + File.separator + "Temp"
        when(ctype){
            "googledrive" -> {
                folderPath = folderPath +File.separator +"Google Drive"
                if (!File(folderPath).exists())
                    File(folderPath).mkdirs()
            }
            "dropbox" ->{
                folderPath = folderPath +File.separator +"DropBox"
                if (!File(folderPath).exists())
                    File(folderPath).mkdirs()
            }
            "onedrive" -> {
                folderPath = folderPath +File.separator +"OneDrive"
                if (!File(folderPath).exists())
                    File(folderPath).mkdirs()
            }
            "box" -> {
                folderPath = folderPath +File.separator +"Box"
                if (!File(folderPath).exists())
                    File(folderPath).mkdirs()
            }
        }


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
    @GET(".")
    @Streaming
    fun downloadFile() :Call<ResponseBody>
}


