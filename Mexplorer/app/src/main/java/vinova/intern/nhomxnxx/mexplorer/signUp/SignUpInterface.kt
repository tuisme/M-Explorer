package vinova.intern.nhomxnxx.mexplorer.signUp

import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView

interface SignUpInterface {
    interface View: BaseView<Presenter> {
        fun signUpSuccess(bool:Boolean)
    }

    interface Presenter{
        fun signUp(firstName: String, lastName:String, email:String, password:String)

        }
}