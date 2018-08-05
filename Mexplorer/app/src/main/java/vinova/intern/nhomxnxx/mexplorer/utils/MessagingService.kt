package vinova.intern.nhomxnxx.mexplorer.utils

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {
	override fun onMessageReceived(p0: RemoteMessage?) {
		super.onMessageReceived(p0)
		Log.d(TAG, "From: " + p0?.from)

		// Check if message contains a data payload.
		if (p0?.data?.isNotEmpty()!!) {
			Log.d(TAG, "Message data payload: " + p0.data)


		}

	}
}