package vinova.intern.nhomxnxx.mexplorer.cloud

import android.content.Context
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.File
import vinova.intern.nhomxnxx.mexplorer.model.FileSec

interface CloudInterface {

	interface View : BaseView<Presenter>{
		fun showList(files : List<File>)
		fun showFile(file : FileSec)
		fun logoutSuccess()
	}

	interface Presenter{
		fun getList(id:String,token:String,userToken:String)
		fun openFile(context: Context, url: String)
		fun getUrlFile(id : String,token:String,user_token:String)
		fun logout(context: Context?, token: String?)
	}
}