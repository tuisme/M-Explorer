package vinova.intern.nhomxnxx.mexplorer.cloud

import android.content.Context
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.FileDetail
import vinova.intern.nhomxnxx.mexplorer.model.FileSec

interface CloudInterface {

	interface View : BaseView<Presenter>{
		fun showList(files : List<FileSec>)
		fun showFile(file : FileDetail)
		fun logoutSuccess()
	}

	interface Presenter{
		fun getList(id:String,token:String,userToken:String,type : String)
		fun openFile(context: Context, url: String)
		fun getUrlFile(id : String,ctoken:String,user_token:String,ctype : String)
		fun logout(context: Context?, token: String?)
	}
}