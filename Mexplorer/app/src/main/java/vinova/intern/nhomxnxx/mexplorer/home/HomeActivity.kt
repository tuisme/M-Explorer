package vinova.intern.nhomxnxx.mexplorer.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home_layout.*
import kotlinx.android.synthetic.main.nav_bar_header.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.RvHomeAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.cloud.CloudActivity
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.dialogs.ConfirmDeleteDialog
import vinova.intern.nhomxnxx.mexplorer.dialogs.RenameDialog
import vinova.intern.nhomxnxx.mexplorer.local.LocalActivity
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity
import vinova.intern.nhomxnxx.mexplorer.model.Cloud
import vinova.intern.nhomxnxx.mexplorer.model.ListCloud
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment


class HomeActivity : BaseActivity(),HomeInterface.View ,
		RenameDialog.DialogListener,
		ConfirmDeleteDialog.ConfirmListener{
	private var mPresenter :HomeInterface.Presenter= HomePresenter(this)
	private lateinit var adapter : RvHomeAdapter
	private var listCloud : ListCloud = ListCloud()

	override fun logoutSuccess() {
		CustomDiaglogFragment.hideLoadingDialog()
		startActivity(Intent(this,LogActivity::class.java))
		finish()
	}

	override fun setPresenter(presenter: HomeInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {

	}

	override fun showError(message: String) {
		CustomDiaglogFragment.hideLoadingDialog()
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		super.onCreateDrawer()
		setRecyclerView()
		if (savedInstanceState==null)
			mPresenter.getList(DatabaseHandler(this).getToken())
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

	override fun showList(list: ListCloud?) {
		showUser()
		this.listCloud = list!!
		adapter.setData(list.clouds)
	}

	 private fun showUser() {
		 val user = DatabaseHandler(this).getUser()
		 val name = "${user.firstName} ${user.lastName}"
		 user_name.text = name
		 user_email.text = user.email
		 user_have_percentage.text = user.used
		 progressBar.progress = (user.used?.toFloat()?.times(100))?.toInt() ?: 0
		 Glide.with(this)
				 .load(user.avatarUrl)
				 .into(img_profile)
	}

	private fun setRecyclerView(){
		adapter = RvHomeAdapter(this,app_bar_home.findViewById(R.id.bottom_sheet_detail),supportFragmentManager)
		val manager = LinearLayoutManager(this)
		rvContent.layoutManager = manager
		rvContent.adapter = adapter
		swipeContent.setOnRefreshListener {
			mPresenter.refreshList(DatabaseHandler(this).getToken())
			swipeContent.isRefreshing = false
		}
		adapter.setListener(object : RvHomeAdapter.ItemClickListener{
			override fun onItemClick(cloud: Cloud) {
				if (cloud.ctype.equals("local"))
					startActivity(Intent(this@HomeActivity,LocalActivity::class.java))
				else {
					val intent = Intent(this@HomeActivity, CloudActivity::class.java)
					intent.putExtra("id", cloud.cid).putExtra("token",cloud.token)
					startActivity(intent)
				}
			}
		})
		fab_add.setOnClickListener {

		}
	}

	override fun onRename(fromPath: String, toPath: String) {
	}

	override fun onReNameCloud(newName: String, id: String,token:String) {
		mPresenter.renameCloud(id,newName,token,DatabaseHandler(this).getToken()!!)
	}

	override fun onConfirmDelete(path: String?) {

	}

	override fun onConfirmDeleteCloud(name: String, id: String) {
		mPresenter.deleteCloud(id,DatabaseHandler(this@HomeActivity).getToken()!!)
	}

	override fun refreshList(list: ListCloud?) {
		this.listCloud = list!!
		adapter.refreshData(list.clouds)
	}

	override fun onBackPressed() {
		if (BottomSheetBehavior.from(bottom_sheet_detail).state == BottomSheetBehavior.STATE_EXPANDED )
			BottomSheetBehavior.from(bottom_sheet_detail).state = BottomSheetBehavior.STATE_COLLAPSED
		else
			super.onBackPressed()
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
		outState?.putParcelable("list_cloud",listCloud)
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
		super.onRestoreInstanceState(savedInstanceState)
		this.listCloud = savedInstanceState?.getParcelable("list_cloud")!!
		adapter.setData(this.listCloud.clouds)
	}

	override fun refresh() {
		mPresenter.refreshList(DatabaseHandler(this).getToken())
	}
}