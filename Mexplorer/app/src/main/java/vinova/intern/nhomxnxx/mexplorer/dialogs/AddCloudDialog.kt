package vinova.intern.nhomxnxx.mexplorer.dialogs


import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.add_cloud_bottomsheetdialog.view.*
import vinova.intern.nhomxnxx.mexplorer.R

class AddCloudDialog:DialogFragment() {
	private var mListener: AddCloudDialog.DialogListener? = null

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val dialog = BottomSheetDialog(context!!, theme)
		val view = LayoutInflater.from(activity).inflate(R.layout.add_cloud_bottomsheetdialog, null)
		dialog.setContentView(view)
		dialog.setCancelable(true)


		view.googledrive.setOnClickListener {
			dialog.dismiss()
			mListener?.onOptionClick()
		}

		dialog.setOnShowListener {
			val width = resources.getDimension(R.dimen.bottom_sheet_dialog_width).toInt()
			dialog.window?.setLayout(
					if (width == 0) ViewGroup.LayoutParams.MATCH_PARENT else width,
					ViewGroup.LayoutParams.MATCH_PARENT)
		}
		return dialog

	}
	interface DialogListener {
		fun onOptionClick()
	}

	override fun onAttach(activity: Activity) {
		super.onAttach(activity)
		try {
			mListener = activity as AddCloudDialog.DialogListener
		} catch (e: ClassCastException) {
			throw ClassCastException(activity.toString() + " must implement DialogListener")
		}

	}

	override fun onDetach() {
		super.onDetach()
		mListener = null
	}

	companion object {

		fun newInstance(): AddCloudDialog {
			val fragment = AddCloudDialog()
			val args = Bundle()
			fragment.arguments = args
			return fragment
		}
	}
}