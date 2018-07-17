package vinova.intern.nhomxnxx.mexplorer.signUp

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.model.User

class SignInPresenter(view: SignInInterface.View) :SignInInterface.Presenter{
    val mView: SignInInterface.View = view

    init {
        mView.setPresenter(this)
    }
    override fun signIn(email:String, password:String){
        val api = CallApi.createService()
        api.logIn(email, password)
                .enqueue(object:Callback<User>{
                    override fun onFailure(call: Call<User>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<User>?, response: Response<User>?) {
                        if(response?.body() != null){
                            mView.signInSuccess(response.body() as User)
                        }
                        else {
                            Log.e("HUHU", response?.errorBody()?.string().toString())
                            mView.showError("Validation failed: Email has already been taken")

                        }
                    }

                })
    }
}