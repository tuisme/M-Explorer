package vinova.intern.nhomxnxx.mexplorer.signUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.sign_up_fragment.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.User

class SignUpFragment : Fragment(), SignUpInterface.View{
	var mPresenter : SignUpInterface.Presenter = SignUpPresenter(this)

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.sign_up_fragment, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		btn_sign_up.setOnClickListener {
			if (first_name_sign_up.text.toString().trim() == "" || last_name_sign_up.text.toString().trim() == "" ||
					email_sign_up.text.toString().trim() == "" || pass_word.text.toString().trim() == "") {
				Toast.makeText(context, "Please fill all field", Toast.LENGTH_LONG).show()
			}
			else {
				mPresenter.signUp(first_name_sign_up.text.toString(), last_name_sign_up.text.toString(),
						email_sign_up.text.toString(), pass_word.text.toString())
			}
		}
	}


	override fun signUpSuccess(user: User) {
		Toast.makeText(context,"Ngon", Toast.LENGTH_LONG).show()
	}

	override fun setPresenter(presenter: SignUpInterface.Presenter) {
		this.mPresenter = presenter

	}

	override fun showLoading(isShow: Boolean) {
	}

	override fun showError(message: String) {
		Toast.makeText(context,message, Toast.LENGTH_LONG).show()
	}

}