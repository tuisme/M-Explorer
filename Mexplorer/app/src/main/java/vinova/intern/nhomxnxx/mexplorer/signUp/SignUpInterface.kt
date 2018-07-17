package vinova.intern.nhomxnxx.mexplorer.signUp

import vinova.intern.best_trip.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.User

interface SignUpInterface {
    interface View: BaseView<Presenter>{
        fun signUpSuccess(user:User)
    }

    interface Presenter{
        fun signUp(firstName: String, lastName:String, email:String, password:String)

        }
}