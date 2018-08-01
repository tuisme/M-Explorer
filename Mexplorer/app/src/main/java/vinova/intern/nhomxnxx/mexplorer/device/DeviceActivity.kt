package vinova.intern.nhomxnxx.mexplorer.device

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.GONE
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.content_home_layout.*
import kotlinx.android.synthetic.main.nav_bar_header.*
import vinova.intern.nhomxnxx.mexplorer.adapter.DeviceAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.Devices

@SuppressLint("Registered")
class DeviceActivity: BaseActivity(), DeviceInterface.View{
    private lateinit var adapter : DeviceAdapter
    private var mPresenter: DeviceInterface.Presenter = DevicePresenter(this)
    lateinit var token : String
    override fun showList(devices : List<Devices>?) {
        showUser()
        adapter.setData(devices!!)
        adapter.notifyDataSetChanged()
    }

    override fun setPresenter(presenter: DeviceInterface.Presenter) {
        this.mPresenter = presenter
    }

    override fun showLoading(isShow: Boolean) {

    }

    override fun showError(message: String) {
        Toasty.error(this,message,Toast.LENGTH_SHORT).show()
    }

    private fun showUser() {
        val user = DatabaseHandler(this).getUser()
        val name = "${user.firstName} ${user.lastName}"
        user_name.text = name
        user_email.text = user.email
        user_have_percentage.text = user.used
        progressBar.progress = (user.used?.toFloat()?.times(100))?.toInt() ?: 0
        Glide.with(this)
                .load(user.avatarUrl)
                .into(img_profile)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreateDrawer()
        fab_add.visibility = GONE
        setDevice()
        token = DatabaseHandler(this).getToken()!!
        if (savedInstanceState==null){
            mPresenter.getDevice(DatabaseHandler(this).getToken())
            swipeContent.isRefreshing = false
        }
    }
    fun setDevice(){
        adapter = DeviceAdapter(this)
        rvContent.layoutManager = LinearLayoutManager(this)
        rvContent.adapter= adapter
        mPresenter.getDevice(DatabaseHandler(this).getToken())
        swipeContent.setOnRefreshListener {
            swipeContent.isRefreshing = false
        }

    }

}