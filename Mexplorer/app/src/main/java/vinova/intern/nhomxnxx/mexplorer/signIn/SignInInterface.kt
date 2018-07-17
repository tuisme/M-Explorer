package vinova.intern.nhomxnxx.mexplorer.signUp

import vinova.intern.best_trip.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.User

interface SignInInterface {
    interface View: BaseView<Presenter>{
        fun signInSuccess(user:User)
    }

    interface Presenter{
        fun signIn(email:String, password:String)

        }
}