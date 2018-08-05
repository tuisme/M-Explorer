package vinova.intern.nhomxnxx.mexplorer.utils

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import okhttp3.MediaType
import okio.BufferedSink
import okhttp3.RequestBody
import java.io.File
import java.io.FileInputStream
import java.io.IOException


@Suppress("NAME_SHADOWING", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class ProgressRequestBody(val mFile: File, val mListener: UploadCallbacks, val context: Context, val type:String) : RequestBody() {

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int, mFile: File)
        fun onError()
        fun onFinish()
    }

    override fun contentType(): MediaType? {
        return MediaType.parse(type)
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return mFile.length()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val fileLength = mFile.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fis = FileInputStream(mFile)
        var uploaded: Long = 0

        fis.use { fis ->
            var read = fis.read(buffer)
            val handler = Handler(Looper.getMainLooper())
            while (read != -1) {

                // update progress on UI thread
                handler.post(ProgressUpdater(uploaded, fileLength))

                uploaded += read.toLong()
                sink.write(buffer, 0, read)
                read = fis.read(buffer)
            }
        }
    }

    private inner class ProgressUpdater(private val mUploaded: Long, private val mTotal: Long) : Runnable {

        override fun run() {
            mListener.onProgressUpdate((100 * mUploaded / mTotal).toInt(),mFile)
        }
    }

    companion object {

        private val DEFAULT_BUFFER_SIZE = 2048
    }
}