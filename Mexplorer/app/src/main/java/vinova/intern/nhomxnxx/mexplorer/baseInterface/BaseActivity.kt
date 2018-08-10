package vinova.intern.nhomxnxx.mexplorer.baseInterface

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import com.google.android.material.navigation.NavigationView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.nav_bar_header.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.dialogs.ProfileDialog
import vinova.intern.nhomxnxx.mexplorer.utils.Support

open class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
	private val END_SCALE = 0.8f
	private var token : String? =null
	protected lateinit var fullAds : InterstitialAd
	protected lateinit var mRewardedVideoAd: RewardedVideoAd
	private var i = 0

	protected var mMessageReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context?, intent: Intent?) {

		}
	}

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
		nav_view.getHeaderView(0).img_profile.setOnClickListener {
			ProfileDialog.getInstance(DatabaseHandler(this).getUser()).show(supportFragmentManager,"fragment")
		}
		loadUser()
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

	@SuppressLint("SetTextI18n")
	fun loadUser(){
		val parentView = nav_view.getHeaderView(0)
		val user = DatabaseHandler(this).getUser()
		val name = "${user.first_name} ${user.last_name}"
		parentView.user_name.text = name
		if (user.is_vip!!)
			parentView.user_name.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_vip,0)
		parentView.user_email.text = user.email
		parentView.user_have_percentage.text = "${Support.getFileSize(user.used!!.toLong())} of ${Support.getFileSize(user.allocated!!.toLong())}"
		parentView.progressBar.progress = ((user.used!!/user.allocated!!)*100).toInt()
		Glide.with(this)
				.load(user.avatar_url)
				.into(parentView.img_profile)
	}

	protected fun setAdsListener(ctx : Context,mPresenter : BasePresenter,userToken : String){
		val adId = getString(R.string.ads_id_vid)

		MobileAds.initialize(ctx,adId)

		mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(ctx)

		mRewardedVideoAd.loadAd(adId, AdRequest.Builder().build())

		mRewardedVideoAd.rewardedVideoAdListener = object : RewardedVideoAdListener{
			override fun onRewardedVideoAdClosed() {
				Log.e("ABCD","ad closed")
			}

			override fun onRewardedVideoAdLeftApplication() {
				Log.e("ABCD","ad left")
			}

			override fun onRewardedVideoAdLoaded() {
				Log.e("ABCD","ad load")
			}

			override fun onRewardedVideoAdOpened() {
			}

			override fun onRewardedVideoCompleted() {
				mPresenter.redeem(userToken)
			}

			override fun onRewarded(p0: RewardItem?) {
				mPresenter.redeem(userToken)
			}

			override fun onRewardedVideoStarted() {
				Log.e("ABCD","ad started")
				mRewardedVideoAd.loadAd(adId,AdRequest.Builder().build())
			}

			override fun onRewardedVideoAdFailedToLoad(p0: Int) {
				Log.e("ABCD","ad failed to load ${i++}")
				mRewardedVideoAd.loadAd(adId,AdRequest.Builder().build())
			}

		}

		nav_view.getHeaderView(0).buy_space.setOnClickListener {
			if (mRewardedVideoAd.isLoaded){
				mRewardedVideoAd.show()
			}
			else
				Toasty.normal(ctx,"No video ads available", Toast.LENGTH_SHORT).show()
		}
	}

}