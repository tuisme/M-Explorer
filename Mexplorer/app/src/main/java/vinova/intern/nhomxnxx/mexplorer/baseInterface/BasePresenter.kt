package vinova.intern.nhomxnxx.mexplorer.baseInterface

import android.content.Context
import android.net.Uri

interface BasePresenter {
	fun logout(context: Context?, token: String?)
	fun updateUser(first_name : String,last_name:String,uri:Uri)
}
