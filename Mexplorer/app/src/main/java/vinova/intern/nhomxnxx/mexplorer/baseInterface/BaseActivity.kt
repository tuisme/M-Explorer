package vinova.intern.nhomxnxx.mexplorer.baseInterface

import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler

open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
	private val END_SCALE = 0.8f
	private var goHome = false
	private var token : String? =null
	protected fun onCreateDrawer() {
		setContentView(R.layout.activity_home)
		setSupportActionBar(tool_bar_home)
		setNavigationDrawer()
		token = DatabaseHandler(this).getToken()
	}

	private fun setNavigationDrawer(){
		val toggle =
				ActionBarDrawerToggle(this,drawer_layout, tool_bar_home,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
		drawer_layout?.addDrawerListener(toggle)
		toggle.isDrawerIndicatorEnabled = false
		toggle.setHomeAsUpIndicator(R.drawable.ic_menu)
		toggle.setToolbarNavigationClickListener {
			if (drawer_layout.isDrawerVisible(GravityCompat.START)) {
				drawer_layout.closeDrawer(GravityCompat.START)
			} else {
				drawer_layout.openDrawer(GravityCompat.START)

			}
		}
		toggle.syncState()

		drawer_layout?.addDrawerListener(object : DrawerLayout.SimpleDrawerListener(){
			override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
				val diffScaledOffset = slideOffset * (1 - END_SCALE)

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

	override fun onBackPressed() {
		if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
			drawer_layout.closeDrawer(GravityCompat.START)
		} else {
			super.onBackPressed()
		}
	}

	override fun onNavigationItemSelected(p0: MenuItem): Boolean {
		when(p0.itemId){
			R.id.home -> {

			}

			R.id.bookmark->{

			}

			R.id.signout -> {

			}
		}
		drawer_layout?.closeDrawer(GravityCompat.START)
		return true
	}

}