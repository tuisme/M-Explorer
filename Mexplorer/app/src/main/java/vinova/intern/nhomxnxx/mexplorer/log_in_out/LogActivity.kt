package vinova.intern.nhomxnxx.mexplorer.log_in_out

import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.tabs.TabLayout
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_log.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.PageAdapter
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.dialogs.ForgotDialog
import vinova.intern.nhomxnxx.mexplorer.model.BaseResponse

class LogActivity:AppCompatActivity(),ForgotDialog.DialogListener {
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

	override fun onForget(email: String) {
		CallApi.getInstance().forgotPass(email)
				.enqueue(object : Callback<BaseResponse> {
					override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {

					}

					override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
						Toasty.success(this@LogActivity,"Please check your email", Toast.LENGTH_SHORT).show()
					}
				})
	}
}