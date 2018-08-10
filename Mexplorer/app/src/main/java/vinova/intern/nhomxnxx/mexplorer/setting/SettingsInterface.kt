package vinova.intern.nhomxnxx.mexplorer.setting

import android.content.Context
import android.content.Intent
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView

interface SettingsInterface {
    interface View:BaseView<Presenter>{
        fun setSwitch(isChecked: Boolean)
    }

    interface Presenter{
        fun encryptFile(context: Context, data: Intent)
        fun authentication(context: Context, data: Intent)
    }
}