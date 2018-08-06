package vinova.intern.nhomxnxx.mexplorer.device

import android.content.Context
import android.net.Uri
import com.facebook.login.LoginManager
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.Devices
import vinova.intern.nhomxnxx.mexplorer.model.ListDevice
import vinova.intern.nhomxnxx.mexplorer.model.Request
import java.io.File

class DevicePresenter (view: DeviceInterface.View,ctx: Context): DeviceInterface.Presenter{
    val context = ctx
    val mView: DeviceInterface.View = view
    val databaseAccess = DatabaseHandler(context)
    init {
    	mView.setPresenter(this)
    }

    override fun updateUser(first_name: String, last_name: String, uri: Uri) {
        val file = File(uri.path)

        val requestBody = RequestBody.create(
                MediaType.parse("file/*"),
                file)
        val userToken = databaseAccess.getToken()!!

        val avatar = MultipartBody.Part.createFormData("avatar", file.name, requestBody)

        CallApi.getInstance().updateUsesr(userToken,first_name, last_name, avatar)
                .enqueue(object : Callback<Request>{
                    override fun onFailure(call: Call<Request>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                        if (response?.body() != null){
                            val user = response.body()?.data
                            if (user != null) {
                                if (databaseAccess.getUserLoggedIn() != null) {
                                    databaseAccess.deleteUserData(databaseAccess.getUserLoggedIn())
                                }
                                databaseAccess.insertUserData(user.token, user.email, user.first_name,
                                        user.last_name, DatabaseHandler.NORMAL, DatabaseHandler.LOGGING_IN,
                                        user.avatar_url,user.is_vip.toString(),user.used,user.mentAuth,0,user.allocated)
                                mView.updateUser()
                            }
                        }
                        else
                            mView.showError(response?.message()!!)
                    }

                })
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
                                    mView.showList((response.body()?.data as MutableList<Devices>?)!!)
                                else
                                    mView.showError(response.message())
                        }
                    })
        }
    }
    override fun deleteDevice(token: String, id: String) {
        CallApi.getInstance().deleteDevices(token,id)
                .enqueue(object : Callback<ListDevice>{
                    override fun onFailure(call: Call<ListDevice>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<ListDevice>?, response: Response<ListDevice>?) {
                            if (response?.body()?.status.equals("success"))
                                mView.refresh()
                            else
                                mView.showError(response?.message()!!)
                    }
                })
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
                                mView.showLoading(false)
                                mView.logoutSuccess()
                                db.deleteUserData(token)
                            }
                            else
                                mView.showError(response?.message().toString())
                        }
                    })
    }
}