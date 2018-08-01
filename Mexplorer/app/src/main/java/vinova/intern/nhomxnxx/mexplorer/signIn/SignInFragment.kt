package vinova.intern.nhomxnxx.mexplorer.signIn

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.sign_in_fragment.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.dialogs.ForgotDialog
import vinova.intern.nhomxnxx.mexplorer.home.HomeActivity
import vinova.intern.nhomxnxx.mexplorer.model.User
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment
import java.util.*


class SignInFragment:Fragment(), GoogleApiClient.OnConnectionFailedListener, SignInInterface.View,
					LocationListener{
	override fun onConnectionFailed(connectionResult: ConnectionResult) {
		Log.d("onConnectionFailed", "onConnectionFail$connectionResult")
	}

	var mPresenter : SignInInterface.Presenter = SignInPresenter(this)
	var callBackManager : CallbackManager? = null
	val RC_SIGN_IN = 9001
	var mGoogleApiClient: GoogleApiClient? = null
	private val LOCATION_REQUEST_CODE = 123
	var location = ""
	private lateinit var mLocationManager : LocationManager

	private val LOCATION_PERMS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
			Manifest.permission.READ_EXTERNAL_STORAGE)

	override fun signInSuccess(user: User) {
        CustomDiaglogFragment.hideLoadingDialog()
		startActivity(Intent(context,BaseActivity::class.java))
		startActivity(Intent(context, HomeActivity::class.java))
		activity?.finish()
	}

	override fun setPresenter(presenter: SignInInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {

	}

	override fun showError(message: String) {
		CustomDiaglogFragment.hideLoadingDialog()
		Toasty.error(context!!,message,Toast.LENGTH_LONG).show()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.sign_in_fragment, container, false)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		callBackManager?.onActivityResult(requestCode,resultCode,data)
		if (requestCode == 9001){
			val result: GoogleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
			mPresenter.handleGoogleSignInResult(result,context!!, location)
		}
	}


	@SuppressLint("MissingPermission")
	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		FacebookSdk.sdkInitialize(activity)

		val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestScopes(Scope(Scopes.DRIVE_FULL))
				.requestEmail()
				.build()
		mGoogleApiClient = GoogleApiClient.Builder(context!!)
				.enableAutoManage(FragmentActivity(), this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build()

		btn_forget.setOnClickListener {
			ForgotDialog.getInstance().show(activity?.supportFragmentManager,"fragment")
		}

		btn_sign_in.setOnClickListener {
			CustomDiaglogFragment.showLoadingDialog(fragmentManager)
			if (email_sign_in.text.toString().trim() == "" || pass_word_sign_in.text.toString().trim() == "") {
				CustomDiaglogFragment.hideLoadingDialog()
				Toast.makeText(context, "Please fill all field", Toast.LENGTH_LONG).show()
			} else {
				mPresenter.signIn(context, email_sign_in.text.toString(), pass_word_sign_in.text.toString(),location)
			}
		}

		fab_log_with_face.setOnClickListener {
			login_face?.fragment = this
			callBackManager = CallbackManager.Factory.create()
            login_face?.setReadPermissions(Arrays.asList("public_profile","email"))
			login_face.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
				override fun onSuccess(result: LoginResult) {
                    CustomDiaglogFragment.showLoadingDialog(fragmentManager)
					mPresenter.handleFacebookAccessToken(result,context,location)
				}

				override fun onCancel() {
				}

				override fun onError(error: FacebookException?) {
				}

			})
			login_face?.performClick()
		}

		fab_log_with_google.setOnClickListener {
			val signInIntent: Intent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
			startActivityForResult(signInIntent, RC_SIGN_IN)
		}

		if (!canAccessLocation()){
			requestPermissions(LOCATION_PERMS,LOCATION_REQUEST_CODE)
		}
		else{
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000000, 0.0f, this)
		}
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		mLocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
	}

	@SuppressLint("MissingPermission")
	override fun onResume() {
		super.onResume()
	}

	override fun onPause() {
		super.onPause()
		mLocationManager.removeUpdates(this)
	}

	override fun onLocationChanged(p0: Location?) {
		location = "${p0?.latitude} ${p0?.longitude}"
	}

	override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

	}

	override fun onProviderEnabled(p0: String?) {

	}

	override fun onProviderDisabled(p0: String?) {

	}

	private fun canAccessLocation() : Boolean {
        return(hasPermission(Manifest.permission.ACCESS_FINE_LOCATION))
    }

    private fun hasPermission(perm : String) : Boolean {
        return(PackageManager.PERMISSION_GRANTED==checkSelfPermission(context!!,perm))
    }



	@SuppressLint("MissingPermission")
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		when(requestCode){
			LOCATION_REQUEST_CODE ->{
				if (canAccessLocation()) {
					mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000000, 0.0f, this)
				}
			}
		}
	}
}