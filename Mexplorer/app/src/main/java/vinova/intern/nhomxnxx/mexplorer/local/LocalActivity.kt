package vinova.intern.nhomxnxx.mexplorer.local

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_home_layout.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.LocalAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.device.DeviceActivity
import vinova.intern.nhomxnxx.mexplorer.dialogs.*
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity
import vinova.intern.nhomxnxx.mexplorer.model.User
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment
import vinova.intern.nhomxnxx.mexplorer.utils.Support
import java.io.File


class LocalActivity :BaseActivity(),LocalInterface.View, AddItemsDialog.DialogListener,
        LocalAdapter.OnFileItemListener,
        UpdateItemDialog.DialogListener,
        NewFolderDialog.DialogListener,
        NewTextFileDialog.DialogListener,
        ConfirmDeleteDialog.ConfirmListener,
        RenameDialog.DialogListener,
        ProfileDialog.DialogListener{

    private var mPresenter :LocalInterface.Presenter= LocalPresenter(this,this)
    var mMovingPath:String? = null
    var mCopy:Boolean =false
    lateinit var adapter: LocalAdapter
    val CAPTURE_IMAGE_REQUEST = 20


    override fun openFile(url: File) {
        try {
            val uri = Uri.fromFile(url)
            val intent = Intent(Intent.ACTION_VIEW)
            val apkURI = FileProvider.getUriForFile(this, applicationContext
                    .packageName + ".provider", url)
            intent.setDataAndType(apkURI, Support.getMimeType(this, uri))
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        }
        catch (e: ActivityNotFoundException){
            Toasty.info(this,"No support this file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun setPresenter(presenter: LocalInterface.Presenter) {
        this.mPresenter = presenter
    }

    override fun showToast(mes: String) {
        if(error_nothing.visibility == View.VISIBLE)
            error_nothing.visibility = View.GONE
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

    override fun onReNameCloud(newName: String, id: String, isDic: Boolean, token: String) {

    }

    override fun onClick(file: File) {
        mPresenter.openFileOrFolder(adapter,file)
    }

    override fun onLongClick(file: File) {
        UpdateItemDialog.newInstance(file.absolutePath).show(supportFragmentManager, "update_item")
    }

    @TargetApi(Build.VERSION_CODES.M)
    @SuppressLint("ObsoleteSdkInt")
    override fun onOptionClick(which: Int, path: String?) {
        when (which) {
            R.id.new_file -> NewTextFileDialog.newInstance().show(supportFragmentManager, "new_file_dialog")
            R.id.new_folder -> NewFolderDialog.newInstance().show(supportFragmentManager, "new_folder_dialog")
            R.id.new_image -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    captureImage()
                }
            }
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

    override fun onConfirmDeleteCloud(name: String, isDic: Boolean, id: String) {

    }

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreateDrawer()
        rvContent.layoutManager = LinearLayoutManager(this)
        adapter = LocalAdapter(this,error_nothing)
        rvContent.addItemDecoration(DividerItemDecoration(rvContent.context, DividerItemDecoration.VERTICAL))
        rvContent.adapter = adapter
        adapter.setListener(this)
        title = adapter.path
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
            moving_layout.visibility = View.GONE
            mMovingPath = null
        }
    }

    private fun isStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                true
            } else {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            2222-> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
                } else {
                    Toasty.warning(this, "Permission Denied, Please allow to proceed !", Toast.LENGTH_LONG).show()
                }
            }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            mPresenter.saveImage(data, adapter)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun captureImage() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),2222)
        }
        else {
            val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.home->{
	            finish()
	            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
            R.id.signout->{
                CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
                mPresenter.logout(this, DatabaseHandler(this).getToken())
            }
            R.id.bookmark->{
                super.onBackPressed()
            }
            R.id.device_connected -> {
                val intent = Intent(this, DeviceActivity::class.java)
                startActivity(intent)
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

    override fun onUpdate(user: User) {
        mPresenter.updateUser(user.first_name!!,user.last_name!!, Uri.parse(user.avatar_url))
    }

    override fun updateUser() {
        super.loadUser()
    }
}