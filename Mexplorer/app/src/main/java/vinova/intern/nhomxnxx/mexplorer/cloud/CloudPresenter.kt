package vinova.intern.nhomxnxx.mexplorer.cloud

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.model.SpecificCloud

class CloudPresenter(view : CloudInterface.View):CloudInterface.Presenter {
	val mView = view
	init {
		mView.setPresenter(this)
	}
	override fun getList(id:String,token:String,userToken:String) {
		CallApi.getInstance().gotoCloud(id,token,userToken)
				.enqueue(object : Callback<SpecificCloud>{
					override fun onFailure(call: Call<SpecificCloud>?, t: Throwable?) {

					}

					override fun onResponse(call: Call<SpecificCloud>?, response: Response<SpecificCloud>?) {
						if (response?.body() != null)
							if (response.body()?.status.equals("success"))
								mView.showList(response.body()?.data!!)
						else
							mView.showError(response.message())
					}

				})
	}

}