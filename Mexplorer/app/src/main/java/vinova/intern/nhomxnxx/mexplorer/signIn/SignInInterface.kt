package vinova.intern.nhomxnxx.mexplorer.signIn

import android.content.Context
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView

interface SignInInterface {
    interface View: BaseView<Presenter> {
        fun signInSuccess()
    }

    interface Presenter{
        fun signIn(context: Context?, email:String, password:String,location : String)
        fun handleFacebookAccessToken(result: LoginResult, context: Context?,location : String)
        fun handleGoogleSignInResult(result: GoogleSignInResult,context: Context,location : String)
    }
}