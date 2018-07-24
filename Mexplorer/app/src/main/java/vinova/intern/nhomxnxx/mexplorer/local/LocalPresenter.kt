package vinova.intern.nhomxnxx.mexplorer.local

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.FileProvider
import com.facebook.FacebookSdk
import vinova.intern.nhomxnxx.mexplorer.adapter.LocalAdapter
import java.io.*

class LocalPresenter(view:LocalInterface.View):LocalInterface.Presenter{
    override fun openFileOrFolder(adapter: LocalAdapter, file: File) {
        if (file.isDirectory) {
            adapter.path = file.absolutePath
            //listDir(this.path, holder, position)
            adapter.refreshData()
        } else if (file.isFile) {
            mView.openFile(file)
        }
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



}