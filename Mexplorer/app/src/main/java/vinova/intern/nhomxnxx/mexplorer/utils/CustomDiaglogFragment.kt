package vinova.intern.nhomxnxx.mexplorer.utils

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import vinova.intern.nhomxnxx.mexplorer.R

class CustomDiaglogFragment:DialogFragment() {
    companion object {

        private var loading: CustomDiaglogFragment? = null

        private fun newInstance(): CustomDiaglogFragment {
            return CustomDiaglogFragment()
        }

        fun showLoadingDialog(fm: FragmentManager?) {
            if (loading == null) {
                loading = newInstance()
            }
            loading?.show(fm, "fragment")
        }

        fun hideLoadingDialog() {
            loading?.dismiss()
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(this@CustomDiaglogFragment.requireActivity())
        val inflater = activity?.layoutInflater
        val view = inflater?.inflate(R.layout.loading_fragment, null)
        alertDialogBuilder.setView(view)
        val dialog = alertDialogBuilder.create()
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window.setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}