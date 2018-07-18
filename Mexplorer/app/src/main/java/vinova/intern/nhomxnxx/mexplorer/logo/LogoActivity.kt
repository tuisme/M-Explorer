package vinova.intern.nhomxnxx.mexplorer.logo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.activity_logo.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.home.HomeActivity
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity

class LogoActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_logo)
        setAnimation()

        Handler().postDelayed({
            if(isLogin()) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
            else {
                val option: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, logo_app, ViewCompat.getTransitionName(logo_app).toString())
                startActivity(Intent(this, LogActivity::class.java), option.toBundle())
                finish()
            }
        },2000)
}

    private fun isLogin(): Boolean {
        val databaseAccess = DatabaseHandler(this)
        val username = databaseAccess.getUserLoggedIn()
        return username!= null
    }

    private fun setAnimation() {
        val animation = AnimationUtils.loadAnimation(this,R.anim.logo_anim)
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.duration = 1000
        logo_app.startAnimation(animation)
    }
}