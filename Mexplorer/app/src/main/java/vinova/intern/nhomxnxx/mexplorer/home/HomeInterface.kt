package vinova.intern.nhomxnxx.mexplorer.home

import android.content.Context
import android.content.Intent
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BasePresenter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.ListCloud

interface HomeInterface {
    interface View: BaseView<Presenter> {
        fun logoutSuccess()
        fun forceLogOut(message : String)
        fun showList(list : ListCloud?)
        fun refreshList(list : ListCloud?)
        fun refresh()
        fun setSwitch(isChecked: Boolean)
        fun isAuth(isAuth: Boolean)
    }

    interface Presenter : BasePresenter{
        fun getList(token: String?)
        fun refreshList(token : String?)
        fun renameCloud(id:String,newName : String,token:String,userToken :String)
        fun deleteCloud(id:String,token:String)
        fun sendCode(code : String,name:String,userToken: String,provider:String)
        fun authentication(context: Context,data: Intent)
    }
}