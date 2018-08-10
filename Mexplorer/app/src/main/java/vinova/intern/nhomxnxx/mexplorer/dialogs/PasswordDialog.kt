package vinova.intern.nhomxnxx.mexplorer.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.andrognito.patternlockview.PatternLockView
import com.andrognito.patternlockview.listener.PatternLockViewListener
import com.andrognito.patternlockview.utils.PatternLockUtils
import es.dmoral.toasty.Toasty
import vinova.intern.nhomxnxx.mexplorer.R

@Suppress("UNREACHABLE_CODE", "UNUSED_EXPRESSION")
class PasswordDialog:DialogFragment() {

    private var mListener: PasswordDialog.DialogListener? = null

    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, theme)

        val func = arguments?.getInt(PasswordDialog.FUNC)

        val pass = arguments?.getString(PasswordDialog.PASS)

        val isTurnOff = arguments?.getBoolean(PasswordDialog.TURN)

        val view = LayoutInflater.from(activity).inflate(R.layout.lock_dialog, null)
        dialog.setContentView(view)
        dialog.setCancelable(true)

        val header = view.findViewById<TextView>(R.id.tv_pass)
        val patternLockView = view.findViewById<PatternLockView>(R.id.pattern_lock_view)
        if (func != 1) header.text = "Verify password"

        patternLockView.addPatternLockListener(object:PatternLockViewListener{
            @SuppressLint("CommitPrefEdits", "SetTextI18n")
            override fun onComplete(pattern: MutableList<PatternLockView.Dot>?) {
                when(func) {
                    // create pass
                    1 -> {
                        PasswordDialog.newInstance(2,PatternLockUtils.patternToString(patternLockView,pattern)).show(fragmentManager,"fragment")
                        patternLockView.clearPattern()
                        dialog.dismiss()
                    }
                    2 -> {
                        if (pass == PatternLockUtils.patternToString(patternLockView,pattern)){
                            mListener?.savePass( pass?.toByteArray(Charsets.UTF_8)!!)
                            patternLockView.clearPattern()
                            dialog.dismiss()
                        }
                        else {
                            patternLockView.clearPattern()
                            context?.let { Toasty.error(it,"Not match", Toast.LENGTH_SHORT).show() }
                        }
                    }
                    3 -> {
                        if (pass == PatternLockUtils.patternToString(patternLockView,pattern) && !isTurnOff!!){
                            patternLockView.clearPattern()
                            mListener?.isAuth(true)
                            dialog.dismiss()
                        }
                        else if(pass == PatternLockUtils.patternToString(patternLockView,pattern) && isTurnOff!!){
                            patternLockView.clearPattern()
                            mListener?.turnOff()

                            dialog.dismiss()
                        }
                        else {
                            patternLockView.clearPattern()
                            context?.let { Toasty.error(it,"Not match", Toast.LENGTH_SHORT).show() }
                        }
                    }
                }

            }
            override fun onCleared() {
            }

            override fun onStarted() {
            }

            override fun onProgress(progressPattern: MutableList<PatternLockView.Dot>?) {
            }
        })
        dialog.setOnKeyListener { _, i, _ ->
            if (i == KeyEvent.KEYCODE_BACK) {
                if (func == 1 || func == 2)
                    mListener?.setSwitch(false)
                else if (func == 3 && isTurnOff!!) {
                    mListener?.setSwitch(true)
                }
                true

            }
            false
        }
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
    interface DialogListener {
        fun isAuth(isAuth: Boolean)
        fun savePass(pass: ByteArray)
        fun turnOff()
        fun setSwitch(isChecked: Boolean)
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
        val frag = this
        private val FUNC = "func"
        private val PASS = "pass"
        private val TURN = "turn"

        fun newInstance(func: Int, pass: String?, isTurnOff:Boolean = false): PasswordDialog {
            val fragment = PasswordDialog()
            val args = Bundle()
            args.putInt(FUNC, func)
            args.putString(PASS, pass)
            args.putBoolean(TURN, isTurnOff)

            fragment.arguments = args
            return fragment
        }
    }
}