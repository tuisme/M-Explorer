package vinova.intern.nhomxnxx.mexplorer.cloud

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.facebook.login.LoginManager
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import es.dmoral.toasty.Toasty
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.*
import vinova.intern.nhomxnxx.mexplorer.service.NotificationService
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment
import vinova.intern.nhomxnxx.mexplorer.utils.FileUtils
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CloudPresenter(view : CloudInterface.View,context: Context):CloudInterface.Presenter {
	val mView = view
	val ctx = context
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
		try {
			if (url.contains(".doc") || url.contains(".docx")) {
				// Word document
				intent.setDataAndType(uri, "application/msword")
			} else if (url.contains(".pdf")) {
				// PDF file
				intent.setDataAndType(uri, "application/pdf")
			} else if (url.contains(".ppt") || url.contains(".pptx")) {
				// Powerpoint file
				intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
			} else if (url.contains(".xls") || url.contains(".xlsx")) {
				// Excel file
				intent.setDataAndType(uri, "application/vnd.ms-excel")
			} else if (url.contains(".zip") || url.contains(".rar")) {
				// WAV audio file
				intent.setDataAndType(uri, "application/x-wav")
			} else if (url.contains(".rtf")) {
				// RTF file
				intent.setDataAndType(uri, "application/rtf")
			} else if (url.contains(".wav") || url.contains(".mp3")) {
				// WAV audio file
				intent.setDataAndType(uri, "audio/x-wav")
			} else if (url.contains(".gif")) {
				// GIF file
				intent.setDataAndType(uri, "image/gif")
			} else if (url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png")) {
				// JPG file
				intent.setDataAndType(uri, "image/jpeg")
			} else if (url.contains(".txt")) {
				// Text file
				intent.setDataAndType(uri, "text/plain")
			} else if (url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") || url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi")) {
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
		catch (e: ActivityNotFoundException){
			Toasty.info(context,"No support this file", Toast.LENGTH_SHORT).show()
		}
	}

	override fun download(id: String, ctoken: String, user_token: String, ctype: String) {
		CallApi.getInstance().getUrlFile(id, ctoken, user_token,ctype)
				.enqueue(object : Callback<SpecificFile>{
					override fun onFailure(call: Call<SpecificFile>?, t: Throwable?) {
						mView.showError(t.toString())
					}
					override fun onResponse(call: Call<SpecificFile>?, response: Response<SpecificFile>?) {
						if (response?.body()?.status.equals("success")) {
							val url = response?.body()?.data?.url
							val name = response?.body()?.data?.name
							mView.downloadFile(url.toString(),name.toString(),ctype)
						}
						else
							mView.showError("Download error")
					}

				})
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

	override fun upLoadFile(user_token: String, id: String, uri: Uri, ctype: String, ctoken: String) {
		val file = FileUtils.getFile(ctx,uri) ?: return mView.showError("Please choose another file")

		val requestBody = RequestBody.create(
				MediaType.parse(ctx.contentResolver.getType(uri)),
				file)
		val body = MultipartBody.Part.createFormData("file", file.name, requestBody)

		CallApi.getInstance().uploadFile(user_token, id, body, ctype, ctoken)
				.enqueue(object : Callback<BaseResponse>{
					override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
						mView.showError(t.toString())
						Log.e("ABCD",t.toString())
					}

					override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
						if (response?.body()?.status.equals("success"))
							mView.refresh()
						else {
							Log.e("ABCD",response?.errorBody()?.string()!!)
							mView.showError(response.errorBody()?.string()!!)
						}
					}
				})
	}

	override fun renameFile(user_token: String, id: String, fname: String, ctype: String, ctoken: String) {
		if (fname != "")
			CallApi.getInstance().renameFile(user_token, id, fname, ctype, ctoken)
					.enqueue(object : Callback<BaseResponse>{
						override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
							mView.showError(t.toString())
						}

						override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
							if (response?.body()?.status.equals("success"))
								mView.refresh()
							else
								mView.showError(response?.errorBody()?.string()!!)
						}

					})
		else mView.showError("Please fill new name")

	}

	override fun renameFolder(userToken: String, id: String, newName: String, cloudType: String, ctoken: String) {
		if (newName != "")
			CallApi.getInstance().renameFile(userToken, id, newName, cloudType, ctoken)
					.enqueue(object : Callback<BaseResponse>{
						override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
							mView.showError(t.toString())
						}

						override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
							if (response?.body()?.status.equals("success"))
								mView.refresh()
							else
								mView.showError(response?.errorBody()?.string()!!)
						}

					})
		else mView.showError("Please fill new name")
	}

	override fun createFolder(user_token: String, fname: String, parent: String, ctype: String, ctoken: String) {
		CallApi.getInstance().createFolder(user_token, fname, parent, ctype, ctoken)
				.enqueue(object : Callback<requestUploadFolder>{
					override fun onFailure(call: Call<requestUploadFolder>?, t: Throwable?) {
						mView.showError(t.toString())
					}

					override fun onResponse(call: Call<requestUploadFolder>?, response: Response<requestUploadFolder>?) {
						if(response?.body()!=null){
							mView.refresh()
						}
						else
							mView.showError(response?.message()!!)
					}
				})
	}

	lateinit var folderRoot : String
	fun listAllFilesAndFilesSubDirectory(path : String):JsonArray{
		val directory = File(path)
		val array = JsonArray()
		val parent = JsonObject()
		parent.addProperty("parent",directory.name)
		array.add(parent)
		for (file : File in directory.listFiles()){
			when(file.isFile){
				true -> {
					val fileObj  = JsonObject()
					fileObj.addProperty("name",file.name)
					fileObj.addProperty("path",file.path.substringAfter("$folderRoot/"))
					array.add(fileObj)
				}
				false-> {
					val fileObj  = JsonObject()
					fileObj.add(file.name,listAllFilesAndFilesSubDirectory(file.path))
					array.add(listAllFilesAndFilesSubDirectory(file.path))
				}
			}
		}
		return array
	}

	override fun upLoadFolder(user_token: String, ctoken: String, ctype: String, id: String, path : String) {
		val root = File(Environment.getExternalStorageDirectory().absolutePath+"/$path")
		folderRoot = root.parent
		val zipPath = Environment.getExternalStorageDirectory().absolutePath+"/$path.zip"

		zip(root,zipPath)
		val file = File(zipPath)

		val map = JsonObject()
		map.add("root",listAllFilesAndFilesSubDirectory(root.path))

		val extension = MimeTypeMap.getFileExtensionFromUrl(file.path.replace(" ",""))
		val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
		val requestBody = RequestBody.create(
				MediaType.parse(type),
				file)
		val body = MultipartBody.Part.createFormData("zip", file.name, requestBody)

		CallApi.getInstance().uploadFolder(user_token,id,map.toString(),body,ctype,ctoken)
				.enqueue(object  : Callback<BaseResponse>{
					override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
						mView.showError(t.toString())
					}

					override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
						deleteFile(file)
						mView.refresh()
					}
				})
	}

	private fun zip(directory : File,toLocation: String){
		val base = directory.toURI()
		val deque : Deque<File> = LinkedList<File>()
		deque.push(directory)
		val fos = FileOutputStream(toLocation)
		var res : Closeable = fos
		val buffer = ByteArray(1024)
		val zos = ZipOutputStream(fos)
		try{
			res = zos
			while (!deque.isEmpty()){
				val file : File = deque.pop()
				for (kid in file.listFiles()){
					var name = base.relativize(kid.toURI()).path
					if (kid.isDirectory){
						deque.push(kid)
						name = if (name.endsWith("/")) name else "$name/"
						zos.putNextEntry(ZipEntry(name))
					}
					else{
						zos.putNextEntry(ZipEntry(name))
						val fis = FileInputStream(kid)
						var count = fis.read(buffer)
						while (count > 0){
							zos.write(buffer,0,count)
							count = fis.read(buffer)
						}
						fis.close()
					}
				}
			}
		}
		catch (e : Exception){

		}
		finally {
			zos.closeEntry()
			res.close()

		}
	}

	private fun deleteFile(file : File){
		file.delete()
		if (file.exists()){
			file.canonicalFile.delete()
			if (file.exists()){
				ctx.deleteFile(file.name)
			}
		}
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

	override fun deleteFile(user_token: String, id: String, ctype: String, ctoken: String) {
			CallApi.getInstance().deleteFile(user_token, id, ctype, ctoken)
					.enqueue(object : Callback<BaseResponse>{
						override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
							mView.showError(t.toString())
						}

						override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
							if (response?.body()?.status.equals("success"))
								mView.refresh()
							else
								mView.showError(response?.errorBody()?.string()!!)
						}

					})
	}

	override fun deleteFolder(userToken: String, id: String, cloudType: String, ctoken: String) {
		CallApi.getInstance().deleteFolder(userToken, id, cloudType, ctoken)
				.enqueue(object : Callback<BaseResponse>{
					override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
						mView.showError(t.toString())
					}

					override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
						if (response?.body()?.status.equals("success"))
							mView.refresh()
						else
							mView.showError(response?.errorBody()?.string()!!)
					}

				})	}

	override fun saveImage(data: Intent?, user_token: String, id: String, ctype: String, ctoken: String) {
		val extras = data?.extras
		val imageBitmap = extras?.get("data") as Bitmap
		val bytes = ByteArrayOutputStream()
		imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
		val file = createImageFile()
		try {
			file.createNewFile()
			val fo = FileOutputStream(file)
			fo.write(bytes.toByteArray())
			fo.close()
			uploadImage(user_token,id,file,ctype,ctoken)
			mView.refresh()
		}catch (e: IOException){
			e.printStackTrace()
		}
	}

	@SuppressLint("SimpleDateFormat")
	@Throws(IOException::class)
	private fun createImageFile(): File {
		// Create an image file name
		val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
		val imageFileName = "IMG_" + timeStamp + "_"

		// Save a file: path for use with ACTION_VIEW intents
		return File.createTempFile(
				imageFileName, /* prefix */
				".png", /* suffix */
				File(Environment.getExternalStorageDirectory().path + File.separator + "Temp")      /* directory */
		)
	}


	fun uploadImage(user_token: String, id: String, file: File, ctype: String, ctoken: String){
		val requestBody = RequestBody.create(
				MediaType.parse("file/*"),
				file)
		val body = MultipartBody.Part.createFormData("file", file.name, requestBody)

		CallApi.getInstance().uploadFile(user_token, id, body, ctype, ctoken)
				.enqueue(object : Callback<BaseResponse>{
					override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
						mView.showError(t.toString())
					}

					override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
						if (response?.body()?.status.equals("success"))
							mView.refresh()
						else {
							response?.errorBody()?.string()?.let { mView.showError(it) }
						}
					}
				})
	}

	override fun moveOrCopy(idItem: String, mCopy: Boolean, user_token: String, cloudType: String, ctoken: String, idDest: String, isDic:Boolean) {
		val mType: String = if (isDic) "folder"
		else "file"
		if (mCopy){
			CallApi.getInstance().copyFile(user_token,idItem,cloudType,ctoken,idDest,mType)
					.enqueue(object:Callback<BaseResponse>{
						override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
							mView.showError(t.toString())
						}

						override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
							if (response?.body()?.status.equals("success")) {
								CustomDiaglogFragment.hideLoadingDialog()
								mView.refresh()
							}
							else {
								response?.errorBody()?.string()?.let { mView.showError(it) }
							}
						}
					})
		}

		else {
			CallApi.getInstance().moveFile(user_token,idItem,cloudType,ctoken,idDest,mType)
					.enqueue(object:Callback<BaseResponse>{
						override fun onFailure(call: Call<BaseResponse>?, t: Throwable?) {
							mView.showError(t.toString())
						}

						override fun onResponse(call: Call<BaseResponse>?, response: Response<BaseResponse>?) {
							if (response?.body()?.status.equals("success")){
								CustomDiaglogFragment.hideLoadingDialog()
								mView.refresh()
							}
							else {
								response?.errorBody()?.string()?.let { mView.showError(it) }
							}
						}
					})
		}
	}


}