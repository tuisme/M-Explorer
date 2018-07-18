package vinova.intern.nhomxnxx.mexplorer.signIn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.sign_in_fragment.*
import vinova.intern.nhomxnxx.mexplorer.R
import android.content.Intent
import android.widget.Toast
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment
import vinova.intern.nhomxnxx.mexplorer.home.HomeActivity
import vinova.intern.nhomxnxx.mexplorer.model.User


class SignInFragment:Fragment(), SignInInterface.View{
	var mPresenter : SignInInterface.Presenter = SignInPresenter(this)

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
		Toast.makeText(context,message,Toast.LENGTH_LONG).show()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.sign_in_fragment, container, false)
	}



	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
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
	}
}