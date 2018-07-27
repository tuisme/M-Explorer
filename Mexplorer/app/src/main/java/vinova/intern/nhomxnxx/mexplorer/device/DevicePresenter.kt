package vinova.intern.nhomxnxx.mexplorer.device

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.model.ListDevice

class DevicePresenter (view: DeviceInterface.View): DeviceInterface.Presenter{
    val mView: DeviceInterface.View = view

    override fun getDevice(token: String?) {
        if (token != null) {
            CallApi.getInstance().getDevices(token)
                    .enqueue(object : Callback<ListDevice> {
                        override fun onFailure(call: Call<ListDevice>?, t: Throwable?) {
                        }

                        override fun onResponse(call: Call<ListDevice>?, response: Response<ListDevice>?) {
                            if (response?.body() != null)
                                if (response.body()?.status.equals("success"))
                                    mView.showList(response.body()?.data!!)
                                else
                                    mView.showError(response.message())
                        }
                    })
        }
    }
}