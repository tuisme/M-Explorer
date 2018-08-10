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
import android.os.Environment
import android.provider.Settings
import android.webkit.MimeTypeMap
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.model.BaseResponse
import vinova.intern.nhomxnxx.mexplorer.utils.ProgressRequestBody
import vinova.intern.nhomxnxx.mexplorer.utils.Support
import java.io.Closeable
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class UploadFolderService : IntentService("Upload Folder Service"), ProgressRequestBody.UploadCallbacks {
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
    lateinit var folderRoot : String
    lateinit var path:String
    lateinit var userToken:String
    lateinit var id:String
    lateinit var cloudType:String
    lateinit var ctoken:String
    lateinit var body : MultipartBody.Part
    lateinit var root:File

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        path = intent?.getStringExtra("path").toString()
        userToken = intent?.getStringExtra("user_token").toString()
        id = intent?.getStringExtra("id").toString()
        cloudType = intent?.getStringExtra("ctype").toString()
        ctoken = intent?.getStringExtra("ctoken").toString()
        root = File(Environment.getExternalStorageDirectory().absolutePath+"/$path")

        return super.onStartCommand(intent, flags, startId)
    }
    override fun onHandleIntent(intent: Intent?) {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val id = "my_channel_03"
            val name1 = "Channel3"
            val description = "My channel3"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(id, name1, importance)
            mChannel.description = description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager!!.createNotificationChannel(mChannel)
        }
        val CHANNEL_ID = "my_channel_03"

        notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo_img)
                .setContentTitle(root.name)
                .setContentText("Please wait a minute to upload...")
                .setChannelId(CHANNEL_ID)
                .setPriority(1)
                .setAutoCancel(true)
        notificationManager!!.notify(0, notificationBuilder!!.build())
        initUpload()
    }

    private fun initUpload() {
        folderRoot = root.parent
        val zipPath = Environment.getExternalStorageDirectory().absolutePath+"/$path.zip"

        zip(root,zipPath)
        val file = File(zipPath)

        val map = JsonObject()
        map.add("root",listAllFilesAndFilesSubDirectory(root.path))

        val requestBody = ProgressRequestBody(file, this,this, Support.getMimeType(this, Uri.fromFile(file)).toString())
        body = MultipartBody.Part.createFormData("zip", file.name, requestBody)
        CallApi.getInstance().uploadFolder(userToken,id,map.toString(),body,cloudType,ctoken)
                .enqueue(object  : Callback<BaseResponse>{
                    override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
                        onUploadFail()
                    }

                    override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
                        onWait()
                        deleteFile(file)
                        onUploadComplete()
                    }
                })
    }

    fun sendNotification(percentage: Int) {

        notificationBuilder!!.setProgress(100, percentage, false)
        notificationBuilder!!.setContentText("Uploaded $percentage% ")
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }

    fun onWait() {
        notificationBuilder!!.setProgress(0, 0, false)
        notificationBuilder!!.setContentText("Please wait....")
        notificationManager!!.notify(0, notificationBuilder!!.build())
    }

    fun onUploadComplete() {
        notificationManager!!.cancel(0)
        notificationBuilder!!.setProgress(0, 0, false)
        notificationBuilder!!.setContentText("Folder Uploaded")
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

    private fun zip(directory : File,toLocation: String){
        val base = directory.toURI()
        val deque : Deque<File> = LinkedList<File>()
        deque.push(directory)
        val fos = FileOutputStream(toLocation)
        var res : Closeable = fos
        val buffer = ByteArray(1024)
        val zos = ZipOutputStream(fos)
        try{
            res = zos
            while (!deque.isEmpty()){
                val file : File = deque.pop()
                for (kid in file.listFiles()){
                    var name = base.relativize(kid.toURI()).path
                    if (kid.isDirectory){
                        deque.push(kid)
                        name = if (name.endsWith("/")) name else "$name/"
                        zos.putNextEntry(ZipEntry(name))
                    }
                    else{
                        zos.putNextEntry(ZipEntry(name))
                        val fis = FileInputStream(kid)
                        var count = fis.read(buffer)
                        while (count > 0){
                            zos.write(buffer,0,count)
                            count = fis.read(buffer)
                        }
                        fis.close()
                    }
                }
            }
        }
        catch (e : Exception){

        }
        finally {
            zos.closeEntry()
            res.close()

        }
    }

    private fun listAllFilesAndFilesSubDirectory(path : String): JsonArray {
        val directory = File(path)
        val array = JsonArray()
        val parent = JsonObject()
        parent.addProperty("parent",directory.name)
        array.add(parent)
        for (file : File in directory.listFiles()){
            when(file.isFile){
                true -> {
                    val fileObj  = JsonObject()
                    fileObj.addProperty("name",file.name)
                    fileObj.addProperty("path",file.path.substringAfter("$folderRoot/"))
                    array.add(fileObj)
                }
                false-> {
                    val fileObj  = JsonObject()
                    fileObj.add(file.name,listAllFilesAndFilesSubDirectory(file.path))
                    array.add(listAllFilesAndFilesSubDirectory(file.path))
                }
            }
        }
        return array
    }
    private fun deleteFile(file : File){
        file.delete()
        if (file.exists()){
            file.canonicalFile.delete()
            if (file.exists()){
                this.deleteFile(file.name)
            }
        }
    }

}


