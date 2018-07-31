package vinova.intern.nhomxnxx.mexplorer.dialogs


import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.add_cloud_bottomsheetdialog.*
import kotlinx.android.synthetic.main.add_cloud_bottomsheetdialog.view.*
import vinova.intern.nhomxnxx.mexplorer.R

class AddCloudDialog:DialogFragment() {
	private var mListener: AddCloudDialog.DialogListener? = null

	@SuppressLint("InflateParams")
	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val dialog = BottomSheetDialog(context!!, theme)
		val view = LayoutInflater.from(activity).inflate(R.layout.add_cloud_bottomsheetdialog, null)
		val zoomAnim: Animation = AnimationUtils.loadAnimation(context,R.anim.zoom_anim)
		var provider = "googledrive"
		dialog.setContentView(view)
		dialog.setCancelable(true)
		view.checkggDrive.startAnimation(zoomAnim)
		view.googledrivetext.setTextColor(Color.RED)
		view.checkggDrive.setOnClickListener {
			provider="googledrive"
			view.checkggDrive.startAnimation(zoomAnim)
            view.checkOneDrive.clearAnimation()
            view.checkDropbox.clearAnimation()
            view.checkBox.clearAnimation()
			view.googledrivetext.setTextColor(Color.RED)
			view.dropboxText.setTextColor(Color.BLACK)
			view.oneDriveText.setTextColor(Color.BLACK)
			view.boxText.setTextColor(Color.BLACK)

		}
		view.checkOneDrive.setOnClickListener {
			provider = "onedrive"
			view.checkOneDrive.startAnimation(zoomAnim)
            view.checkggDrive.clearAnimation()
            view.checkDropbox.clearAnimation()
            view.checkBox.clearAnimation()
			view.oneDriveText.setTextColor(Color.RED)
			view.googledrivetext.setTextColor(Color.BLACK)
			view.dropboxText.setTextColor(Color.BLACK)
			view.boxText.setTextColor(Color.BLACK)

		}
		view.checkDropbox.setOnClickListener {
			provider = "dropbox"
			view.checkDropbox.startAnimation(zoomAnim)
			view.checkOneDrive.clearAnimation()
            view.checkggDrive.clearAnimation()
            view.checkBox.clearAnimation()
			view.dropboxText.setTextColor(Color.RED)
			view.googledrivetext.setTextColor(Color.BLACK)
			view.oneDriveText.setTextColor(Color.BLACK)
			view.boxText.setTextColor(Color.BLACK)

		}
		view.checkBox.setOnClickListener {
			provider = "box"
			view.checkBox.startAnimation(zoomAnim)
            view.checkggDrive.clearAnimation()
            view.checkDropbox.clearAnimation()
            view.checkOneDrive.clearAnimation()
			view.boxText.setTextColor(Color.RED)
			view.googledrivetext.setTextColor(Color.BLACK)
			view.dropboxText.setTextColor(Color.BLACK)
			view.oneDriveText.setTextColor(Color.BLACK)
		}
        view.add_item.setOnClickListener {
            val name =view.nameNewCloud.text.toString()
            if (name != "") {
                dialog.dismiss()
                mListener?.onOptionClick(name, provider)
            }
			else{
				Toast.makeText(context,"Please fill name the cloud", Toast.LENGTH_LONG).show()
			}
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
		fun onOptionClick(name : String,provider : String)
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