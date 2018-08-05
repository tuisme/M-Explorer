package vinova.intern.nhomxnxx.mexplorer.utils

import android.content.Context
import android.net.ConnectivityManager
import android.telephony.TelephonyManager

@Suppress("DEPRECATED_IDENTITY_EQUALS", "DEPRECATION")
class NetworkUtils {

    companion object {

        private val NETWORK_TYPE_NO_CONNECTION = -1
        private val NETWORK_TYPE_UNKNOWN = 0
        private val NETWORK_TYPE_2G = 1
        private val NETWORK_TYPE_3G = 2
        private val NETWORK_TYPE_4G = 3
        private val NETWORK_TYPE_WIFI = 4
        val messageNetWork = "No internet connection"


        fun isUseWifi(context: Context): Boolean {

            return NETWORK_TYPE_WIFI === getNetworkType(context)
        }

        fun isConnectedInternet(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = cm.activeNetworkInfo
            return netInfo != null && netInfo.isConnectedOrConnecting
        }


        private fun getNetworkType(context: Context): Int {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = cm.activeNetworkInfo
            if (info == null || !info.isConnected)
                return NETWORK_TYPE_NO_CONNECTION
            if (info.type == ConnectivityManager.TYPE_WIFI)
                return NETWORK_TYPE_WIFI
            if (info.type == ConnectivityManager.TYPE_MOBILE) {
                val networkType = info.subtype
                return when (networkType) {
                    TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> NETWORK_TYPE_2G
                    TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP -> NETWORK_TYPE_3G
                    TelephonyManager.NETWORK_TYPE_LTE -> NETWORK_TYPE_4G
                    else -> NETWORK_TYPE_UNKNOWN
                }
            }
            return NETWORK_TYPE_UNKNOWN
        }
    }
}