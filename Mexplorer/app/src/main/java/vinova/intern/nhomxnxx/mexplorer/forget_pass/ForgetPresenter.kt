package vinova.intern.nhomxnxx.mexplorer.forget_pass

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.model.Request

class ForgetPresenter(view : ForgetInterface.View):ForgetInterface.Presenter {
	val mView : ForgetInterface.View = view
	init {
		mView.setPresenter(this)
	}
	override fun sendRequestReset(email:String?) {
		if (email!=null)
			CallApi.createService().requestNewPass(email)
					.enqueue(object : Callback<Request>{
						override fun onFailure(call: Call<Request>?, t: Throwable?) {

						}

						override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
							if (response?.body()?.status.equals("success")){
								mView.requestSuccess()
							}
							else{
								mView.showError(response?.message().toString())
							}
						}
					})
	}
}