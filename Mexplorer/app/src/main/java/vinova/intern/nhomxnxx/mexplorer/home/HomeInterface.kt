package vinova.intern.nhomxnxx.mexplorer.home

import android.content.Context
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.ListCloud

interface HomeInterface {
    interface View: BaseView<Presenter> {
        fun logoutSuccess()
        fun showList(list : ListCloud?)
    }

    interface Presenter{
        fun logout(context: Context?, token: String?)
        fun getList(token: String?)
    }
}