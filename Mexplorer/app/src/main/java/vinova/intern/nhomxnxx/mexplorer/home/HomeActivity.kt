package vinova.intern.nhomxnxx.mexplorer.home

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_home.*
import vinova.intern.nhomxnxx.mexplorer.R

class HomeActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {


	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_home)
		setSupportActionBar(tool_bar_home)


	}

	override fun onNavigationItemSelected(p0: MenuItem): Boolean {
		return true
	}

	private fun setNavigationDrawer(){

	}
}