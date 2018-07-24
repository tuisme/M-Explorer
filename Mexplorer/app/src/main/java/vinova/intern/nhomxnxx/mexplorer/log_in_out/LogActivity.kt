package vinova.intern.nhomxnxx.mexplorer.log_in_out

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_log.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.PageAdapter

class LogActivity:AppCompatActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_log)
		changeTab()
	}

	private fun changeTab(){
		val manager: FragmentManager = supportFragmentManager
		val adapter = PageAdapter(manager)
		view_pager.adapter = adapter
		tab_layout.setupWithViewPager(view_pager)
		view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))
	}
}