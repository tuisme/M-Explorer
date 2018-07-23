package vinova.intern.nhomxnxx.mexplorer.local

import android.os.Environment.getExternalStorageDirectory
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import kotlinx.android.synthetic.main.content_home_layout.*
import vinova.intern.nhomxnxx.mexplorer.adapter.LocalAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity


class LocalActivity :BaseActivity(){
    lateinit var adapter: LocalAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.onCreateDrawer()
        rvContent.layoutManager = LinearLayoutManager(this)
        adapter = LocalAdapter(this)
        rvContent.adapter = adapter
        adapter.setData()
    }

    override fun onBackPressed() {
        if (adapter.path == getExternalStorageDirectory().absolutePath)
        super.onBackPressed()
        else {
            adapter.path = File(adapter.path).parent
            adapter.setData()
            adapter.notifyDataSetChanged()
        }
    }

}