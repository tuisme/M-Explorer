package vinova.intern.nhomxnxx.mexplorer.cloud

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.facebook.login.LoginManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.Request
import vinova.intern.nhomxnxx.mexplorer.model.SpecificCloud
import vinova.intern.nhomxnxx.mexplorer.model.SpecificFile

class CloudPresenter(view : CloudInterface.View):CloudInterface.Presenter {
	val mView = view
	init {
		mView.setPresenter(this)
	}
	override fun getList(id:String,token:String,userToken:String,type : String) {
		CallApi.getInstance().gotoCloud(id,token,userToken,type)
				.enqueue(object : Callback<SpecificCloud>{
					override fun onFailure(call: Call<SpecificCloud>?, t: Throwable?) {
						mView.showError(t.toString())
					}

					override fun onResponse(call: Call<SpecificCloud>?, response: Response<SpecificCloud>?) {
						if (response?.body() != null)
							mView.showList(response.body()?.data!!)
						else
							mView.showError(response?.message()!!)
					}

				})
	}

	override fun openFile(context: Context, url: String){
		val intent = Intent(Intent.ACTION_VIEW)
		val uri: Uri = Uri.parse(url)
		if (url.contains(".doc") || url.contains(".docx")) {
			// Word document
			intent.setDataAndType(uri, "application/msword")
		} else if(url.contains(".pdf")) {
			// PDF file
			intent.setDataAndType(uri, "application/pdf")
		} else if(url.contains(".ppt") || url.contains(".pptx")) {
			// Powerpoint file
			intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
		} else if(url.contains(".xls") || url.contains(".xlsx")) {
			// Excel file
			intent.setDataAndType(uri, "application/vnd.ms-excel")
		} else if(url.contains(".zip") || url.contains(".rar")) {
			// WAV audio file
			intent.setDataAndType(uri, "application/x-wav")
		} else if(url.contains(".rtf")) {
			// RTF file
			intent.setDataAndType(uri, "application/rtf")
		} else if(url.contains(".wav") || url.contains(".mp3")) {
			// WAV audio file
			intent.setDataAndType(uri, "audio/x-wav")
		} else if(url.contains(".gif")) {
			// GIF file
			intent.setDataAndType(uri, "image/gif")
		} else if(url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
			// JPG file
			intent.setDataAndType(uri, "image/jpeg")
		} else if(url.contains(".txt")) {
			// Text file
			intent.setDataAndType(uri, "text/plain")
		} else if(url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
			// Video files
			intent.setDataAndType(uri, "video/*")
		} else {
			//if you want you can also define the intent type for any other file

			//additionally use else clause below, to manage other unknown extensions
			//in this case, Android will show all applications installed on the device
			//so you can choose which application to use
			intent.setDataAndType(uri, "*/*")
		}
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		context.startActivity(intent)
	}

	override fun getUrlFile(id: String, ctoken: String, user_token: String,ctype:String) {
		CallApi.getInstance().getUrlFile(id, ctoken, user_token,ctype)
				.enqueue(object : Callback<SpecificFile>{
					override fun onFailure(call: Call<SpecificFile>?, t: Throwable?) {
						mView.showError(t.toString())
					}

					override fun onResponse(call: Call<SpecificFile>?, response: Response<SpecificFile>?) {
						if (response?.body()?.status.equals("success"))
							mView.showFile(response?.body()?.data!!)
						else
							mView.showError("something wrong just happened")
					}

				})
	}

	override fun logout(context: Context?, token: String?) {
		val db = DatabaseHandler(context)
		if (token!=null)
			CallApi.getInstance().logout(token)
					.enqueue(object : Callback<Request> {
						override fun onFailure(call: Call<Request>?, t: Throwable?) {
							mView.showError(t.toString())
						}

						override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
							if (response?.body()?.status.equals("success")) {
								LoginManager.getInstance().logOut()

								db.deleteUserData(token)
							}
							else
								mView.showError(response?.message().toString())
						}
					})
	}
}