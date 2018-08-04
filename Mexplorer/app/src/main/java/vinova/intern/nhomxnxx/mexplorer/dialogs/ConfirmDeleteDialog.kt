package vinova.intern.nhomxnxx.mexplorer.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment


import vinova.intern.nhomxnxx.mexplorer.R
import java.io.File

class ConfirmDeleteDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        val msg: String
        val path = arguments?.getString(PATH)
        val isDic = arguments?.getBoolean(DIC)
        val id = arguments?.getString(ID)
        val file = File(path)
        msg = if (file.isDirectory) {
            "You are about to delete the folder with all it's content for real."
        } else {
            "You are about to delete the file"
        }
        builder.setMessage(msg)
        builder.setPositiveButton(R.string.label_delete) { _, _ ->
            if (isLocal)
                (activity as ConfirmListener).onConfirmDelete(path)
            else
                isDic?.let { (activity as ConfirmListener).onConfirmDeleteCloud(path.toString(), it,id.toString()) }
        }
        builder.setNegativeButton(R.string.label_cancel, null)
        return builder.create()
    }

    interface ConfirmListener {
        fun onConfirmDelete(path: String?)
        fun onConfirmDeleteCloud(name :String,isDic:Boolean,id:String)
    }

    companion object {

        private val PATH = "path"
        private val ID = "id"
        private val DIC = "dic"

        private var isLocal = false
        fun newInstance(path: String): ConfirmDeleteDialog {
            isLocal = true
            val fragment = ConfirmDeleteDialog()
            val args = Bundle()
            args.putString(PATH, path)
            fragment.arguments = args
            return fragment
        }

        fun newInstanceCloud(path: String, isDic:Boolean,id:String): ConfirmDeleteDialog {
            val fragment = ConfirmDeleteDialog()
            val args = Bundle()
            args.putString(PATH, path)
            args.putBoolean(DIC, isDic)
            args.putString(ID,id)
            fragment.arguments = args
            return fragment
        }
    }
}
