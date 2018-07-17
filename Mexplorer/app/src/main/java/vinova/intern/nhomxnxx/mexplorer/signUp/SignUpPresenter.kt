package vinova.intern.nhomxnxx.mexplorer.signUp

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.model.User

class SignUpPresenter(view: SignUpInterface.View) :SignUpInterface.Presenter{
    val mView: SignUpInterface.View = view

    init {
        mView.setPresenter(this)
    }
    override fun signUp(firstName: String, lastName:String, email:String, password:String){
        val api = CallApi.createService()
        api.signUp(firstName, lastName, email, password)
                .enqueue(object:Callback<User>{
                    override fun onFailure(call: Call<User>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<User>?, response: Response<User>?) {
                        if(response?.body() != null){
                            mView.signUpSuccess(response.body() as User)
                        }
                        else {
                            mView.showError("Validation failed: Email has already been taken")

                        }
                    }

                })
    }
}