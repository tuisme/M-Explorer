package vinova.intern.nhomxnxx.mexplorer.dialogs

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.content_home_layout.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.FileSec
import java.io.File

@Suppress("UNUSED_EXPRESSION")
class UpdateItemDialog : DialogFragment() {
    private var mListener: DialogListener? = null

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = BottomSheetDialog(context!!, theme)
        val path = arguments?.getString(PATH)
        val file = arguments?.getParcelable<FileSec>(FILE)
        val ctype = arguments?.getString(TYPE)
        val isDirectory:Boolean
        isDirectory = if(path != null) {
            File(path).isDirectory
        }
        else file?.mime_type?.contains("folder")!!
        val view = LayoutInflater.from(activity).inflate(R.layout.update_item_dialog, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        // title
        val title = view.findViewById<View>(R.id.title) as TextView
        title.text = if (isDirectory) getString(R.string.folder_options) else getString(R.string.file_options)

        val rename = view.findViewById<View>(R.id.rename)
        val delete = view.findViewById<View>(R.id.delete)
        val move = view.findViewById<View>(R.id.move)
        val copy = view.findViewById<View>(R.id.copy)
        val offline = view.findViewById<Switch>(R.id.offline)
        val download = view.findViewById<View>(R.id.download)
        if (path!=null) download.visibility = View.GONE

        rename.setOnClickListener {
            dialog.dismiss()
            mListener?.onOptionClick(R.id.rename, path ?: "${file?.id}/$isDirectory/${file?.name}")
        }

        delete.setOnClickListener {
            dialog.dismiss()
            mListener?.onOptionClick(R.id.delete, path ?: "${file?.id}/$isDirectory/${file?.name}")
        }

        if (!isDirectory && path ==null || isDirectory && path ==null) {
            move.setOnClickListener {
                dialog.dismiss()
                mListener?.onOptionClick(R.id.move, path ?: "${file?.id}/$isDirectory/${file?.name}")
            }

            copy.setOnClickListener {
                dialog.dismiss()
                mListener?.onOptionClick(R.id.copy, path ?: "${file?.id}/$isDirectory/${file?.name}")
            }
        } else {
            move.visibility = View.GONE
            copy.visibility = View.GONE
        }
        var folderPath = Environment.getExternalStorageDirectory().path + File.separator + "Temp"
        when(ctype){
            "googledrive" -> {
                folderPath = folderPath +File.separator +"Google Drive"+ File.separator + file?.name
            }
            "dropbox" ->{
                folderPath = folderPath +File.separator +"DropBox"+ File.separator + file?.name
            }
            "onedrive" -> {
                folderPath = folderPath +File.separator +"OneDrive"+ File.separator + file?.name
            }
            "box" -> {
                folderPath = folderPath +File.separator +"Box"+ File.separator + file?.name
            }
        }
        if(File(folderPath).exists()){
            offline.isChecked = true
            offline.isClickable = false

        }
        offline.setOnCheckedChangeListener { _, isChecked ->

                if (isChecked) {
                    mListener?.onOptionClick(R.id.offline, path ?: file?.id)
                    offline.isClickable = false
                }

        }

        // control dialog width on different devices
        dialog.setOnShowListener {
            val width = resources.getDimension(R.dimen.bottom_sheet_dialog_width).toInt()
            dialog.window?.setLayout(
                    if (width == 0) ViewGroup.LayoutParams.MATCH_PARENT else width,
                    ViewGroup.LayoutParams.MATCH_PARENT)
        }

        return dialog
    }

    interface DialogListener {
        fun onOptionClick(which: Int, path: String?)
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

        private val PATH = "path"
        private val FILE = "file"
        private val TYPE = "type"

        fun newInstance(path: String): UpdateItemDialog {
            val fragment = UpdateItemDialog()
            val args = Bundle()
            args.putString(PATH, path)
            fragment.arguments = args
            return fragment
        }

        fun newInstanceCloud(file: FileSec, ctype:String):UpdateItemDialog{
            val fragment = UpdateItemDialog()
            val args = Bundle()
            args.putParcelable(FILE,file)
            args.putString(TYPE,ctype)
            fragment.arguments = args
            return fragment
        }
    }


}
