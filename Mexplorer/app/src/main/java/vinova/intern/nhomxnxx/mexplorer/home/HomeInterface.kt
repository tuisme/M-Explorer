package vinova.intern.nhomxnxx.mexplorer.home

import android.content.Context
import vinova.intern.best_trip.baseInterface.BaseView

interface HomeInterface {
    interface View:BaseView<Presenter>{
        fun logoutSuccess()
    }

    interface Presenter{
        fun logout(context: Context?, token: String?)
    }
}