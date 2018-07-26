package vinova.intern.nhomxnxx.mexplorer.local

import android.content.Context
import vinova.intern.nhomxnxx.mexplorer.adapter.LocalAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseView
import java.io.File

interface LocalInterface {
    interface View : BaseView<Presenter>{
        fun showToast(mes: String)
        fun openFile(url: File)
        fun logoutSuccess()
    }

    interface Presenter{
        fun moveOrCopy(mMovingPath: String?, mCopy: Boolean, adapter: LocalAdapter)
        fun delete(path: String)
        fun rename(fromPath: String, toPath: String)
        fun newFile(path:String ,name: String, content: String)
        fun newFolder(adapter: LocalAdapter, name: String)
        fun openFileOrFolder(adapter: LocalAdapter, file: File)
        fun logout(context: Context?, token: String?)

    }
}