package vinova.intern.nhomxnxx.mexplorer.cloud

import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.File

interface CloudInterface {

	interface View : BaseView<Presenter>{
		fun showList(files : List<File>)
	}

	interface Presenter{
		fun getList(id:String,token:String,userToken:String)
	}
}