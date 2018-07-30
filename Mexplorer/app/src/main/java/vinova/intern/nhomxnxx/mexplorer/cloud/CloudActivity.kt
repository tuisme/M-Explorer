package vinova.intern.nhomxnxx.mexplorer.cloud

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home_layout.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.CloudAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity
import vinova.intern.nhomxnxx.mexplorer.model.FileDetail
import vinova.intern.nhomxnxx.mexplorer.model.FileSec
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment

class CloudActivity : BaseActivity(),CloudInterface.View {
	private lateinit var adapter : CloudAdapter
	var mPresenter : CloudInterface.Presenter = CloudPresenter(this)
	lateinit var token : String
	lateinit var userToken : String
	lateinit var cloudType : String
	lateinit var cloudId : String
	val path : ArrayList<String> = arrayListOf()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		super.onCreateDrawer()
		setRv()
		title = intent.getStringExtra("name")
		userToken = DatabaseHandler(this).getToken()!!
	}

	fun setRv(){
		adapter = CloudAdapter(this,error_nothing,bottom_sheet_detail,supportFragmentManager)
		rvContent.layoutManager = LinearLayoutManager(this)
		rvContent.adapter = adapter
		swipeContent.isEnabled = false
		token = intent.getStringExtra("token")
		cloudType = intent.getStringExtra("type")
		cloudId = intent.getStringExtra("id")
		path.add(cloudId)
		CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
		mPresenter.getList(cloudId,token,DatabaseHandler(this).getToken()!!,cloudType)
		adapter.setListener(object : CloudAdapter.ItemClickListener{
			override fun onItemClick(file: FileSec) {
				CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
				if (file.mime_type!!.contains("folder")) {
					mPresenter.getList(file.id!!, token, DatabaseHandler(this@CloudActivity).getToken()!!, cloudType)
					path.add(file.id!!)
				}
				else{
					mPresenter.getUrlFile(file.id!!,token,userToken,cloudType)
				}
			}
		})
	}

	override fun showList(files: List<FileSec>) {
		CustomDiaglogFragment.hideLoadingDialog()
		adapter.setData(files)
		adapter.notifyDataSetChanged()
	}

	override fun showFile(file: FileDetail) {
		CustomDiaglogFragment.hideLoadingDialog()
		mPresenter.openFile(this@CloudActivity,file.url!!)
	}

	override fun setPresenter(presenter: CloudInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {
	}

	override fun showError(message: String) {
		CustomDiaglogFragment.hideLoadingDialog()
		Toasty.error(this,message,Toast.LENGTH_SHORT).show()
	}

	override fun logoutSuccess() {
		CustomDiaglogFragment.hideLoadingDialog()
		startActivity(Intent(this, LogActivity::class.java))
		finish()
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

	override fun onBackPressed() {
		if (path.size > 1){
			path.removeAt(path.size-1)
			val id = path.last()
			mPresenter.getList(id,token,userToken,cloudType)
		}
		else
			super.onBackPressed()
	}
}