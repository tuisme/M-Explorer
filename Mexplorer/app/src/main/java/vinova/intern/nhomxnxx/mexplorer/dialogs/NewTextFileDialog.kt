package vinova.intern.nhomxnxx.mexplorer.dialogs

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment

import vinova.intern.nhomxnxx.mexplorer.R


class NewTextFileDialog : DialogFragment() {

    private var mListener: NewTextFileDialog.DialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        val view = LayoutInflater.from(activity)
                .inflate(R.layout.new_file_dialog, view as ViewGroup?, false)

        // if text is empty, disable the dialog positive button
        val nameEditText = view.findViewById<View>(R.id.name) as EditText
        val contentEditText = view.findViewById<View>(R.id.content) as EditText

        nameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = editable.length > 0 && contentEditText.text.length > 0
            }
        })

        contentEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable) {
                (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = editable.length > 0 && nameEditText.text.isNotEmpty()
            }
        })

        builder.setTitle(R.string.new_file)
        builder.setView(view)
        builder.setPositiveButton(R.string.label_save) { dialogInterface, i -> mListener?.onNewFile(nameEditText.text.toString(), contentEditText.text.toString()) }

        val dialog = builder.create()
        view.post { dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false }
        dialog.setCancelable(false)
        return dialog
    }

    interface DialogListener {
        fun onNewFile(name: String, content: String)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        try {
            mListener = activity as DialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + " must implement DialogListener")
        }

    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {

        fun newInstance(): NewTextFileDialog {
            return NewTextFileDialog()
        }
    }

}
