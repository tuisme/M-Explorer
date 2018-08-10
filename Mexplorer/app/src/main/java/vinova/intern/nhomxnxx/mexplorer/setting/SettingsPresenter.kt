package vinova.intern.nhomxnxx.mexplorer.setting

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.widget.Toast
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vinova.intern.nhomxnxx.mexplorer.api.CallApiFaceAuth
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.utils.Support
import java.io.ByteArrayOutputStream
import java.io.File

class SettingsPresenter(view:SettingsInterface.View):SettingsInterface.Presenter {
    val mView: SettingsInterface.View = view
    private val api_key:String = "wWD5twUKYfBPNTZzdzQJoYL9BmSAKXcK"
    private val api_secret:String = "CdDgnwHSiiOXOGl7VdQ4mjm2Sykhca8B"
    init {
        mView.setPresenter(this)
    }

    @SuppressLint("CheckResult")
    override fun encryptFile(context: Context, data: Intent) {
        val extras = data.extras
        val imageBitmap = extras?.get("data") as Bitmap?
        if (imageBitmap == null) {
            mView.setSwitch(false)
            return
        }
        mView.showLoading(true)
        val bytes = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val ima = bytes.toByteArray()
        val db = DatabaseHandler(context)
        val requestFile = RequestBody.create(MediaType.parse("image/*"), ima)
        val body = MultipartBody.Part.createFormData("image_file","face", requestFile)
        CallApiFaceAuth.getInstance().getFace(api_key,api_secret, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when {
                        it.faces?.size ==0 -> {
                            mView.showLoading(false)
                            Toast.makeText(context, "Please capture image again", Toast.LENGTH_SHORT).show()
                            mView.setSwitch(false)
                        }
                        it.faces?.size!! > 1 -> {
                            mView.showLoading(false)
                            Toast.makeText(context, "Many face, please capture image again", Toast.LENGTH_SHORT).show()
                            mView.setSwitch(false)

                        }
                        else -> {
                            mView.showLoading(false)
                            Support.encrypt(Support.keyy, ima).let { Support.saveFile(it, "enimg.jpg") }
                            Toasty.success(context, "Face authentication active", Toast.LENGTH_SHORT).show()
                            mView.setSwitch(true)
                            val token = db.getToken()
                            db.updateMentAuth("Face",token)
                        }
                    }
                },
                        {
                            Toasty.error(context, "Error " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                        })    }

    @SuppressLint("CheckResult")
    override fun authentication(context: Context, data: Intent) {
        var faceId1: String
        val extras = data.extras
        val imageBitmap = extras?.get("data") as Bitmap?
        if (imageBitmap == null) {
            mView.setSwitch(true)
            return
        }
        mView.showLoading(true)
        val bytes = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
        val ima = bytes.toByteArray()

        val requestFile = RequestBody.create(MediaType.parse("image/*"), ima)
        val body = MultipartBody.Part.createFormData("image_file","face1", requestFile)
        CallApiFaceAuth.getInstance().getFace(api_key,api_secret, body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    when {
                        it.faces?.size ==0 -> {
                            mView.showLoading(false)
                            mView.setSwitch(true)
                            Toasty.error(context, "No detect face, please capture image again", Toast.LENGTH_SHORT).show()
                        }
                        it.faces?.size!! > 1 -> {
                            mView.showLoading(false)
                            mView.setSwitch(true)
                            Toasty.error(context,"Many face, please capture image again", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            faceId1 = it.faces!![0].faceToken.toString()
                            getFaceId2(context, faceId1)
                        }
                    }
                },{
                    Toasty.error(context, "Error " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                })
    }
    @SuppressLint("CheckResult")
    @TargetApi(Build.VERSION_CODES.O)
    private fun getFaceId2(context:Context, faceId1:String) {
        var faceId2: String
        val file = File(Environment.getExternalStorageDirectory().path +"/Temp/.auth/"+ "enimg.jpg")
        val encoded = Support.readFileToByteArray(file)
        val templates = Support.keyy.let { Support.decrypt(it, encoded) }
        val requestFile2 = RequestBody.create(MediaType.parse("image/*"), templates)
        val body2 = MultipartBody.Part.createFormData("image_file","face2", requestFile2)
        CallApiFaceAuth.getInstance().getFace(api_key,api_secret, body2)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe({
                    faceId2 = it.faces!![0].faceToken.toString()
                    compare(context, faceId1,faceId2)
                },
                        {
                            mView.setSwitch(true)
                            Toasty.error(context, "Error " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                        })
    }

    @SuppressLint("CheckResult")
    private fun compare(context:Context, faceId1:String, faceId2:String) {
        val db = DatabaseHandler(context)
        CallApiFaceAuth.getInstance().compare(api_key,api_secret,faceId1,faceId2)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (it.confidence!! > 85) {
                        mView.showLoading(false)
                        Toasty.success(context, "OK", Toast.LENGTH_SHORT).show()
                        val token = db.getToken()
                        db.updateMentAuth(null, token)
                        mView.setSwitch(false)

                    } else {
                        mView.showLoading(false)
                        mView.setSwitch(true)
                        Toasty.error(context, "Not match, please check again", Toast.LENGTH_SHORT).show()
                    }
                },
                        {
                            Toasty.error(context, "Error " + it.localizedMessage, Toast.LENGTH_SHORT).show()
                        })
    }

}

