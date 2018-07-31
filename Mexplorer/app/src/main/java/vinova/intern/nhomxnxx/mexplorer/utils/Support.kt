package vinova.intern.nhomxnxx.mexplorer.utils

import java.text.DecimalFormat

class Support{
    companion object {
        fun getFileSize(size: Long): String {
            if (size <= 0)
                return "0"
            val units = arrayOf("B", "KB", "MB", "GB", "TB")
            val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
            return DecimalFormat("#,##0.#").format(size / Math.pow(1024.0, digitGroups.toDouble())) + " " + units[digitGroups]
        }
    }
}