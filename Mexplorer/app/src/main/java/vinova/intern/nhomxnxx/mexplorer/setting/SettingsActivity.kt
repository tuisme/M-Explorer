package vinova.intern.nhomxnxx.mexplorer.setting

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_settings.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.dialogs.PasswordDialog
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment
import vinova.intern.nhomxnxx.mexplorer.utils.NetworkUtils
import vinova.intern.nhomxnxx.mexplorer.utils.Support
import java.io.File


class SettingsActivity :AppCompatActivity(), SettingsInterface.View, PasswordDialog.DialogListener{
    override fun forceLogOut(message: String) {

    }

    override fun showLoading(isShow: Boolean) {
        if(isShow) CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
        else CustomDiaglogFragment.hideLoadingDialog()
    }

    override fun showError(message: String) {
        CustomDiaglogFragment.hideLoadingDialog()
        Toasty.error(this,message,Toast.LENGTH_SHORT).show()
    }

    override fun updateUser() {
    }

    override fun isAuth(isAuth: Boolean) {
    }

    private var mPresenter :SettingsInterface.Presenter= SettingsPresenter(this)

    val CAPTURE_IMAGE_REQUEST = 20
    val CAPTURE_IMAGE_REQUEST_2 = 22
    val db = DatabaseHandler(this)
    lateinit var userToken: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        userToken = db.getToken()!!

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setSwitchAuth()
        }

        tv_send_feedback.setOnClickListener {
            sendFeedback(this)
        }

    }

    override fun savePass(pass: ByteArray) {
        Support.encrypt(Support.keyy, pass).let { Support.saveFile(it, "code.txt") }
        db.updateMentAuth("Pattern",userToken)
        Toasty.success(this,"Success",Toast.LENGTH_SHORT).show()
        sw_auth.isChecked = true
    }

    override fun turnOff() {
        db.updateMentAuth(null,userToken)
        Toasty.success(this,"Success",Toast.LENGTH_SHORT).show()    }


    override fun setSwitch(isChecked: Boolean) {
        sw_auth.isChecked = isChecked
    }
    override fun setPresenter(presenter: SettingsInterface.Presenter) {
        this.mPresenter = presenter
    }




    @RequiresApi(Build.VERSION_CODES.M)
    private fun setSwitchAuth(){
        sw_auth.isChecked = db.getMentAuth()!=null

        sw_auth.setOnClickListener {
            when(db.getMentAuth()) {
                null -> chooseAuthMethod()
                "Face" -> captureImage(CAPTURE_IMAGE_REQUEST_2)
                "Pattern" -> {
                    val file = File(Environment.getExternalStorageDirectory().path +"/Temp/.auth/"+ "code.txt")
                    val encoded = Support.readFileToByteArray(file)
                    val templates = Support.keyy.let { Support.decrypt(it, encoded) }
                    val pass = templates.toString(Charsets.UTF_8)
                    PasswordDialog.newInstance(3, pass,true).show(supportFragmentManager,"fragment")

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            CAPTURE_IMAGE_REQUEST -> {
                if (data != null) {
                    setSwitch(false)
                    if (!NetworkUtils.isConnectedInternet(this)){
                        showError(NetworkUtils.messageNetWork)
                        return
                    }
                    mPresenter.encryptFile(this,data)

                }
            }
            CAPTURE_IMAGE_REQUEST_2 -> {
                if (data != null) {
                    setSwitch(false)
                    if (!NetworkUtils.isConnectedInternet(this)){
                        showError(NetworkUtils.messageNetWork)
                        return
                    }
                    mPresenter.authentication(this, data)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            2222-> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
                } else {
                    sw_auth.isChecked = !sw_auth.isChecked
                    Toasty.warning(this, "Permission Denied, Please allow to proceed !", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun chooseAuthMethod(){
        val auth = arrayOf<CharSequence>("Face", "Pattern")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose the method:")
                .setCancelable(false)
                .setNegativeButton("Cancel"){_, _ -> 	sw_auth.isChecked = db.getMentAuth()!=null
                }
                .setItems(auth) { _, which ->
                    when(which){
                        0->	captureImage(CAPTURE_IMAGE_REQUEST)

                        1-> {
                            PasswordDialog.newInstance(1, null).show(supportFragmentManager,"fragment")
                        }
                    }
                }
        builder.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun captureImage(code:Int) {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),2222)
        }
        else {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, code)
        }
    }


    fun sendFeedback(context: Context) {
        var body: String? = null
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName
            body = "\n\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER
        } catch (e: PackageManager.NameNotFoundException) {
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("uongsyphuong@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query from android app")
        intent.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)))
    }

}