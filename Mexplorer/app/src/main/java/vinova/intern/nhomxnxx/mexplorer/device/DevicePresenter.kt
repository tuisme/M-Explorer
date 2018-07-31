package vinova.intern.nhomxnxx.mexplorer.device

import android.content.Context
import com.facebook.login.LoginManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.ListDevice
import vinova.intern.nhomxnxx.mexplorer.model.Request

class DevicePresenter (view: DeviceInterface.View): DeviceInterface.Presenter{
    val mView: DeviceInterface.View = view

    init {
    	mView.setPresenter(this)
    }

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

    override fun logout(context: Context?, token: String?) {
        val db = DatabaseHandler(context)
        if (token!=null)
            CallApi.getInstance().logout(token)
                    .enqueue(object : Callback<Request> {
                        override fun onFailure(call: Call<Request>?, t: Throwable?) {
                            mView.showError(t.toString())
                        }

                        override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                            if (response?.body()?.status.equals("success")) {
                                LoginManager.getInstance().logOut()

                                db.deleteUserData(token)
                            }
                            else
                                mView.showError(response?.message().toString())
                        }
                    })
    }
}