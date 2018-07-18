package vinova.intern.nhomxnxx.mexplorer.signIn

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.sign_in_fragment.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.forget_pass.ForgetFragment
import vinova.intern.nhomxnxx.mexplorer.home.HomeActivity
import vinova.intern.nhomxnxx.mexplorer.model.User
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment


class SignInFragment:Fragment(), SignInInterface.View{
	var mPresenter : SignInInterface.Presenter = SignInPresenter(this)
	var callBackManager : CallbackManager? = null

	override fun signInSuccess(user: User) {
        CustomDiaglogFragment.hideLoadingDialog()
		startActivity(Intent(context, HomeActivity::class.java))
		activity?.finish()
	}

	override fun setPresenter(presenter: SignInInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun showError(message: String) {
		CustomDiaglogFragment.hideLoadingDialog()
		Toast.makeText(context,message,Toast.LENGTH_LONG).show()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.sign_in_fragment, container, false)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		callBackManager?.onActivityResult(requestCode,resultCode,data)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		fragmentManager?.beginTransaction()?.replace(R.id.forget_frag,ForgetFragment())?.addToBackStack(null)?.commit()
		btn_sign_in.setOnClickListener{
			CustomDiaglogFragment.showLoadingDialog(fragmentManager)
			if (email_sign_in.text.toString().trim() == "" || pass_word_sign_in.text.toString().trim() == "") {
				CustomDiaglogFragment.hideLoadingDialog()
				Toast.makeText(context, "Please fill all field", Toast.LENGTH_LONG).show()
			}
			else {
				mPresenter.signIn(context,email_sign_in.text.toString(), pass_word_sign_in.text.toString())
			}
		}
		fab_log_with_face.setOnClickListener {
			login_face?.fragment = this
			callBackManager = CallbackManager.Factory.create()
			login_face?.setReadPermissions("email")
			login_face?.fragment = this
			login_face.registerCallback(callBackManager,object : FacebookCallback<LoginResult> {
				override fun onSuccess(result: LoginResult) {
					mPresenter.handleFacebookAccessToken(result)
				}

				override fun onCancel() {
				}

				override fun onError(error: FacebookException?) {
				}

			})
			login_face?.performClick()
		}
		btn_forget.setOnClickListener {
			activity?.findViewById<FrameLayout>(R.id.forget_frag)?.visibility = View.VISIBLE
		}
	}
}