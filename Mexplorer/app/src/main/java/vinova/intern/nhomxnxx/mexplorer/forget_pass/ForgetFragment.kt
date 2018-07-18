package vinova.intern.nhomxnxx.mexplorer.forget_pass

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.forget_fragment.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment

class ForgetFragment: Fragment() ,ForgetInterface.View{
	var mPresenter : ForgetInterface.Presenter = ForgetPresenter(this)

	override fun requestSuccess() {
		CustomDiaglogFragment.hideLoadingDialog()
		Toast.makeText(context,"Please check your email!",Toast.LENGTH_SHORT).show()
	}

	override fun setPresenter(presenter: ForgetInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {

	}

	override fun showError(message: String) {
		CustomDiaglogFragment.hideLoadingDialog()
		Toast.makeText(context,message,Toast.LENGTH_SHORT).show()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(R.layout.forget_fragment,container,false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		send_request.setOnClickListener {
			CustomDiaglogFragment.showLoadingDialog(fragmentManager)
			mPresenter.sendRequestReset(email_for_reset?.text.toString())
		}
	}
}