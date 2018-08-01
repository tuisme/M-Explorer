package vinova.intern.nhomxnxx.mexplorer.cloud

import android.content.Context
import android.net.Uri
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BasePresenter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.FileDetail
import vinova.intern.nhomxnxx.mexplorer.model.FileSec

interface CloudInterface {

	interface View : BaseView<Presenter>{
		fun showList(files : List<FileSec>)
		fun showFile(file : FileDetail)
		fun logoutSuccess()
		fun refresh()
		fun downloadFile(name:String)
	}

	interface Presenter : BasePresenter{
		fun getList(id:String,token:String,userToken:String,type : String)
		fun openFile(context: Context, url: String)
		fun getUrlFile(id : String,ctoken:String,user_token:String,ctype : String)
		fun upLoadFile(user_token: String,id: String,uri: Uri,ctype: String,ctoken : String)
		fun download(id : String,ctoken:String,user_token:String,ctype : String)
		fun renameFile(user_token: String,id: String,fname: String,ctype: String,ctoken: String)
		fun createFolder(user_token: String,fname: String,parent: String,ctype: String,ctoken: String)
		fun deleteFile(user_token: String, id: String, ctype: String, ctoken: String)
	}
}