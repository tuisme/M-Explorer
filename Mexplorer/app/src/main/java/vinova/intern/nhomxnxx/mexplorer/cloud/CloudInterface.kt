package vinova.intern.nhomxnxx.mexplorer.cloud

import android.content.Context
import android.content.Intent
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
		fun downloadFile(url:String,name:String,ctype: String)
	}

	interface Presenter : BasePresenter{
		fun getList(id:String,token:String,userToken:String,type : String)
		fun openFile(context: Context, url: String)
		fun getUrlFile(id : String,ctoken:String,user_token:String,ctype : String)
		fun upLoadFile(user_token: String,id: String,uri: Uri,ctype: String,ctoken : String)
		fun download(id : String,ctoken:String,user_token:String,ctype : String)
		fun renameFile(user_token: String,id: String,fname: String,ctype: String,ctoken: String)
		fun renameFolder(userToken: String, id: String, newName: String, cloudType: String, ctoken: String)
		fun createFolder(user_token: String,fname: String,parent: String,ctype: String,ctoken: String)
		fun deleteFile(user_token: String, id: String, ctype: String, ctoken: String)
		fun deleteFolder(userToken: String, id: String, cloudType: String, ctoken: String)
		fun upLoadFolder(user_token: String,ctoken: String,ctype: String,id: String,path : String)
		fun saveImage(data: Intent?, user_token: String, id: String, ctype: String, ctoken : String)
		fun moveOrCopy(idItem: String, mCopy: Boolean,user_token: String, cloudType: String,ctoken: String , idDest: String, isDic:Boolean)
	}
}