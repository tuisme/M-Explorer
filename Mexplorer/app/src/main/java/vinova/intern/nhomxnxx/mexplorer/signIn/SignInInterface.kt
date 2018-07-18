package vinova.intern.nhomxnxx.mexplorer.signIn

import android.content.Context
import com.facebook.login.LoginResult
import vinova.intern.best_trip.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.User

interface SignInInterface {
    interface View: BaseView<Presenter>{
        fun signInSuccess(user:User)
    }

    interface Presenter{
        fun signIn(context: Context?, email:String, password:String)
        fun handleFacebookAccessToken(result: LoginResult)
    }
}