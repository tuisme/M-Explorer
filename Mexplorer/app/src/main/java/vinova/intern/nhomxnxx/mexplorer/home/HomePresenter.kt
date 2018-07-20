package vinova.intern.nhomxnxx.mexplorer.home

import android.content.Context
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.ListCloud
import vinova.intern.nhomxnxx.mexplorer.model.Request

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
            CallApi.createService().logout(token)
                    .enqueue(object : Callback<Request>{
                        override fun onFailure(call: Call<Request>?, t: Throwable?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                            if (response?.body()?.status.equals("success")) {
                                mView.logoutSuccess()
                                db.deleteUserData(token)
                            }

                            else
                                mView.showError(response?.body()?.message!!)
                        }
                    })
    }

    override fun getList(token: String?) {
        if (token != null)
            CallApi.getInstance().getListCloud(token).enqueue(object : Callback<ListCloud>{
                override fun onFailure(call: Call<ListCloud>?, t: Throwable?) {

                }

                override fun onResponse(call: Call<ListCloud>?, response: Response<ListCloud>?) {
                    if (response?.body()?.status.equals("success")){
                        mView.showUser(response?.body()?.user)
                        mView.showList(response?.body())
                    }
                }
            })
    }
}