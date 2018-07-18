package vinova.intern.nhomxnxx.mexplorer.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity

class HomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener,HomeInterface.View {
	var mPresenter :HomeInterface.Presenter= HomePresenter(this)

	override fun logoutSuccess() {

		startActivity(Intent(this,LogActivity::class.java))
		finish()
	}

	override fun setPresenter(presenter: HomeInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun showError(message: String) {
		Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
	}

	private val END_SCALE = 0.7f

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_home)
		setSupportActionBar(tool_bar_home)
		setNavigationDrawer()
	}

	override fun onNavigationItemSelected(p0: MenuItem): Boolean {
		when(p0.itemId){
			R.id.home->{

			}
			R.id.signout->{
				mPresenter.logout(this, DatabaseHandler(this).getToken())
			}
			R.id.plus->{
				nav_view.menu.add(Menu.NONE,Menu.NONE,1,"SomeDrive").setIcon(R.drawable.ic_drive_icon)
			}
		}
		drawer_layout?.closeDrawer(GravityCompat.START)
		return true
	}

	private fun setNavigationDrawer(){
		val toggle =
				ActionBarDrawerToggle(this,drawer_layout, tool_bar_home,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
		drawer_layout?.addDrawerListener(toggle)
		toggle.syncState()

		drawer_layout?.addDrawerListener(object : DrawerLayout.SimpleDrawerListener(){
			override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
				val diffScaledOffset = slideOffset * (1 - END_SCALE)
				val offsetScale = 1 - diffScaledOffset

				app_bar_home.scaleX = offsetScale
				app_bar_home.scaleY = offsetScale

				val  xOffset = drawerView.width * slideOffset
				val  xOffsetDiff = app_bar_home.width * diffScaledOffset / 2
				val  xTranslation = xOffset - xOffsetDiff

				app_bar_home.translationX = xTranslation
			}
		})
		nav_view?.setNavigationItemSelectedListener(this)
		nav_view?.menu?.getItem(0)?.isChecked = true
	}
}