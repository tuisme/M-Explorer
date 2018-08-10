package vinova.intern.nhomxnxx.mexplorer.service



import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import java.io.File

import retrofit2.Call
import vinova.intern.nhomxnxx.mexplorer.R
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.model.BaseResponse
import vinova.intern.nhomxnxx.mexplorer.utils.FileUtils
import vinova.intern.nhomxnxx.mexplorer.utils.ProgressRequestBody
import vinova.intern.nhomxnxx.mexplorer.utils.Support


class UploadFileService : IntentService("Upload File Service"), ProgressRequestBody.UploadCallbacks {
    override fun onProgressUpdate(percentage: Int, mFile: File) {
        sendNotification(percentage)
    }

    override fun onError() {
        onUploadFail()
    }

    override fun onFinish() {
        onUploadComplete()
    }

    private var notificationBuilder: NotificationCompat.Builder? = null
    private var notificationManager: NotificationManager? = null
    var percentage:Int = 0
    lateinit var uri:Uri

    lateinit var name:String
    lateinit var userToken:String
    lateinit var id:String
    lateinit var cloudType:String
    lateinit var ctoken:String
    lateinit var body : MultipartBody.Part

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        uri = Uri.parse(intent?.getStringExtra("uri"))
        userToken = intent?.getStringExtra("user_token").toString()
        id = intent?.getStringExtra("id").toString()
        cloudType = intent?.getStringExtra("ctype").toString()
        ctoken = intent?.getStringExtra("ctoken").toString()
        val file = FileUtils.getFile(this,uri)
        name = file?.name.toString()
        val requestBody = ProgressRequestBody(file!!, this,this, Support.getMimeType(this, uri).toString())
        body = MultipartBody.Part.createFormData("file", file.name, requestBody)
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onHandleIntent(intent: Intent?) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = "my_channel_02"
            val name1 = "Channel2"
            val description = "My channel2"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name1, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager!!.createNotificationChannel(mChannel)
        }
        val CHANNEL_ID = "my_channel_02"

        notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo_img)
                .setContentTitle(name)
                .setContentText("Uploading file")
                .setChannelId(CHANNEL_ID)
                .setPriority(1)
                .setAutoCancel(true)
        notificationManager!!.notify(0, notificationBuilder!!.build())
        initUpload()
    }

    private fun initUpload() {
        CallApi.getInstance().uploadFile(userToken, id, body, cloudType, ctoken)
                .enqueue(object : Callback<BaseResponse> {
                    override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
                        onUploadFail()
                    }

                    override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
                        if (response?.body()?.status.equals("success")) {
                            Log.e("ABCD", response?.message())
                            onUploadComplete()
                        } else {
                            Log.e("ABCD", response?.errorBody()?.string()!!)
                        }
                    }
                })
    }

    fun sendNotification(percentage: Int) {

        notificationBuilder!!.setProgress(100, percentage, false)
        notificationBuilder!!.setContentText("Uploaded $percentage% ")
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }


    fun onUploadComplete() {
        notificationManager!!.cancel(0)
        notificationBuilder!!.setProgress(0, 0, false)
        notificationBuilder!!.setContentText("File Uploaded")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_SOUND)
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }

    fun onUploadFail() {
        notificationManager!!.cancel(0)
        notificationBuilder!!.setProgress(0, 0, false)
        notificationBuilder!!.setContentText("Upload Fail")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setDefaults(Notification.DEFAULT_SOUND)
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        notificationManager!!.cancel(0)
    }


}


