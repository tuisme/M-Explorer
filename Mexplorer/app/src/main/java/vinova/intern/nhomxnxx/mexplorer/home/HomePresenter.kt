package vinova.intern.nhomxnxx.mexplorer.home

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.facebook.login.LoginManager
import es.dmoral.toasty.Toasty
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vinova.intern.nhomxnxx.mexplorer.api.CallApi
import vinova.intern.nhomxnxx.mexplorer.api.CallApiFaceAuth
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.*
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment
import vinova.intern.nhomxnxx.mexplorer.utils.Support
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException

@Suppress("NAME_SHADOWING", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomePresenter(view:HomeInterface.View): HomeInterface.Presenter {
    val mView: HomeInterface.View = view
    val api_key:String = "wWD5twUKYfBPNTZzdzQJoYL9BmSAKXcK"
    val api_secret:String = "CdDgnwHSiiOXOGl7VdQ4mjm2Sykhca8B"
    init {
        mView.setPresenter(this)
    }

    override fun logout(context: Context?, token: String?) {
        val token = DatabaseHandler(context).getToken()
        val db = DatabaseHandler(context)
        if (token!=null)
            CallApi.getInstance().logout(token)
                    .enqueue(object : Callback<Request>{
                        override fun onFailure(call: Call<Request>?, t: Throwable?) {
                            mView.showError("can not sign out")
                        }
                        override fun onResponse(call: Call<Request>?, response: Response<Request>?) {
                            if (response?.body()?.status.equals("success")) {
	                            LoginManager.getInstance().logOut()
                                mView.showLoading(false)
                                mView.logoutSuccess()
                                db.deleteUserData(token)
                            }
                            else
                                mView.showError(response?.message().toString())
                        }
                    })
    }

    override fun getList(token: String?) {
        if (token != null)
            CallApi.getInstance().getListCloud(token).enqueue(object : Callback<ListCloud>{
                override fun onFailure(call: Call<ListCloud>?, t: Throwable?) {
                    mView.showError(t.toString())
                }

                override fun onResponse(call: Call<ListCloud>?, response: Response<ListCloud>?) {
                    if (response?.body()?.status.equals("success")){
                        mView.showList(response?.body())
                    }
                }
            })
    }

    override fun refreshList(token: String?) {
        if (token != null)
            CallApi.getInstance().getListCloud(token).enqueue(object : Callback<ListCloud>{
                override fun onFailure(call: Call<ListCloud>?, t: Throwable?) {
                    mView.showError(t.toString())
                }

                override fun onResponse(call: Call<ListCloud>?, response: Response<ListCloud>?) {
                    if (response?.body()?.status.equals("success")){
                        mView.refreshList(response?.body())
                    }
                }
            })
    }

    override fun renameCloud(id: String, newName: String,token:String,userToken:String) {
        CallApi.getInstance().changeNameCloud(userToken,id,newName)
                .enqueue(object : Callback<RequestChangeName>{
                    override fun onFailure(call: Call<RequestChangeName>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<RequestChangeName>?, response: Response<RequestChangeName>?) {
	                    if(response?.body() == null)
	                        mView.refresh()
	                    else
		                    mView.refresh()
                    }
                })
    }

    override fun deleteCloud(id: String, token: String) {
        CallApi.getInstance().deleteDrive(token,id)
                .enqueue(object : Callback<RequestChangeName>{
                    override fun onFailure(call: Call<RequestChangeName>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }

                    override fun onResponse(call: Call<RequestChangeName>?, response: Response<RequestChangeName>?) {
                        mView.refresh()
                    }
                })
    }

    override fun sendCode(code: String, name: String,userToken: String,provider:String) {
        CallApi.getInstance().getDrive(userToken,code,name,provider)
                .enqueue(object : Callback<RequestChangeName>{
                    override fun onFailure(call: Call<RequestChangeName>?, t: Throwable?) {
                        mView.showError(t.toString())
                    }
                    override fun onResponse(call: Call<RequestChangeName>?, response: Response<RequestChangeName>?) {
                        if(response?.body()!=null)
                            mView.refresh()
                        else
                            mView.showError(response?.message()!!)
                    }
                })
    }

    override fun encryptFile(context:Context, data: Intent) {
        val extras = data.extras
        val imageBitmap = extras?.get("data") as Bitmap ?: return
        mView.showLoading(true)
        val bytes = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val ima = bytes.toByteArray()
        val db = DatabaseHandler(context)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), ima)
        val body = MultipartBody.Part.createFormData("image_file","face", requestFile)
        CallApiFaceAuth.getInstance().getFace(api_key,api_secret, body)
                .enqueue(object : Callback<AuthenticationFace> {
                    override fun onFailure(call: Call<AuthenticationFace>?, t: Throwable?) {
                    }
                    override fun onResponse(call: Call<AuthenticationFace>?, response: Response<AuthenticationFace>?) {
                        val authFace = response?.body()
                        when {
                            authFace?.faces?.size ==0 -> {
                                mView.showLoading(false)
                                Toast.makeText(context, "Please capture image again", Toast.LENGTH_SHORT).show()
                                mView.setSwitch(false)
                            }
                            authFace?.faces?.size!! > 1 -> {
                                mView.showLoading(false)
                                Toast.makeText(context, "Many face, please capture image again", Toast.LENGTH_SHORT).show()
                                mView.setSwitch(false)

                            }
                            else -> {
                                mView.showLoading(false)
                                Support.encrypt(Support.keyy, ima).let { Support.saveFile(it, "enimg.jpg") }
                                Toasty.success(context, "Face authentication active", Toast.LENGTH_SHORT).show()
                                mView.isAuth(false)
                                db.updateFaceAuth(1, db.getToken())
                            }
                        }
                    }
                })    }

    override fun authentication(context:Context,data: Intent, isTurnOff:Boolean) {
        var faceId1: String
        val extras = data.extras
        val imageBitmap = extras?.get("data") as Bitmap? ?: return
        mView.showLoading(true)
        val bytes = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
        val ima = bytes.toByteArray()

        val requestFile = RequestBody.create(MediaType.parse("image/*"), ima)
        val body = MultipartBody.Part.createFormData("image_file","face1", requestFile)
        CallApiFaceAuth.getInstance().getFace(api_key,api_secret, body)
                .enqueue(object : Callback<AuthenticationFace>{
                    override fun onFailure(call: Call<AuthenticationFace>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<AuthenticationFace>?, response: Response<AuthenticationFace>?) {
                        val authFace = response?.body()
                        when {
                            authFace?.faces?.size ==0 -> {
                                mView.showLoading(false)
                                Toasty.error(context, "No detect face, please capture image again", Toast.LENGTH_SHORT).show()
                            }
                            authFace?.faces?.size!! > 1 -> {
                                mView.showLoading(false)
                                Toasty.error(context,"Many face, please capture image again", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                faceId1 = authFace.faces!![0].faceToken.toString()
                                getFaceId2(context, faceId1,isTurnOff)
                            }
                        }
                    }
                })
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun getFaceId2(context:Context, faceId1:String,isTurnOff:Boolean) {
        var faceId2: String
        val file = File(Environment.getExternalStorageDirectory().path +"/Temp/.auth/"+ "enimg.jpg")
        val encoded = Support.readFileToByteArray(file)
        val templates = Support.keyy.let { Support.decrypt(it, encoded) }
        val requestFile2 = RequestBody.create(MediaType.parse("image/*"), templates)
        val body2 = MultipartBody.Part.createFormData("image_file","face2", requestFile2)
        CallApiFaceAuth.getInstance().getFace(api_key,api_secret, body2)
                .enqueue(object : Callback<AuthenticationFace> {
                    override fun onFailure(call: Call<AuthenticationFace>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<AuthenticationFace>?, response: Response<AuthenticationFace>?) {
                        val authFace = response?.body()
                        faceId2 = authFace?.faces!![0].faceToken.toString()
                        compare(context, faceId1,faceId2,isTurnOff)
                    }
                })
    }

    private fun compare(context:Context, faceId1:String,faceId2:String,isTurnOff:Boolean) {
        val db = DatabaseHandler(context)
        CallApiFaceAuth.getInstance().compare(api_key,api_secret,faceId1,faceId2)
                .enqueue(object :Callback<Compare>{
                    override fun onFailure(call: Call<Compare>?, t: Throwable?) {
                    }

                    override fun onResponse(call: Call<Compare>?, response: Response<Compare>?) {
                        val authFace = response?.body()
                        if(authFace?.confidence!! >80) {
                            mView.showLoading(false)
                            if (isTurnOff) {
                                Toasty.success(context, "OK", Toast.LENGTH_SHORT).show()
                                db.updateFaceAuth(0, db.getToken())
                                mView.setSwitch(false)
                            }
                            else mView.isAuth(true)
                        }
                        else {
                            mView.showLoading(false)
                            Toasty.error(context, "Not match, please check again", Toast.LENGTH_SHORT).show()
                        }
                    }

                })    }




}