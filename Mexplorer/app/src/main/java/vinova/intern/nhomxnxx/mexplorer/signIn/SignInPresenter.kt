package vinova.intern.nhomxnxx.mexplorer.signIn

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.User

class SignInPresenter(view: SignInInterface.View) :SignInInterface.Presenter{
    val mView: SignInInterface.View = view

    init {
        mView.setPresenter(this)
    }
    override fun signIn(context: Context?, email:String, password:String){
        val databaseAccess = DatabaseHandler(context)
        val api = CallApi.createService()
        api.logIn(email, password)
                .enqueue(object:Callback<User>{
                    override fun onFailure(call: Call<User>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<User>?, response: Response<User>?) {
                        if(response?.body() != null){
                            val user = response.body()
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
                            mView.showError("Validation failed: Email has already been taken")
                        }
                    }

                })
    }
}