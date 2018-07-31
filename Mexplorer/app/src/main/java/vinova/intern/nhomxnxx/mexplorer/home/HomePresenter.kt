package vinova.intern.nhomxnxx.mexplorer.home

import android.content.Context
import com.facebook.login.LoginManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.ListCloud
import vinova.intern.nhomxnxx.mexplorer.model.Request
import vinova.intern.nhomxnxx.mexplorer.model.RequestChangeName

@Suppress("NAME_SHADOWING")
class HomePresenter(view:HomeInterface.View): HomeInterface.Presenter {
    val mView: HomeInterface.View = view
    init {
        mView.setPresenter(this)
    }

    override fun logout(context: Context?, token: String?) {
        val token = DatabaseHandler(context).getToken()
        val db = DatabaseHandler(context)
        if (token!=null)
            CallApi.getInstance().logout(token)
                    .enqueue(object : Callback<Request>{
                        override fun onFailure(call: Call<Request>?, t: Throwable?) {
                            mView.showError("can not sign out")
                        }
                        override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                            if (response?.body()?.status.equals("success")) {
	                            LoginManager.getInstance().logOut()
                                mView.logoutSuccess()
                                db.deleteUserData(token)
                            }
                            else
                                mView.showError(response?.message().toString())
                        }
                    })
    }

    override fun getList(token: String?) {
        if (token != null)
            CallApi.getInstance().getListCloud(token).enqueue(object : Callback<ListCloud>{
                override fun onFailure(call: Call<ListCloud>?, t: Throwable?) {
                    mView.showError(t.toString())
                }

                override fun onResponse(call: Call<ListCloud>?, response: Response<ListCloud>?) {
                    if (response?.body()?.status.equals("success")){
                        mView.showList(response?.body())
                    }
                }
            })
    }

    override fun refreshList(token: String?) {
        if (token != null)
            CallApi.getInstance().getListCloud(token).enqueue(object : Callback<ListCloud>{
                override fun onFailure(call: Call<ListCloud>?, t: Throwable?) {
                    mView.showError(t.toString())
                }

                override fun onResponse(call: Call<ListCloud>?, response: Response<ListCloud>?) {
                    if (response?.body()?.status.equals("success")){
                        mView.refreshList(response?.body())
                    }
                }
            })
    }

    override fun renameCloud(id: String, newName: String,token:String,userToken:String) {
        CallApi.getInstance().changeNameCloud(userToken,id,newName)
                .enqueue(object : Callback<RequestChangeName>{
                    override fun onFailure(call: Call<RequestChangeName>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<RequestChangeName>?, response: Response<RequestChangeName>?) {
	                    if(response?.body() == null)
	                        mView.refresh()
	                    else
		                    mView.refresh()
                    }
                })
    }

    override fun deleteCloud(id: String, token: String) {
        CallApi.getInstance().deleteDrive(token,id)
                .enqueue(object : Callback<RequestChangeName>{
                    override fun onFailure(call: Call<RequestChangeName>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<RequestChangeName>?, response: Response<RequestChangeName>?) {
                        mView.refresh()
                    }
                })
    }

    override fun sendCode(code: String, name: String,userToken: String,provider:String) {
        CallApi.getInstance().getDrive(userToken,code,name,provider)
                .enqueue(object : Callback<RequestChangeName>{
                    override fun onFailure(call: Call<RequestChangeName>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<RequestChangeName>?, response: Response<RequestChangeName>?) {
                        if(response?.body()!=null)
                            mView.refresh()
                        else
                            mView.showError(response?.message()!!)
                    }
                })
    }


}