package vinova.intern.nhomxnxx.mexplorer.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.DecimalFormat
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec


@Suppress("DEPRECATED_IDENTITY_EQUALS")
class Support{
    companion object {
        var iv = byteArrayOf(0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F)
        var keyy = byteArrayOf(0x00, 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, 0x00, 0x00, 0x55, 0x77, 0x55, 0x55, 0x77, 0x55)
        fun getFileSize(size: Long): String {
            if (size <= 0)
                return "0 KB"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
        }

        fun getMimeType(context: Context, uri: Uri): String? {
            val mimeType: String?
            mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                val cr = context.contentResolver
                cr.getType(uri)
            } else {
                val regex = Regex("[^A-Za-z0-9 .]")
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString().replace(regex, ""))
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        fileExtension.toLowerCase())
            }
            return mimeType
        }

        fun readFileToByteArray(file: File): ByteArray {
            val fis: FileInputStream?
            // Creating a byte array using the length of the file
            // file.length returns long which is cast to int
            val bArray = ByteArray(file.length().toInt())
            try {
                fis = FileInputStream(file)
                fis.read(bArray)
                fis.close()

            } catch (ioExp: IOException) {
                ioExp.printStackTrace()
            }

            return bArray
        }

        @Throws(Exception::class)
        fun encrypt(raw: ByteArray, clear: ByteArray): ByteArray {
            val skeySpec = SecretKeySpec(raw, "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec,IvParameterSpec(iv))
            return cipher.doFinal(clear)
        }

        @Throws(Exception::class)
        fun decrypt(raw: ByteArray, encrypted: ByteArray): ByteArray {
            val skeySpec = SecretKeySpec(raw, "AES")
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec,IvParameterSpec(iv))
            return cipher.doFinal(encrypted)
        }

        fun saveFile(data:ByteArray, outFileName:String) {
            var fos: FileOutputStream? = null
            val myDirectory = File(Environment.getExternalStorageDirectory().path+ "/Temp", ".auth")
            if (!myDirectory.exists()) {
                myDirectory.mkdirs()
            }
            try {
                fos = FileOutputStream(myDirectory.path + "/"+ outFileName)
                fos.write(data)

            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try {
                    fos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}