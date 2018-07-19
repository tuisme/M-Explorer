package vinova.intern.nhomxnxx.mexplorer.signIn

import android.content.Context
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import vinova.intern.nhomxnxx.mexplorer.model.User

interface SignInInterface {
    interface View: BaseView<Presenter> {
        fun signInSuccess(user:User)
    }

    interface Presenter{
        fun signIn(context: Context?, email:String, password:String)
        fun handleFacebookAccessToken(result: LoginResult, context: Context?)
        fun handleGoogleSignInResult(result: GoogleSignInResult, context: Context?)
    }
}