package vinova.intern.nhomxnxx.mexplorer.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment


import vinova.intern.nhomxnxx.mexplorer.R
import java.io.File

class ConfirmDeleteDialog : DialogFragment() {
    private val mPath: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        val msg: String
        val path = arguments?.getString(PATH)
        val id = arguments?.getString("id")
        val file = File(path)
        if (file.isDirectory) {
            msg = "You are about to delete the folder with all it's content for real."
        } else {
            msg = "You are about to delete the file"
        }
        builder.setMessage(msg)
        builder.setPositiveButton(R.string.label_delete) { _, _ ->
            if (isLocal)
                (activity as ConfirmListener).onConfirmDelete(path)
            else
                (activity as ConfirmListener).onConfirmDeleteCloud(path!!,id!!)
        }
        builder.setNegativeButton(R.string.label_cancel, null)
        return builder.create()
    }

    interface ConfirmListener {
        fun onConfirmDelete(path: String?)
        fun onConfirmDeleteCloud(name :String,id:String)
    }

    companion object {

        private val PATH = "path"
        private var isLocal = false
        fun newInstance(path: String): ConfirmDeleteDialog {
            isLocal = true
            val fragment = ConfirmDeleteDialog()
            val args = Bundle()
            args.putString(PATH, path)
            fragment.arguments = args
            return fragment
        }

        fun newInstanceCloud(path: String,id:String): ConfirmDeleteDialog {
            val fragment = ConfirmDeleteDialog()
            val args = Bundle()
            args.putString(PATH, path)
            args.putString("id",id)
            fragment.arguments = args
            return fragment
        }
    }
}
