package vinova.intern.nhomxnxx.mexplorer.signIn

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.firebase.iid.FirebaseInstanceId
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.Request


class SignInPresenter(view: SignInInterface.View) :SignInInterface.Presenter{
    val mView: SignInInterface.View = view
    lateinit var androidId : String

    init {
        mView.setPresenter(this)
	    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
		    if (it.isSuccessful)
			    androidId = it.result.token
	    }
    }

    @SuppressLint("HardwareIds")
    override fun signIn(context: Context?, email:String, password:String,location : String){
        val databaseAccess = DatabaseHandler(context)
        val api = CallApi.getInstance()
        val androidName = android.os.Build.MODEL
        api.logIn(email, password, androidId, androidName,"android",location)
                .enqueue(object:Callback<Request>{
                    override fun onFailure(call: Call<Request>?, t: Throwable?) {
                        Log.e("ABCD",t.toString())
                    }

                    override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                        if(response?.body()?.status.toString() == "success"){
                            val user = response?.body()?.data
                            if (user != null) {
                                if (databaseAccess.getUserLoggedIn() != null) {
                                    databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn())
                                }
                                databaseAccess.insertUserData(user.token, user.email, user.first_name,
                                        user.last_name, DatabaseHandler.NORMAL, DatabaseHandler.LOGGING_IN,
                                        user.avatar_url,user.is_vip.toString(),user.used,null,0,user.allocated)
                                mView.signInSuccess()
                            }
                        }
                        else {
                            mView.showError(response?.message().toString())
                        }
                    }

                })
    }

    @SuppressLint("HardwareIds")
    override fun handleFacebookAccessToken(result: LoginResult, context: Context?,location : String) {
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { obj, _ ->
            val provider = "facebook"
            val databaseAccess = DatabaseHandler(context)
            Log.e("ABCD",obj.toString())
            val email = obj?.getString("email").toString()
            val firstName = obj?.getString("first_name").toString()
            val lastName = obj?.getString("last_name").toString()
            val androidName = android.os.Build.MODEL
            val api = CallApi.getInstance()
            api.logInProvider(provider, email,firstName,lastName,androidId,androidName,"android",location)
                    .enqueue(object:Callback<Request>{
                        override fun onFailure(call: Call<Request>?, t: Throwable?) {

                        }

                        override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                            if(response?.body()?.status.toString() == "success"){
                                val user = response?.body()?.data
                                if (user != null) {
                                    if (databaseAccess.getUserLoggedIn() != null) {
                                        databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn())
                                    }
                                    databaseAccess.insertUserData(user.token, user.email, user.first_name,
                                            user.last_name, DatabaseHandler.FACEBOOK, DatabaseHandler.LOGGING_IN,
                                            user.avatar_url
                                            ,user.is_vip.toString(),user.used,null,0,user.allocated)
                                    mView.signInSuccess()

                                }
                                else{
                                    mView.showError("Can't find user")
                                }
                            }
                            else {
                                mView.showError(response?.message().toString())
                            }
                        }

                    })
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name,picture.type(large)")
        request.parameters = parameters
        request.executeAsync()
    }

    @SuppressLint("HardwareIds")
    override fun handleGoogleSignInResult(result: GoogleSignInResult, context:Context ,location : String){
        if (result.isSuccess){
            val databaseAccess = DatabaseHandler(context)
            val account: GoogleSignInAccount = result.signInAccount!!
            val email = account.email
            val first_name = account.familyName
            val last_name = account.givenName
            val androidName = android.os.Build.MODEL
            val api = CallApi.getInstance()
            api.logInProvider("google",email!!, first_name.toString(), last_name.toString(),androidId,androidName,"android",location)
                    .enqueue(object:Callback<Request> {
                        override fun onFailure(call: Call<Request>?, t: Throwable?) {

                        }

                        override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                            if (response?.body()?.status.toString() == "success") {
                                val user = response?.body()?.data
                                if (user != null) {
                                    if (databaseAccess.getUserLoggedIn() != null) {
                                        databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn()!!)
                                    }
                                    databaseAccess.insertUserData(user.token, user.email, user.first_name,
                                            user.last_name, DatabaseHandler.GOOGLE, DatabaseHandler.LOGGING_IN,user.avatar_url,
                                            user.is_vip.toString(),user.used,null,0,user.allocated)
                                    mView.signInSuccess()
                                }
                                else {
                                    mView.showError(response?.body()?.message.toString())
                                }
                            }
                            else
                                mView.showError(response?.message().toString())
                        }
                    })
        }

    }
}