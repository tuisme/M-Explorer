package vinova.intern.nhomxnxx.mexplorer.cloud

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.content_home_layout.*
import vinova.intern.nhomxnxx.mexplorer.adapter.CloudAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.File
import vinova.intern.nhomxnxx.mexplorer.model.FileSec

class CloudActivity : BaseActivity(),CloudInterface.View {
	private lateinit var adapter : CloudAdapter
	var mPresenter : CloudInterface.Presenter = CloudPresenter(this)
	lateinit var token : String
	lateinit var userToken : String
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		super.onCreateDrawer()
		setRv()
		userToken = DatabaseHandler(this).getToken()!!
	}

	fun setRv(){
		adapter = CloudAdapter(this,error_nothing)
		rvContent.layoutManager = LinearLayoutManager(this)
		rvContent.adapter = adapter
		swipeContent.isEnabled = false
		token = intent.getStringExtra("token")
		mPresenter.getList(intent.getStringExtra("id"),token,DatabaseHandler(this).getToken()!!)
		adapter.setListener(object : CloudAdapter.ItemClickListener{
			override fun onItemClick(file: File) {
				if (file.type.equals("folder"))
					mPresenter.getList(file.id!!,token,DatabaseHandler(this@CloudActivity).getToken()!!)
				else{
					mPresenter.getUrlFile(file.id!!,token,userToken)
				}
			}
		})
	}

	override fun showList(files: List<File>) {
		adapter.setData(files)
		adapter.notifyDataSetChanged()
	}

	override fun showFile(file: FileSec) {
		mPresenter.openFile(this@CloudActivity,file.url!!)
	}

	override fun setPresenter(presenter: CloudInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {
	}

	override fun showError(message: String) {
	}

}