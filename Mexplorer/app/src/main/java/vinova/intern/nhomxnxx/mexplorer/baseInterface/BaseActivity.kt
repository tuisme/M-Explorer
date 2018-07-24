package vinova.intern.nhomxnxx.mexplorer.baseInterface

import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import vinova.intern.nhomxnxx.mexplorer.R

open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

	protected fun onCreateDrawer() {
		setContentView(R.layout.activity_home)
		setSupportActionBar(tool_bar_home)

		val toggle = ActionBarDrawerToggle(
				this, drawer_layout, tool_bar_home, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer_layout.addDrawerListener(toggle)
		toggle.syncState()

		nav_view.setNavigationItemSelectedListener(this)
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