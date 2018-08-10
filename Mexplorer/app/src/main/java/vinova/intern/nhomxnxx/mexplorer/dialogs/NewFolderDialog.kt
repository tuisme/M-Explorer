package vinova.intern.nhomxnxx.mexplorer.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import vinova.intern.nhomxnxx.mexplorer.R


class NewFolderDialog : DialogFragment() {

    private var mListener: NewFolderDialog.DialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity)

        val view = LayoutInflater.from(activity)
                .inflate(R.layout.new_folder_dialog, view as ViewGroup?, false)

        // if text is empty, disable the dialog positive button
        val editText = view.findViewById<View>(R.id.name) as EditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = editable != null && editable.isNotEmpty()
            }
        })

        builder.setTitle(R.string.new_folder)
        builder.setView(view)
        builder.setPositiveButton(R.string.label_save) { _, _ ->
            mListener?.onNewFolder(editText.text.toString())
        }

        val dialog = builder.create()
        view.post { dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false }
        dialog.setCancelable(false)
        return dialog
    }

    interface DialogListener {
        fun onNewFolder(name: String)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
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

        fun newInstance(): NewFolderDialog {
            return NewFolderDialog()
        }
    }

}
