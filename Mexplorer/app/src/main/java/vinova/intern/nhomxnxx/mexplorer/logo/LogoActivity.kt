package vinova.intern.nhomxnxx.mexplorer.logo

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_logo.*
import vinova.intern.nhomxnxx.mexplorer.R

class LogoActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar!!.hide()
        setContentView(R.layout.activity_logo)
        setAnimation()
//        Handler().postDelayed({
//            startActivity(intent)
//            finish()
//        },1500)
}

    private fun setAnimation() {
        val animation = AnimationUtils.loadAnimation(this,R.anim.logo_anim)
        animation.interpolator = LinearInterpolator()
        animation.repeatCount = Animation.INFINITE
        animation.duration = 1000
        logo_app.startAnimation(animation)
    }
}