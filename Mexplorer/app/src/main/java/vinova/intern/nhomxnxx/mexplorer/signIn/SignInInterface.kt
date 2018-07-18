package vinova.intern.nhomxnxx.mexplorer.signIn

import android.content.Context
import vinova.intern.best_trip.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.User

interface SignInInterface {
    interface View: BaseView<Presenter>{
        fun signInSuccess(user:User)
    }

    interface Presenter{
        fun signIn(context: Context?, email:String, password:String)

        }
}