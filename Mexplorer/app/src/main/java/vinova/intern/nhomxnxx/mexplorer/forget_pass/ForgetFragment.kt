package vinova.intern.nhomxnxx.mexplorer.forget_pass

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.forget_fragment.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment

class ForgetFragment: DialogFragment() ,ForgetInterface.View{
	var mPresenter : ForgetInterface.Presenter = ForgetPresenter(this)

	companion object {
		private var forget : ForgetFragment? = null

		var isShow = false

		private fun newInstance():ForgetFragment{
			return ForgetFragment()
		}

		fun showForgetDialog(fm : FragmentManager){
			if (forget == null)
				forget = newInstance()
			isShow = true
			forget?.show(fm,"fragment")
		}

		fun hideForgetDialog(){
			isShow = false
			forget?.dismiss()
		}
	}

	override fun requestSuccess() {
		CustomDiaglogFragment.hideLoadingDialog()
		Toast.makeText(context,"Please check your email!",Toast.LENGTH_SHORT).show()
		afterRequestSuccess()
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
		Log.e("ABCD","here")
		return super.onCreateView(inflater, container, savedInstanceState)
	}

	@SuppressLint("InflateParams")
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val alertDialogBuilder = AlertDialog.Builder(this@ForgetFragment.requireActivity())
		val inflater = activity!!.layoutInflater
		val view = inflater.inflate(R.layout.forget_fragment, null)
		beforeRequest(view)
		view.findViewById<Button>(R.id.send_request).setOnClickListener {
			CustomDiaglogFragment.showLoadingDialog(fragmentManager)
			mPresenter.sendRequestReset(email_for_reset?.text.toString())
		}
		alertDialogBuilder.setView(view)
		val dialog = alertDialogBuilder.create()
		dialog.setCancelable(false)
		dialog.setCanceledOnTouchOutside(false)
		dialog.window.setBackgroundDrawable(
				ColorDrawable(Color.TRANSPARENT))
		return dialog
	}

	private fun afterRequestSuccess(){
		new_pass.visibility = View.VISIBLE
		number_check.visibility = View.VISIBLE
		email_for_reset.visibility = View.GONE
	}

	private fun beforeRequest(view: View){
		view.findViewById<TextInputLayout>(R.id.new_pass).visibility = View.GONE
		view.findViewById<TextInputLayout>(R.id.number_check).visibility = View.GONE
		view.findViewById<TextInputLayout>(R.id.textInputLayout).visibility = View.VISIBLE
	}
}