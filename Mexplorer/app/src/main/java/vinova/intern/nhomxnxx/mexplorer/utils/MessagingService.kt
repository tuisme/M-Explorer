package vinova.intern.nhomxnxx.mexplorer.utils

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage



class MessagingService : FirebaseMessagingService() {
	private lateinit var mBroadcaster : LocalBroadcastManager

	override fun onCreate() {
		super.onCreate()
		mBroadcaster = LocalBroadcastManager.getInstance(this)
	}

	override fun onMessageReceived(remoteMessage: RemoteMessage?) {
		super.onMessageReceived(remoteMessage)
		val intent = Intent("MyData")
		val z = remoteMessage?.notification?.body
		intent.putExtra("message",z)
		mBroadcaster.sendBroadcast(intent)
	}
}