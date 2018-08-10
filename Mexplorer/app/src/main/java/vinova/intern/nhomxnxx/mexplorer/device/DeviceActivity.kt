package vinova.intern.nhomxnxx.mexplorer.device

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home_layout.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.DeviceAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.dialogs.ProfileDialog
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity
import vinova.intern.nhomxnxx.mexplorer.model.Devices
import vinova.intern.nhomxnxx.mexplorer.model.User
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment


@SuppressLint("Registered")
class DeviceActivity: BaseActivity(), DeviceInterface.View,GoogleApiClient.OnConnectionFailedListener,ProfileDialog.DialogListener{

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this@DeviceActivity,"Failed",Toast.LENGTH_SHORT).show()
    }

    private lateinit var adapter : DeviceAdapter
    private var mPresenter: DeviceInterface.Presenter = DevicePresenter(this,this)
    lateinit var token : String
    private var position: Int? = null
    private var mGoogleApiClient: GoogleApiClient? = null
    private var listDevice: MutableList<Devices> = mutableListOf()
    private val p = Paint()
    private lateinit var androidId : String

    init {
    	FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener {
            if(it.isSuccessful)
                androidId = it.result.token
        }
    }

    override fun showList(devices : MutableList<Devices>?) {
        adapter.setData(devices)
        listDevice = devices!!
        adapter.notifyDataSetChanged()
        swipeContent.isRefreshing = false
        rvContent.hideShimmerAdapter()
    }

    override fun setPresenter(presenter: DeviceInterface.Presenter) {
        this.mPresenter = presenter
    }

    override fun showLoading(isShow: Boolean) {

    }

    override fun showError(message: String) {
        CustomDiaglogFragment.hideLoadingDialog()
        rvContent.hideShimmerAdapter()
        Toasty.error(this,message,Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreateDrawer()
        fab_add.visibility = GONE
        setDevice()
        token = DatabaseHandler(this@DeviceActivity).getToken()!!
        if (savedInstanceState==null){
            mPresenter.getDevice(token)
            enableSwipe()
            swipeContent.isRefreshing = false
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Scope(Scopes.DRIVE_FULL))
                    .requestServerAuthCode("389228917380-ek9t84cthihvi8u4apphlojk3knd5geu.apps.googleusercontent.com",true)
                    .requestEmail()
                    .build()
            mGoogleApiClient = GoogleApiClient.Builder(this@DeviceActivity)
                    .enableAutoManage(FragmentActivity(), this@DeviceActivity)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build()
            mGoogleApiClient?.connect()
        }
    }

    private fun setDevice(){
        adapter = DeviceAdapter(this)
        rvContent.layoutManager = LinearLayoutManager(this)
        rvContent.adapter= adapter
        rvContent.showShimmerAdapter()
        rvContent.addItemDecoration(DividerItemDecoration(rvContent.context, DividerItemDecoration.VERTICAL))
        mPresenter.getDevice(DatabaseHandler(this).getToken())
        swipeContent.setOnRefreshListener {
            swipeContent.isRefreshing = false
            rvContent.showShimmerAdapter()
            mPresenter.getDevice(token)
        }
        nav_view.menu.findItem(R.id.device_connected).isChecked = true
    }

    override fun refresh() {
        CustomDiaglogFragment.hideLoadingDialog()
        mPresenter.getDevice(token)
    }

    fun checkName(){
        if (listDevice[position!!].device_id != androidId) {
            mPresenter.deleteDevice(token, listDevice.get(position!!).id!!)
            val snackbar = Snackbar.make(window.decorView.rootView, " Removed device!", Snackbar.LENGTH_LONG)
            snackbar.show()
        }
        else
            Toast.makeText(this@DeviceActivity,"Device is connecting", Toast.LENGTH_SHORT).show()
    }

    private fun enableSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.LEFT) {
                    checkName()
                    refresh()

                } else {
                    checkName()
                    refresh()
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

                val icon: Bitmap
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    val itemView = viewHolder.itemView
                    val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                    val width = height / 3

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"))
                        val background = RectF(itemView.left .toFloat(), itemView.top .toFloat(), dX, itemView.bottom .toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_clear_white_48dp)
                        val icon_dest = RectF(itemView.left .toFloat() + width, itemView.top .toFloat() + width, itemView.left .toFloat() + 2 * width, itemView.bottom .toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"))
                        val background = RectF(itemView.right .toFloat() + dX, itemView.top.toFloat(), itemView.right .toFloat(), itemView.bottom .toFloat())
                        c.drawRect(background, p)
                        icon = BitmapFactory.decodeResource(resources, R.drawable.ic_clear_white_48dp)
                        val icon_dest = RectF(itemView.right .toFloat() - 2 * width, itemView.top .toFloat() + width, itemView.right .toFloat() - width, itemView.bottom .toFloat() - width)
                        c.drawBitmap(icon, null, icon_dest, p)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rvContent)
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.home->{
	            finish()
	            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
            R.id.signout->{
                CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
                Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                mPresenter.logout(this, DatabaseHandler(this).getToken())

            }
            R.id.bookmark->{

            }
            R.id.device_connected -> {
            }
        }
        drawer_layout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun logoutSuccess() {
        CustomDiaglogFragment.hideLoadingDialog()
        startActivity(Intent(this, LogActivity::class.java))
        finish()
    }

    override fun onUpdate(user: User) {
        mPresenter.updateUser(user.first_name!!,user.last_name!!, Uri.parse(user.avatar_url))
    }

    override fun updateUser() {
        super.loadUser()
    }
}