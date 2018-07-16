package vinova.intern.nhomxnxx.mexplorer.logo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_logo.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity

class LogoActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_logo)
        setAnimation()
        Handler().postDelayed({
            startActivity(Intent(this,LogActivity::class.java))
            finish()
        },1500)
}

    private fun setAnimation() {
        val animation = AnimationUtils.loadAnimation(this,R.anim.logo_anim)
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.duration = 1000
        logo_app.startAnimation(animation)
    }
}