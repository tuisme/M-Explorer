package vinova.intern.nhomxnxx.mexplorer.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.view.MenuItem
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home_layout.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.LocalAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.dialogs.*
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment
import java.io.File


class LocalActivity :BaseActivity(),LocalInterface.View, AddItemsDialog.DialogListener,
        LocalAdapter.OnFileItemListener,
        UpdateItemDialog.DialogListener,
        NewFolderDialog.DialogListener,
        NewTextFileDialog.DialogListener,
        ConfirmDeleteDialog.ConfirmListener,
        RenameDialog.DialogListener{

    private var mPresenter :LocalInterface.Presenter= LocalPresenter(this)
    var mMovingPath:String? = null
    var mCopy:Boolean =false
    lateinit var adapter: LocalAdapter

    override fun openFile(url: File) {
        val uri = Uri.fromFile(url)
        val intent = Intent(Intent.ACTION_VIEW)
        val apkURI = FileProvider.getUriForFile(this, applicationContext
                .packageName + ".provider", url)
        intent.setDataAndType(apkURI, getMimeType(uri))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(intent)
    }

    override fun setPresenter(presenter: LocalInterface.Presenter) {
        this.mPresenter = presenter
    }

    override fun showToast(mes: String) {
        Toast.makeText(this, mes, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading(isShow: Boolean) {
        if (isShow) CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
        else CustomDiaglogFragment.hideLoadingDialog()
    }

    override fun showError(message: String) {
    }

    override fun onNewFolder(name: String) {
        mPresenter.newFolder(adapter,name)
    }

    override fun onNewFile(name: String, content: String) {
        mPresenter.newFile(adapter.path, name, content)
        adapter.refreshData()
    }

    override fun onRename(fromPath: String, toPath: String) {
        mPresenter.rename(fromPath, toPath)
        adapter.refreshData()
    }

    override fun onReNameCloud(newName: String, id: String, token: String) {

    }

    override fun onClick(file: File) {
        mPresenter.openFileOrFolder(adapter,file)
    }

    override fun onLongClick(file: File) {
        UpdateItemDialog.newInstance(file.absolutePath).show(supportFragmentManager, "update_item")
    }

    override fun onOptionClick(which: Int, path: String?) {
        when (which) {
            R.id.new_file -> NewTextFileDialog.newInstance().show(supportFragmentManager, "new_file_dialog")
            R.id.new_folder -> NewFolderDialog.newInstance().show(supportFragmentManager, "new_folder_dialog")
            R.id.delete -> ConfirmDeleteDialog.newInstance(path.toString()).show(supportFragmentManager, "confirm_delete")
            R.id.rename -> RenameDialog.newInstance(path.toString()).show(supportFragmentManager, "rename")
            R.id.move -> {
                moving_file_name.text = getString(R.string.moving_file, File(path).name)
                mMovingPath = path
                mCopy = false
                moving_layout.visibility = View.VISIBLE
            }
            R.id.copy -> {
                moving_file_name.text = getString(R.string.copy_file, File(path).name)
                mMovingPath = path
                mCopy = true
                moving_layout.visibility = View.VISIBLE
            }
        }
    }

    override fun onConfirmDelete(path: String?) {
        mPresenter.delete(path.toString())
        adapter.refreshData()
    }

    override fun onConfirmDeleteCloud(name: String, id: String) {

    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreateDrawer()
        rvContent.layoutManager = LinearLayoutManager(this)
        adapter = LocalAdapter(this,error_nothing)
        rvContent.adapter = adapter
        adapter.setListener(this)
        if (isStoragePermissionGranted()){
            adapter.refreshData()
        }
        fab_add.visibility = View.VISIBLE
        fab_add.setOnClickListener {
            AddItemsDialog.newInstance().show(supportFragmentManager, "add_items")
        }
        swipeContent.isEnabled =false

        accept_move.setOnClickListener {
            moving_layout.visibility = View.GONE
            mPresenter.moveOrCopy(mMovingPath,mCopy,adapter)
            mMovingPath = null
        }

        decline_move.setOnClickListener {
            moving_layout.visibility = View.GONE;
            mMovingPath = null
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true
            } else {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
            adapter.refreshData()
        }
    }


    override fun onBackPressed() {
        if (adapter.path == getExternalStorageDirectory().absolutePath)
        super.onBackPressed()
        else {
            if(error_nothing.visibility == View.VISIBLE)
                error_nothing.visibility = View.GONE
            adapter.path = File(adapter.path).parent
            adapter.refreshData()
        }
    }


    private fun getMimeType(uri: Uri): String? {
        val mimeType: String?
        mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = this.contentResolver
            cr.getType(uri)
        } else {
            val regex = Regex("[^A-Za-z0-9 .]")
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString().replace(regex, ""))
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase())
        }
        return mimeType
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.home->{

            }
            R.id.signout->{
                CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
                mPresenter.logout(this, DatabaseHandler(this).getToken())
            }
            R.id.bookmark->{

            }
        }
        drawer_layout?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun logoutSuccess() {
        CustomDiaglogFragment.hideLoadingDialog()
        startActivity(Intent(this, LogActivity::class.java))
        finish()
    }
}