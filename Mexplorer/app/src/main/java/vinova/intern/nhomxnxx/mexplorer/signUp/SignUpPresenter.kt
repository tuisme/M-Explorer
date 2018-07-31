package vinova.intern.nhomxnxx.mexplorer.signUp

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.model.Request

class SignUpPresenter(view: SignUpInterface.View) :SignUpInterface.Presenter{
    val mView: SignUpInterface.View = view

    init {
        mView.setPresenter(this)
    }
    override fun signUp(firstName: String, lastName:String, email:String, password:String){
        val api = CallApi.getInstance()
        api.signUp(firstName, lastName, email, password)
                .enqueue(object:Callback<Request>{
                    override fun onFailure(call: Call<Request>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                        if(response?.body() != null){
                            mView.signUpSuccess(response.body()?.status.toString() == "success")
                        }
                        else {
                            mView.showError("Validation failed: Email has already been taken")
                        }
                    }

                })
    }
}