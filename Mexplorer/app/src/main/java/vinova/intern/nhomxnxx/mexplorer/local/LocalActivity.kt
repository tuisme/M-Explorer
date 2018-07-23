package vinova.intern.nhomxnxx.mexplorer.local

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment.getExternalStorageDirectory
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.content_home_layout.*
import vinova.intern.nhomxnxx.mexplorer.adapter.LocalAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import java.io.File


class LocalActivity :BaseActivity(){
    lateinit var adapter: LocalAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreateDrawer()
        rvContent.layoutManager = LinearLayoutManager(this)
        adapter = LocalAdapter(this,error_nothing)
        rvContent.adapter = adapter
        if (isStoragePermissionGranted()){
            adapter.setData()
            adapter.notifyDataSetChanged()
        }
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

}