package vinova.intern.nhomxnxx.mexplorer.signIn

import android.annotation.SuppressLint
import android.content.Context
import com.facebook.login.LoginResult
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.provider.Settings
import com.facebook.GraphRequest
import com.facebook.GraphResponse
import org.json.JSONObject
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.User
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facebook.AccessToken
import vinova.intern.nhomxnxx.mexplorer.model.Request


class SignInPresenter(view: SignInInterface.View) :SignInInterface.Presenter{
    val mView: SignInInterface.View = view

    init {
        mView.setPresenter(this)
    }
    @SuppressLint("HardwareIds")
    override fun signIn(context: Context?, email:String, password:String){
        val databaseAccess = DatabaseHandler(context)
        val api = CallApi.createService()
        val androidName = android.os.Build.MODEL
        val androidId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        api.logIn(email, password, androidName, androidId)
                .enqueue(object:Callback<Request>{
                    override fun onFailure(call: Call<Request>?, t: Throwable?) {
                        Toast.makeText(context, "NGu ",Toast.LENGTH_SHORT).show()
                    }

                    override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                        if(response?.body()?.status.toString() == "success"){
                            val user = response?.body()?.user
                            if (user != null) {
                                if (databaseAccess.getUserLoggedIn() != null) {
                                    databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn()!!)
                                }
                                databaseAccess.insertUserData(user.token, user.email, user.firstName,
                                        user.lastName, DatabaseHandler.NORMAL, DatabaseHandler.LOGGING_IN)
                                mView.signInSuccess(user)

                            }
                        }
                        else {
                            mView.showError(response?.body()?.message.toString())
                        }
                    }

                })
    }

    @SuppressLint("HardwareIds")
    override fun handleFacebookAccessToken(result: LoginResult, context: Context?) {
        val request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken()) { obj, _ ->
            val databaseAccess = DatabaseHandler(context)
            val email = obj?.getString("email").toString()
            val id = obj?.getString("id").toString()
            val firstName = obj?.getString("first_name").toString()
            val lastName = obj?.getString("last_name").toString()
            val androidName = android.os.Build.MODEL
            val androidId = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
            val api = CallApi.createService()
            api.logInWithFB(email,id,firstName,lastName,androidId,androidName)
                    .enqueue(object:Callback<Request>{
                        override fun onFailure(call: Call<Request>?, t: Throwable?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                            if(response?.body()?.status.toString() == "success"){
                                val user = response?.body()?.user
                                if (user != null) {
                                    if (databaseAccess.getUserLoggedIn() != null) {
                                        databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn()!!)
                                    }
                                    databaseAccess.insertUserData(user.token, user.email, user.firstName,
                                            user.lastName, DatabaseHandler.FACEBOOK, DatabaseHandler.LOGGING_IN)
                                    mView.signInSuccess(user)

                                }
                            }
                            else {
                                mView.showError(response?.body()?.message.toString())
                            }
                        }

                    })
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name")
        request.parameters = parameters
        request.executeAsync()
    }
}