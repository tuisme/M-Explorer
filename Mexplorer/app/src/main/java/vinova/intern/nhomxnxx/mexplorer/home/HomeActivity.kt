package vinova.intern.nhomxnxx.mexplorer.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
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
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity
import vinova.intern.nhomxnxx.mexplorer.model.ListCloud
import vinova.intern.nhomxnxx.mexplorer.model.User
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment


class HomeActivity : BaseActivity(),HomeInterface.View {
	private var mPresenter :HomeInterface.Presenter= HomePresenter(this)
	private lateinit var adapter : RvHomeAdapter
	private lateinit var listCloud : ListCloud
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

	private val END_SCALE = 0.8f

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_home)
		super.onCreateDrawer()
		setSupportActionBar(tool_bar_home)
		setNavigationDrawer()
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
		this.listCloud = list!!
		adapter.setData(list.clouds)
	}

	override fun showUser(user: User?) {
		val name = "${user?.firstName} ${user?.lastName}"
		user_name.text = name
		user_email.text = user?.email
		user_have_percentage.text = user?.used
		progressBar.progress = (user?.used?.toFloat()!! *100).toInt()
		Glide.with(this)
				.load(user?.avatarUrl)
				.into(img_profile)
	}

	private fun setNavigationDrawer(){
		val toggle =
				ActionBarDrawerToggle(this,drawer_layout, tool_bar_home,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
		drawer_layout?.addDrawerListener(toggle)
		toggle.syncState()

		drawer_layout?.addDrawerListener(object : DrawerLayout.SimpleDrawerListener(){
			override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
				val diffScaledOffset = slideOffset * (1 - END_SCALE)
//				val offsetScale = 1 - diffScaledOffset
//
//				app_bar_home.scaleX = offsetScale
//				app_bar_home.scaleY = offsetScale

				val  xOffset = drawerView.width * slideOffset
				val  xOffsetDiff = app_bar_home.width * diffScaledOffset / 2
				val  xTranslation = xOffset - xOffsetDiff

				app_bar_home.translationX = xTranslation
			}
		})
		nav_view?.setNavigationItemSelectedListener(this)
		nav_view?.menu?.getItem(0)?.isChecked = true
		nav_view?.itemIconTintList = null
	}

	private fun setRecyclerView(){
		adapter = RvHomeAdapter(this,app_bar_home.findViewById(R.id.bottom_sheet_detail))
		val manager = LinearLayoutManager(this)
		rvContent.layoutManager = manager
		rvContent.adapter = adapter

		swipeContent.setOnRefreshListener {
			mPresenter.getList(DatabaseHandler(this).getToken())
			swipeContent.isRefreshing = false
		}
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
}