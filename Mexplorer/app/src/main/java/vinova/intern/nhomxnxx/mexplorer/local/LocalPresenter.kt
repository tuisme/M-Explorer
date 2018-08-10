package vinova.intern.nhomxnxx.mexplorer.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import com.facebook.login.LoginManager
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.adapter.LocalAdapter
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.Request
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class LocalPresenter(view:LocalInterface.View,ctx : Context):LocalInterface.Presenter{
    val context = ctx
    val databaseAccess = DatabaseHandler(context)

    override fun openFileOrFolder(adapter: LocalAdapter, file: File) {
        if (file.isDirectory) {
            adapter.path = file.absolutePath
            //listDir(this.path, holder, position)
            adapter.refreshData()
        } else if (file.isFile) {
            mView.openFile(file)
        }
    }
    override fun saveImage(data: Intent?, adapter: LocalAdapter) {
        val extras = data?.extras
        val imageBitmap = extras?.get("data") as Bitmap? ?: return
        val bytes = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val file = createImageFile(adapter)
        try {
            file.createNewFile()
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            fo.close()
            adapter.refreshData()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(adapter:LocalAdapter): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName, /* prefix */
                ".png", /* suffix */
                File(adapter.path)      /* directory */
        )
    }

    override fun newFolder(adapter: LocalAdapter, name: String) {
        val currentPath = adapter.path
        val folderPath = currentPath + File.separator + name
        val created:Boolean =
                if (File(folderPath).exists()) {
                    false
                }
                else File(folderPath).mkdirs()

        if (created) {
            adapter.refreshData()
            mView.showToast("New folder created: $name")
        } else {
            mView.showToast("Failed create folder: $name")
        }
    }

    override fun newFile(path: String,name:String, content: String) {
        mView.showLoading(true)
        val folderPath = path + File.separator + name
        createFile(folderPath, content.toByteArray())
        mView.showLoading(false)
        mView.showToast("New file created: $name")
    }


    private fun createFile(path: String, content_: ByteArray): Boolean {
        val content = content_
        try {
            val stream = FileOutputStream(File(path))
            stream.write(content)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            return false
        }
        return true
    }

    override fun rename(fromPath: String, toPath: String) {
        mView.showLoading(true)
        val file = File(fromPath)
        val newFile = File(toPath)
        file.renameTo(newFile)
        mView.showLoading(false)
        mView.showToast("Renamed")
    }

    override fun delete(path: String) {
        mView.showLoading(true)
        if (File(path).isDirectory) {
            deleteDirectoryImpl(path)
            mView.showLoading(false)
            mView.showToast("Folder was deleted")
        } else {
            val file = File(path)
            file.delete()
            mView.showLoading(false)
            mView.showToast("File was deleted")
        }
    }

    override fun moveOrCopy(mMovingPath: String?, mCopy: Boolean, adapter: LocalAdapter) {
        mView.showLoading(true)
        if (mMovingPath != null) {
            var toPath = adapter.path + File.separator + File(mMovingPath).name
            if (!mCopy) {
                if (mMovingPath != toPath) {
                    if (copy(mMovingPath, toPath)) {
                        File(mMovingPath).delete()
                    }
                    mView.showLoading(false)

                    mView.showToast("Moved")
                    adapter.refreshData()

                } else {
                    mView.showLoading(false)
                    mView.showToast("The file is already here")
                }
            }
            else {

                if (mMovingPath != toPath) {
                    copy(mMovingPath, toPath)
                    mView.showLoading(false)
                    mView.showToast("Copied")
                    adapter.refreshData()
                }
                else {
                    toPath = adapter.path + File.separator +"copy"+ File(mMovingPath).name
                    copy(mMovingPath, toPath)
                    mView.showLoading(false)
                    mView.showToast("Copied")
                    adapter.refreshData()
                }
            }
        }
    }


    private fun deleteDirectoryImpl(path: String) {
        val directory = File(path)

        // If the directory exists then delete
        if (directory.exists()) {
            val files = directory.listFiles()
            // Run on all sub files and folders and delete them
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    deleteDirectoryImpl(files[i].absolutePath)
                } else {
                    files[i].delete()
                }
            }
        }
        directory.delete()
    }

    val mView: LocalInterface.View = view

    init {
        mView.setPresenter(this)
    }

    fun copy(fromPath: String, toPath: String):Boolean {
        val file = File(fromPath)
        if (!file.isFile) {
            return false
        }
        var inStream: FileInputStream? = null
        var outStream: FileOutputStream? = null
        try {
            inStream = FileInputStream(file)
            outStream = FileOutputStream(File(toPath))
            val inChannel = inStream.channel
            val outChannel = outStream.channel
            inChannel.transferTo(0, inChannel.size(), outChannel)
        } catch (e: Exception) {
            return false
        } finally {
            closeSilently(inStream)
            closeSilently(outStream)
        }
        return true    }

    private fun closeSilently(closeable: Closeable?) {
        if (closeable != null) {
            try {
                closeable.close()
            } catch (e: IOException) {
            }

        }
    }

    override fun logout(context: Context?, token: String?) {
        val db = DatabaseHandler(context)
        if (token!=null)
            CallApi.getInstance().logout(token)
                    .enqueue(object : Callback<Request> {
                        override fun onFailure(call: Call<Request>?, t: Throwable?) {

                        }

                        override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                            if (response?.body()?.status.equals("success")) {
                                LoginManager.getInstance().logOut()
                                mView.showLoading(false)
                                mView.logoutSuccess()
                                db.deleteUserData(token)
                            }
                            else
                                mView.showError(response?.message().toString())
                        }
                    })
    }

    override fun updateUser(first_name: String, last_name: String, uri: Uri) {
        val file = File(uri.path)

        val requestBody = RequestBody.create(
                MediaType.parse("file/*"),
                file)
        val userToken = databaseAccess.getToken()!!

        val avatar = MultipartBody.Part.createFormData("avatar", file.name, requestBody)

        CallApi.getInstance().updateUsesr(userToken,first_name, last_name, avatar)
                .enqueue(object : Callback<Request>{
                    override fun onFailure(call: Call<Request>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                        if (response?.body() != null){
                            val user = response.body()?.data
                            if (user != null) {
                                if (databaseAccess.getUserLoggedIn() != null) {
                                    databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn())
                                }
                                databaseAccess.insertUserData(user.token, user.email, user.first_name,
                                        user.last_name, DatabaseHandler.NORMAL, DatabaseHandler.LOGGING_IN,
                                        user.avatar_url,user.is_vip.toString(),user.used,user.mentAuth,0,user.allocated)
                                mView.updateUser()
                            }
                        }
                        else
                            mView.showError(response?.message()!!)
                    }

                })
    }

    override fun redeem(user_token: String) {
        CallApi.getInstance().redeemSpace(user_token)
                .enqueue(object : Callback<Request>{
                    override fun onFailure(call: Call<Request>?, t: Throwable?) {

                    }

                    override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                        if (response?.body() != null){
                            val user = response.body()?.data
                            if (user != null) {
                                if (databaseAccess.getUserLoggedIn() != null) {
                                    databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn())
                                }
                                databaseAccess.insertUserData(user.token, user.email, user.first_name,
                                        user.last_name, DatabaseHandler.NORMAL, DatabaseHandler.LOGGING_IN,
                                        user.avatar_url,user.is_vip.toString(),user.used,user.mentAuth,0,user.allocated)
                                mView.updateUser()
                            }
                        }
                        else
                            mView.showError(response?.message()!!)
                    }

                })
    }
}