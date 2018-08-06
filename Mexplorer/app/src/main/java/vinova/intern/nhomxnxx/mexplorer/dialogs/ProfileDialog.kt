package vinova.intern.nhomxnxx.mexplorer.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import com.vansuita.pickimage.enums.EPickType
import kotlinx.android.synthetic.main.profile_dialog.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.User



class ProfileDialog : DialogFragment(){

	private var mListener : DialogListener? = null

	private val TAKE_PROFILE_IMG_CODE = 12356

	lateinit var img : ImageView

	lateinit var user : User


	companion object {
		fun getInstance(user: User) : ProfileDialog{
			val fragment = ProfileDialog()
			val args = Bundle()
			args.putParcelable("user",user)
			fragment.arguments = args
			return fragment
		}
	}

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

		val builder = AlertDialog.Builder(activity)

		val view = LayoutInflater.from(activity)
				.inflate(R.layout.profile_dialog,view as ViewGroup?,false)

		user = arguments?.get("user") as User

		view.profile_firstName.setText(user.first_name, TextView.BufferType.EDITABLE)
		view.profile_lastName.setText(user.last_name, TextView.BufferType.EDITABLE)
		view.profile_email.setText(user.email, TextView.BufferType.EDITABLE)

		Glide.with(this)
				.load(user.avatar_url)
				.into(view.circleImageView)

		view.profile_firstName.addTextChangedListener(object : TextWatcher{
			override fun afterTextChanged(editable: Editable?) {
				(dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !editable?.toString().equals(user.first_name)
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

			}

			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

			}

		})

		view.profile_lastName.addTextChangedListener(object : TextWatcher{
			override fun afterTextChanged(editable: Editable?) {
				(dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = !editable?.toString().equals(user.last_name)
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

			}

			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

			}

		})

		view.update_img_profile.setOnClickListener {
			getImage()
		}

		img = view.circleImageView

		builder.setView(view)
		builder.setPositiveButton(getText(R.string.update)) { _, _ ->
			user.first_name = view.profile_firstName.text.toString()
			user.last_name = view.profile_lastName.text.toString()
			mListener?.onUpdate(user)
		}

		builder.setNegativeButton(getText(R.string.label_cancel)){ _,_ ->
			dialog.cancel()
		}

		val dialog = builder.create()
		view.post { dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false }
		dialog.setCancelable(false)

		return dialog
	}

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		try {
			mListener = context as DialogListener
		}
		catch (e: ClassCastException) {
			throw ClassCastException(activity.toString() + " must implement DialogListener")
		}
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when(requestCode){
			TAKE_PROFILE_IMG_CODE ->{
				getImage()
			}
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when(requestCode){
			TAKE_PROFILE_IMG_CODE ->{
				if (data!=null){
					val uri = data.data
					img.setImageURI(uri)
					user.avatar_url = uri.toString()
					(dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
				}
			}
		}
	}

	override fun onDetach() {
		super.onDetach()
		mListener = null
	}

	interface DialogListener{
		fun onUpdate(user : User)
	}

	private fun getImage(){
		PickImageDialog.build(object : PickSetup(){}
				.setFlip(true)
				.setPickTypes(EPickType.GALLERY, EPickType.CAMERA)
				.setIconGravity(Gravity.START)
				.setButtonOrientation(LinearLayoutCompat.HORIZONTAL))
				.setOnPickResult {
					if (it.error == null){
						img.setImageURI(it.uri)
						user.avatar_url = it.path
						(dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
					}
				}
				.show(fragmentManager,"fragment")
	}
}