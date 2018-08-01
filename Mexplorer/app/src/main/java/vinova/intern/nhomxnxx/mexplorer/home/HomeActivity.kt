package vinova.intern.nhomxnxx.mexplorer.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.box.androidsdk.content.BoxConfig
import com.box.androidsdk.content.auth.BoxAuthentication
import com.box.androidsdk.content.models.BoxSession
import com.bumptech.glide.Glide
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
import kotlinx.android.synthetic.main.nav_bar_header.*
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
import java.lang.Exception


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
	override fun logoutSuccess() {
		CustomDiaglogFragment.hideLoadingDialog()
		startActivity(Intent(this,LogActivity::class.java))
		finish()
	}

	override fun setPresenter(presenter: HomeInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {

	}

	override fun showError(message: String) {
		CustomDiaglogFragment.hideLoadingDialog()
		Toasty.error(this,message,Toast.LENGTH_SHORT).show()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		super.onCreateDrawer()
		setRecyclerView()
		userToken = DatabaseHandler(this).getToken()!!
		if (savedInstanceState==null)
			mPresenter.getList(DatabaseHandler(this).getToken())
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
		setBox()
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
		adapter.setData(list.clouds)
		showUser()
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
				if (cloud.type.equals("local"))
					startActivity(Intent(this@HomeActivity,LocalActivity::class.java)
							.putExtra("name",cloud.name))
				else {
					val intent = Intent(this@HomeActivity, CloudActivity::class.java)
					intent.putExtra("id", cloud.root).putExtra("token",cloud.token)
							.putExtra("type",cloud.type).putExtra("name",cloud.name)
					startActivity(intent)
				}
			}
		})
		fab_add.setOnClickListener {
			AddCloudDialog.newInstance().show(supportFragmentManager,"Fragement")
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if (requestCode == 9001){
			val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
			if(result.isSuccess){
				val account: GoogleSignInAccount = result.signInAccount!!
				val authCode = account.serverAuthCode
				mPresenter.sendCode(authCode!!,newName,userToken,providerName)
			}
		}
	}

	override fun onRename(fromPath: String, toPath: String) {
	}

	override fun onReNameCloud(newName: String, id: String,token:String) {
		mPresenter.renameCloud(id,newName,token,DatabaseHandler(this).getToken()!!)
	}

	override fun onConfirmDelete(path: String?) {

	}

	override fun onConfirmDeleteCloud(name: String, id: String) {
		mPresenter.deleteCloud(id,DatabaseHandler(this@HomeActivity).getToken()!!)
	}

	override fun onOptionClick(name: String,provider:String) {

		newName = name
		providerName = provider
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
	}

	override fun onResume() {
		super.onResume()
		if (com.dropbox.core.android.Auth.getOAuth2Token() != null && firstTime){
			firstTime = false
			val token = com.dropbox.core.android.Auth.getOAuth2Token()
			mPresenter.sendCode(token,newName,userToken,providerName)
		}
	}

	override fun onConnectionFailed(p0: ConnectionResult) {

	}

	override fun refreshList(list: ListCloud?) {
		this.listCloud = list!!
		adapter.refreshData(list.clouds)
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
		adapter.setData(this.listCloud.clouds)
	}

	override fun refresh() {
		mPresenter.refreshList(userToken)
	}

	//box session
	override fun onLoggedOut(info: BoxAuthentication.BoxAuthenticationInfo?, ex: Exception?) {

	}
	// get access token of box
	override fun onAuthCreated(info: BoxAuthentication.BoxAuthenticationInfo?) {
		val code = boxSession.authInfo.refreshToken()
		mPresenter.sendCode(code,newName,userToken,providerName)
	}

	override fun onRefreshed(info: BoxAuthentication.BoxAuthenticationInfo?) {

	}

	override fun onAuthFailure(info: BoxAuthentication.BoxAuthenticationInfo?, ex: Exception?) {

	}
}