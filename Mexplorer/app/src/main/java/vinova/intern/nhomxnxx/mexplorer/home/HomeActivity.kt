package vinova.intern.nhomxnxx.mexplorer.home

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import androidx.core.view.GravityCompat
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.box.androidsdk.content.BoxConfig
import com.box.androidsdk.content.auth.BoxAuthentication
import com.box.androidsdk.content.models.BoxSession
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home_layout.*
import kotlinx.android.synthetic.main.switch_layout.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.RvHomeAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.cloud.CloudActivity
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.device.DeviceActivity
import vinova.intern.nhomxnxx.mexplorer.dialogs.AddCloudDialog
import vinova.intern.nhomxnxx.mexplorer.dialogs.ConfirmDeleteDialog
import vinova.intern.nhomxnxx.mexplorer.dialogs.RenameDialog
import vinova.intern.nhomxnxx.mexplorer.local.LocalActivity
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity
import vinova.intern.nhomxnxx.mexplorer.model.Cloud
import vinova.intern.nhomxnxx.mexplorer.model.ListCloud
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment
import vinova.intern.nhomxnxx.mexplorer.utils.NetworkUtils
import vinova.intern.nhomxnxx.mexplorer.utils.NetworkUtils.Companion.messageNetWork
import java.lang.Exception


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeActivity : BaseActivity(),HomeInterface.View ,
		RenameDialog.DialogListener, GoogleApiClient.OnConnectionFailedListener,
		ConfirmDeleteDialog.ConfirmListener,
		AddCloudDialog.DialogListener, BoxAuthentication.AuthListener {

    private var mPresenter :HomeInterface.Presenter= HomePresenter(this)
	private lateinit var adapter : RvHomeAdapter
	private var listCloud : ListCloud = ListCloud()
	val RC_SIGN_IN = 9001
	var mGoogleApiClient: GoogleApiClient? = null
	var newName : String = ""
	var providerName : String = ""
	lateinit var userToken : String
	lateinit var boxSession: BoxSession
	var firstTime = false
	val CAPTURE_IMAGE_REQUEST = 20
	val CAPTURE_IMAGE_REQUEST_2 = 22
    val CAPTURE_IMAGE_REQUEST_3 = 24
    var isAuth:Boolean = false
	lateinit var sw_auth:SwitchCompat
	lateinit var cloud:Cloud

    val db = DatabaseHandler(this@HomeActivity)

	override fun logoutSuccess() {
		CustomDiaglogFragment.hideLoadingDialog()
		startActivity(Intent(this,LogActivity::class.java))
		finish()
	}

	override fun forceLogOut(message: String) {
		Toasty.info(this,message,Toast.LENGTH_SHORT).show()
		startActivity(Intent(this,LogActivity::class.java))
		finish()
	}

	override fun setPresenter(presenter: HomeInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {
        if(isShow) CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
        else CustomDiaglogFragment.hideLoadingDialog()
	}

	override fun showError(message: String) {
		CustomDiaglogFragment.hideLoadingDialog()
		Toasty.error(this,message,Toast.LENGTH_SHORT).show()
	}


	@TargetApi(Build.VERSION_CODES.M)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		super.onCreateDrawer()
		setRecyclerView()
		setGoogleAccount()
		setBox()
		setSwitchAuth()
		userToken = DatabaseHandler(this).getToken()!!
		if (savedInstanceState==null) {
			if (!NetworkUtils.isConnectedInternet(this)){
				showError(NetworkUtils.messageNetWork)
				return
			}
			mPresenter.getList(DatabaseHandler(this).getToken())
		}
	}

    override fun setSwitch(isChecked: Boolean) {
        sw_auth.isChecked = isChecked
    }

	@RequiresApi(Build.VERSION_CODES.M)
	private fun setSwitchAuth(){
		sw_auth = MenuItemCompat.getActionView(nav_view.menu.findItem(R.id.auth)).findViewById<SwitchCompat>(R.id.sw_auth)
		if (db.getIsFaceAuth() == 1){
			sw_auth.isChecked = true
		}
		else if (db.getIsFaceAuth() == 0) {
			sw_auth.isChecked = false
		}
		sw_auth.setOnClickListener {
			if (sw_auth.isChecked) {
				val ad = AlertDialog.Builder(this)
				ad.create()
				ad.setCancelable(false)
				ad.setTitle(title)
				ad.setMessage("Do you want to turn on face authentication?")
				ad.setPositiveButton("Yes") { _, _ ->
					captureImage(CAPTURE_IMAGE_REQUEST)
				}
				ad.setNegativeButton("No") { _, _ -> sw_auth.isChecked=false }
				ad.show()
			}
			else {
				val ad = AlertDialog.Builder(this)
				ad.create()
				ad.setCancelable(false)
				ad.setTitle(title)
				ad.setMessage("Please face authentication to turn off")
				ad.setPositiveButton("Yes") { _, _ ->
					captureImage(CAPTURE_IMAGE_REQUEST_2)
				}
				ad.setNegativeButton("No") { _, _ ->  sw_auth.isChecked=true}
				ad.show()
			}
		}
	}

	private fun setGoogleAccount(){
		val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestScopes(Scope(Scopes.DRIVE_FULL))
				.requestServerAuthCode("389228917380-ek9t84cthihvi8u4apphlojk3knd5geu.apps.googleusercontent.com",true)
				.requestEmail()
				.build()
		mGoogleApiClient = GoogleApiClient.Builder(this@HomeActivity)
				.enableAutoManage(FragmentActivity(), this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build()
		mGoogleApiClient?.connect()
	}

	private fun setBox(){
		BoxConfig.CLIENT_ID = "i9jieqavbpuutnbbrqdyeo44m0imegpk"
		BoxConfig.CLIENT_SECRET = "4LjQ7N3toXIXVozyXOB21tBTcCo2KX6F"
		BoxConfig.REDIRECT_URL = "https://app.box.com"
		boxSession = BoxSession(this@HomeActivity,null)
		boxSession.setSessionAuthListener(this@HomeActivity)
	}

	override fun onNavigationItemSelected(p0: MenuItem): Boolean {
		when(p0.itemId){
			R.id.home->{

			}
			R.id.signout->{
				CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
				mPresenter.logout(this, DatabaseHandler(this).getToken())
				Auth.GoogleSignInApi.signOut(mGoogleApiClient)
			}
			R.id.bookmark->{

			}
			R.id.device_connected -> {
				val intent = Intent(this,DeviceActivity::class.java)
				startActivity(intent)
			}
		}
		drawer_layout?.closeDrawer(GravityCompat.START)
		return true
	}

	override fun showList(list: ListCloud?) {
		rvContent.hideShimmerAdapter()
		this.listCloud = list!!
		adapter.setData(list.data)
	}

	private fun setRecyclerView(){
		adapter = RvHomeAdapter(this,app_bar_home.findViewById(R.id.bottom_sheet_detail),supportFragmentManager)
		val manager = LinearLayoutManager(this)
		rvContent.layoutManager = manager
		rvContent.adapter = adapter
        rvContent.showShimmerAdapter()
		swipeContent.setOnRefreshListener {
			mPresenter.refreshList(DatabaseHandler(this).getToken())
			swipeContent.isRefreshing = false
		}
		adapter.setListener(object : RvHomeAdapter.ItemClickListener{
            override fun onItemClick(cloud: Cloud) {
                this@HomeActivity.cloud = cloud
				if (cloud.type.equals("local"))
					startActivity(Intent(this@HomeActivity,LocalActivity::class.java)
							.putExtra("name",cloud.name))
				else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && db.getIsFaceAuth() == 1 && !isAuth) {
                        captureImage(CAPTURE_IMAGE_REQUEST_3)
                    }
                    else {
                        val intent = Intent(this@HomeActivity, CloudActivity::class.java)
                        intent.putExtra("id", cloud.root).putExtra("token", cloud.token)
                                .putExtra("type", cloud.type).putExtra("name", cloud.name)
                        startActivity(intent)
                    }
				}
			}
		})
		fab_add.setOnClickListener {
			AddCloudDialog.newInstance().show(supportFragmentManager,"Fragement")
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when (requestCode){
			9001 -> {
				val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
				if(result.isSuccess){
					val account: GoogleSignInAccount = result.signInAccount!!
					val authCode = account.serverAuthCode
					mPresenter.sendCode(authCode!!,newName,userToken,providerName)
				}
			}
			CAPTURE_IMAGE_REQUEST -> {
				if (data != null) {
                    mPresenter.encryptFile(this@HomeActivity,data)

                }
			}
			CAPTURE_IMAGE_REQUEST_2 -> {
				if (data != null) {
					mPresenter.authentication(this@HomeActivity, data,true)
				}
			}
            CAPTURE_IMAGE_REQUEST_3 -> {
                if (data != null) {
                    mPresenter.authentication(this@HomeActivity, data,false)
                }
            }
		}

	}

	override fun onRename(fromPath: String, toPath: String) {
	}

	override fun onReNameCloud(newName: String, id: String, isDic: Boolean, token: String) {
		mPresenter.renameCloud(id,newName,token,DatabaseHandler(this).getToken()!!)
	}

	override fun onConfirmDelete(path: String?) {

	}

	override fun onConfirmDeleteCloud(name: String, isDic: Boolean, id: String) {
		mPresenter.deleteCloud(id,DatabaseHandler(this@HomeActivity).getToken()!!)
	}

	override fun onOptionClick(name: String,provider:String) {

		newName = name
		providerName = provider
		if (NetworkUtils.isConnectedInternet(this))
			when(provider){
				"googledrive" -> {
					val signInIntent: Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
					startActivityForResult(signInIntent, RC_SIGN_IN)
					Auth.GoogleSignInApi.signOut(mGoogleApiClient)
				}
				"dropbox" ->{
					firstTime = true
					com.dropbox.core.android.Auth.startOAuth2Authentication(this@HomeActivity,getString(R.string.drbx_key))
				}
				"onedrive" -> {

				}
				"box" -> {
					boxSession.authenticate(this@HomeActivity)
				}
			}
		else
			showError(messageNetWork)
	}

	override fun onResume() {
		super.onResume()
		if (com.dropbox.core.android.Auth.getOAuth2Token() != null && firstTime){
			if (!NetworkUtils.isConnectedInternet(this)){
				showError(NetworkUtils.messageNetWork)
				return
			}
			firstTime = false
			val token = com.dropbox.core.android.Auth.getOAuth2Token()
			mPresenter.sendCode(token,newName,userToken,providerName)
		}
	}

	override fun onConnectionFailed(p0: ConnectionResult) {

	}

	override fun refreshList(list: ListCloud?) {
		this.listCloud = list!!
		adapter.refreshData(list.data)
	}

	override fun onBackPressed() {
		if (BottomSheetBehavior.from(bottom_sheet_detail).state == BottomSheetBehavior.STATE_EXPANDED )
			BottomSheetBehavior.from(bottom_sheet_detail).state = BottomSheetBehavior.STATE_COLLAPSED
		else
			super.onBackPressed()
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
		outState?.putParcelable("list_cloud",listCloud)
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
		super.onRestoreInstanceState(savedInstanceState)
		this.listCloud = savedInstanceState?.getParcelable("list_cloud")!!
		adapter.setData(this.listCloud.data)
	}

	override fun refresh() {
		if (!NetworkUtils.isConnectedInternet(this)){
			showError(NetworkUtils.messageNetWork)
			return
		}
		else
			mPresenter.refreshList(userToken)
	}

	//box session
	override fun onLoggedOut(info: BoxAuthentication.BoxAuthenticationInfo?, ex: Exception?) {

	}
	// get access token of box
	override fun onAuthCreated(info: BoxAuthentication.BoxAuthenticationInfo?) {
		if (!NetworkUtils.isConnectedInternet(this)){
			showError(NetworkUtils.messageNetWork)
			return
		}
		val code = boxSession.authInfo.refreshToken()
		mPresenter.sendCode(code,newName,userToken,providerName)
	}

	override fun onRefreshed(info: BoxAuthentication.BoxAuthenticationInfo?) {

	}

	override fun onAuthFailure(info: BoxAuthentication.BoxAuthenticationInfo?, ex: Exception?) {

	}

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
			2222-> {
				if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
					startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
				} else {
					Toasty.warning(this, "Permission Denied, Please allow to proceed !", Toast.LENGTH_LONG).show()
				}
			}
        }
    }

	@RequiresApi(Build.VERSION_CODES.M)
	private fun captureImage(code:Int) {
		if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(arrayOf(Manifest.permission.CAMERA),2222)
		}
		else {
			val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
			startActivityForResult(cameraIntent, code)
		}
	}

    override fun isAuth(isAuth: Boolean) {
        this@HomeActivity.isAuth = isAuth
        if (isAuth){
            val intent = Intent(this@HomeActivity, CloudActivity::class.java)
            intent.putExtra("id", cloud.root).putExtra("token", cloud.token)
                    .putExtra("type", cloud.type).putExtra("name", cloud.name)
            startActivity(intent)
        }
    }
}