package vinova.intern.nhomxnxx.mexplorer.local

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.snatik.storage.Storage
import kotlinx.android.synthetic.main.content_home_layout.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.LocalAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.dialogs.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class LocalActivity :BaseActivity(), AddItemsDialog.DialogListener,
        LocalAdapter.OnFileItemListener,
        UpdateItemDialog.DialogListener,
        NewFolderDialog.DialogListener,
        NewTextFileDialog.DialogListener,
        ConfirmDeleteDialog.ConfirmListener,
        RenameDialog.DialogListener{
    override fun onNewFolder(name: String) {
        val currentPath = adapter.path
        val folderPath = currentPath + File.separator + name
        val created:Boolean =
            if (File(folderPath).exists()) {
                false
            }
            else File(folderPath).mkdirs()

        if (created) {
            adapter.setData()
            adapter.notifyDataSetChanged()
            Toast.makeText(this,"New folder created: $name",Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"Failed create folder: $name",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNewFile(name: String, content: String, encrypt: Boolean) {
        val currentPath = adapter.path
        val folderPath = currentPath + File.separator + name
        createFile(folderPath, content.toByteArray())
        adapter.setData()
        adapter.notifyDataSetChanged()
        Toast.makeText(this,"New file created: $name",Toast.LENGTH_SHORT).show()
    }

    override fun onRename(fromPath: String, toPath: String) {
        val file = File(fromPath)
        val newFile = File(toPath)
        file.renameTo(newFile)
        adapter.setData()
        adapter.notifyDataSetChanged()
        Toast.makeText(this,"Renamed",Toast.LENGTH_SHORT).show()
    }

    override fun onClick(file: File) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLongClick(file: File) {
        UpdateItemDialog.newInstance(file.absolutePath).show(fragmentManager, "update_item")

    }

    override fun onOptionClick(which: Int, path: String?) {
        when (which) {
            R.id.new_file -> NewTextFileDialog.newInstance().show(fragmentManager, "new_file_dialog")
            R.id.new_folder -> NewFolderDialog.newInstance().show(fragmentManager, "new_folder_dialog")
            R.id.delete -> ConfirmDeleteDialog.newInstance(path.toString()).show(fragmentManager, "confirm_delete")
            R.id.rename -> RenameDialog.newInstance(path.toString()).show(fragmentManager, "rename")
//            R.id.move -> {
//                mo.setText(getString(R.string.moving_file, mStorage?.getFile(path).getName()))
//                mMovingPath = path
//                mCopy = false
//                mMovingLayout.setVisibility(View.VISIBLE)
//            }
//            R.id.copy -> {
//                mMovingText.setText(getString(R.string.copy_file, mStorage.getFile(path).getName()))
//                mMovingPath = path
//                mCopy = true
//                mMovingLayout.setVisibility(View.VISIBLE)
//            }
        }    }

    override fun onConfirmDelete(path: String?) {
        if (File(path).isDirectory) {
            deleteDirectoryImpl(path.toString())
            Toast.makeText(this,"Folder was deleted", Toast.LENGTH_SHORT).show()
        } else {
            val file = File(path)
            file.delete()
            Toast.makeText(this,"File was deleted", Toast.LENGTH_SHORT).show()
        }
        adapter.setData()
        adapter.notifyDataSetChanged()
    }

    lateinit var adapter: LocalAdapter
    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreateDrawer()
        rvContent.layoutManager = LinearLayoutManager(this)
        adapter = LocalAdapter(this,error_nothing)
        rvContent.adapter = adapter
        adapter.setListener(this)
        if (isStoragePermissionGranted()){
            adapter.setData()
            adapter.notifyDataSetChanged()
        }
        fab_add.visibility = View.VISIBLE
        fab_add.setOnClickListener {
            AddItemsDialog.newInstance().show(fragmentManager, "add_items")
        }
        swipeContent.isEnabled =false

    }

    fun isStoragePermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("ABCD", "Permission is granted")
                return true
            } else {

                Log.v("ABCD", "Permission is revoked")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                return false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("ABCD", "Permission is granted")
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("ABCD","Permission: "+permissions[0]+ "was "+grantResults[0])
            //resume tasks needing this permission
            adapter.setData()
            adapter.notifyDataSetChanged()
        }
    }


    override fun onBackPressed() {
        if (adapter.path == getExternalStorageDirectory().absolutePath)
        super.onBackPressed()
        else {
            if(error_nothing.visibility == View.VISIBLE)
                error_nothing.visibility = View.GONE
            adapter.path = File(adapter.path).parent
            adapter.setData()
            adapter.notifyDataSetChanged()
        }
    }

    private fun deleteDirectoryImpl(path: String): Boolean {
        val directory = File(path)

        // If the directory exists then delete
        if (directory.exists()) {
            val files = directory.listFiles() ?: return true
        // Run on all sub files and folders and delete them
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    deleteDirectoryImpl(files[i].absolutePath)
                } else {
                    files[i].delete()
                }
            }
        }
        return directory.delete()
    }

    private fun createFile(path: String, content_: ByteArray): Boolean {
        val content = content_
        try {
            val stream = FileOutputStream(File(path))
            stream.write(content)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            return false
        }

        return true
    }
}