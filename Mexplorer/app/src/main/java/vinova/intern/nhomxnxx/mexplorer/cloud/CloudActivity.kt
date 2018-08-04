package vinova.intern.nhomxnxx.mexplorer.cloud

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home_layout.*
import kotlinx.android.synthetic.main.nav_bar_header.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.adapter.CloudAdapter
import vinova.intern.nhomxnxx.mexplorer.baseInterface.BaseActivity
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.device.DeviceActivity
import vinova.intern.nhomxnxx.mexplorer.dialogs.*
import vinova.intern.nhomxnxx.mexplorer.home.HomeActivity
import vinova.intern.nhomxnxx.mexplorer.log_in_out.LogActivity
import vinova.intern.nhomxnxx.mexplorer.model.FileDetail
import vinova.intern.nhomxnxx.mexplorer.model.FileSec
import vinova.intern.nhomxnxx.mexplorer.service.DownloadService
import vinova.intern.nhomxnxx.mexplorer.utils.CustomDiaglogFragment
import java.io.File


class CloudActivity : BaseActivity(),CloudInterface.View, UpdateItemDialog.DialogListener, UploadFileDialog.DialogListener,
		RenameDialog.DialogListener, ConfirmDeleteDialog.ConfirmListener, NewFolderDialog.DialogListener {

	private lateinit var adapter : CloudAdapter
	var mPresenter : CloudInterface.Presenter = CloudPresenter(this,this)
	lateinit var ctoken : String
	lateinit var userToken : String
	lateinit var cloudType : String
	lateinit var cloudId : String
	val PICKFILE_REQUEST_CODE = 1997
	val READ_REQUEST_CODE = 2511
	lateinit var url_ :String
	lateinit var name_ :String
	lateinit var ctype_ :String
	var path : ArrayList<ArrayList<String>> = arrayListOf()
	val CAPTURE_IMAGE_REQUEST = 20
	lateinit var folder : File
	var firstLoadUser = true
    lateinit var idItem: String
    var mCopy:Boolean =false
    var isDic:Boolean = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		super.onCreateDrawer()
		setRv()
		userToken = DatabaseHandler(this).getToken()!!
		accept_move.setOnClickListener {
			moving_layout.visibility = View.GONE
            CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
			mPresenter.moveOrCopy(idItem,mCopy,userToken,cloudType,ctoken,path.last()[0],isDic)
		}

		decline_move.setOnClickListener {
			moving_layout.visibility = View.GONE
			idItem = ""
		}
	}

	private fun setRv(){
		adapter = CloudAdapter(this,error_nothing,bottom_sheet_detail,supportFragmentManager)
		rvContent.layoutManager = LinearLayoutManager(this)
		rvContent.adapter = adapter
		rvContent.addItemDecoration(DividerItemDecoration(rvContent.context, DividerItemDecoration.VERTICAL))
		rvContent.showShimmerAdapter()

		ctoken = intent.getStringExtra("token")
		cloudType = intent.getStringExtra("type")
		cloudId = intent.getStringExtra("id")
		path.add(arrayListOf(cloudId,intent.getStringExtra("name")))
		title = path.last()[1]

		mPresenter.getList(cloudId,ctoken,DatabaseHandler(this).getToken()!!,cloudType)

		adapter.setListener(object : CloudAdapter.ItemClickListener{
			override fun onClick(file: FileSec) {
				if (file.mime_type!!.contains("folder")) {
					mPresenter.getList(file.id!!, ctoken, DatabaseHandler(this@CloudActivity).getToken()!!, cloudType)
					rvContent.showShimmerAdapter()
					path.add(arrayListOf(file.id!!,file.name!!))
				}
				else{
					CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
					mPresenter.getUrlFile(file.id!!,ctoken,userToken,cloudType)
				}
			}

			override fun onLongClick(file: FileSec) {
				UpdateItemDialog.newInstanceCloud(file,cloudType).show(supportFragmentManager, "update_item")
			}
		})

		fab_add.setOnClickListener {
			UploadFileDialog.getInstance().show(supportFragmentManager,"upload file")
		}

		swipeContent.setOnRefreshListener {
			swipeContent.isRefreshing = false
			rvContent.showShimmerAdapter()
			mPresenter.getList(path.last()[0],ctoken,userToken,cloudType)
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		when(requestCode) {
			PICKFILE_REQUEST_CODE -> {
				if (data != null) {
					val uri: Uri = data.data
					mPresenter.upLoadFile(userToken, path.last()[0], uri, cloudType, ctoken)
				}
			}
			READ_REQUEST_CODE -> {
				if (data!=null) {
					val uri : Uri = data.data
					folder = File(uri.path)
					mPresenter.upLoadFolder(userToken,ctoken,cloudType,path.last()[0],folder.path.split(":").last())
				}
			}

			CAPTURE_IMAGE_REQUEST -> {
				if (data != null) {
					mPresenter.saveImage(data, userToken, path.last()[0], cloudType, ctoken)
				}
			}
		}
	}

	// for floating button
	@SuppressLint("ObsoleteSdkInt")
	@TargetApi(Build.VERSION_CODES.M)
	override fun onOptionClick(type: String) {
		when(type){
			"create folder"->{
				NewFolderDialog.newInstance().show(supportFragmentManager,"fragment")
			}
			"upload file" ->{
				val intent = Intent(Intent.ACTION_GET_CONTENT)
				intent.type = "*/*"
				startActivityForResult(Intent.createChooser(intent,"select a file to upload"),PICKFILE_REQUEST_CODE)
			}
			"upload folder" -> {
				val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
				startActivityForResult(intent, READ_REQUEST_CODE)
			}
			"upload image" -> {

					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						captureImage()
					}
			}
		}
	}

	// for on long click
	override fun onOptionClick(which: Int, path: String?) {
		when(which){
			R.id.offline ->{
                CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
				path?.let { mPresenter.download(it, ctoken,userToken,cloudType) }
			}
			R.id.delete -> {
				val name = path?.split("/")!!
				ConfirmDeleteDialog.newInstanceCloud( name[2],name[1].toBoolean(),name[0]).show(supportFragmentManager,"fragment")
			}
			R.id.rename -> {
				val name = path?.split("/")!!
				RenameDialog.newInstanceCloud(name[2],name[0],name[1].toBoolean(),ctoken).show(supportFragmentManager,"fragment")
			}
			R.id.copy ->{
				idItem = path.toString()
				moving_layout.visibility = View.VISIBLE
				val temp = path?.split("/")
				if(temp != null)
					idItem= temp[0]
				isDic = temp?.get(1)?.toBoolean()!!
                moving_file_name.text = getString(R.string.copy_file,temp[2])
                mCopy = true
			}
            R.id.move ->{
                moving_layout.visibility = View.VISIBLE
                val temp = path?.split("/")
                if(temp != null)
                    idItem= temp[0]
                isDic = temp?.get(1)?.toBoolean()!!
                moving_file_name.text = getString(R.string.moving_file,temp[2])
                mCopy  = false
            }
		}
	}

	override fun onNewFolder(name: String) {
		mPresenter.createFolder(userToken,name,path.last()[0],cloudType,ctoken)
	}

    override fun onReNameCloud(newName: String, id: String, isDic: Boolean, token: String) {
        if(isDic)
            mPresenter.renameFolder(userToken,id,newName,cloudType,ctoken)
        else
            mPresenter.renameFile(userToken,id,newName,cloudType,ctoken)
    }

	override fun onRename(fromPath: String, toPath: String) {

	}

	override fun showList(files: List<FileSec>) {
		title = path.last()[1]
		swipeContent.isRefreshing = false
		rvContent.hideShimmerAdapter()
		adapter.setData(files)
		adapter.notifyDataSetChanged()
		if (firstLoadUser)
			showUser()
	}

	private fun showUser() {
		val user = DatabaseHandler(this).getUser()
		val name = "${user.firstName} ${user.lastName}"
		user_name.text = name
		user_email.text = user.email
		user_have_percentage.text = user.used
		progressBar.progress = (user.used?.toFloat()?.times(100))?.toInt() ?: 0
		Glide.with(this)
				.load(user.avatarUrl)
				.into(img_profile)
	}

	override fun showFile(file: FileDetail) {
		CustomDiaglogFragment.hideLoadingDialog()
		mPresenter.openFile(this@CloudActivity,file.url!!)
	}

	override fun setPresenter(presenter: CloudInterface.Presenter) {
		this.mPresenter = presenter
	}

	override fun showLoading(isShow: Boolean) {
	}

	override fun showError(message: String) {
		CustomDiaglogFragment.hideLoadingDialog()
		rvContent.hideShimmerAdapter()
		Toasty.error(this,message,Toast.LENGTH_SHORT).show()
	}

	override fun logoutSuccess() {
		CustomDiaglogFragment.hideLoadingDialog()
		startActivity(Intent(this, LogActivity::class.java))
		finish()
	}

	override fun onNavigationItemSelected(p0: MenuItem): Boolean {
		when(p0.itemId){
			R.id.home->{
				val intent = Intent(this,HomeActivity::class.java)
				startActivity(intent)
			}
			R.id.signout->{
				CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
				mPresenter.logout(this, DatabaseHandler(this).getToken())
			}
			R.id.bookmark->{

			}
			R.id.device_connected -> {
				val intent = Intent(this, DeviceActivity::class.java)
				startActivity(intent)
			}
		}
		drawer_layout?.closeDrawer(GravityCompat.START)
		return true
	}

	override fun onBackPressed() {
		when {
			drawer_layout?.isDrawerOpen(GravityCompat.END)!! -> drawer_layout?.isDrawerOpen(GravityCompat.START)
			path.size > 1 -> {
                if (adapter.error.visibility == View.VISIBLE)
                    adapter.error.visibility = View.GONE
				rvContent.showShimmerAdapter()
				path.removeAt(path.size-1)
				val id = path.last()[0]
				mPresenter.getList(id,ctoken,userToken,cloudType)
			}
			else -> super.onBackPressed()
		}
	}

	override fun refresh() {
		CustomDiaglogFragment.hideLoadingDialog()
		mPresenter.getList(path.last()[0],ctoken,userToken,cloudType)
	}

	override fun downloadFile(url: String, name: String, ctype: String) {
		url_ = url
		name_ = name
		ctype_ = ctype

		if (checkPermission()) {
			startDownload(url,name,ctype)
		} else {
			requestPermission()
		}
	}

	private fun startDownload(url:String,name:String,ctype:String) {
		val intent = Intent(this, DownloadService::class.java)
		intent.putExtra("url",url)
		intent.putExtra("name",name)
		intent.putExtra("ctype",ctype)
        CustomDiaglogFragment.hideLoadingDialog()
		startService(intent)
	}

	private fun checkPermission(): Boolean {
		val result = ContextCompat.checkSelfPermission(this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE)
		return result == PackageManager.PERMISSION_GRANTED
	}

	private fun requestPermission() {

		ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)

	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		when (requestCode) {
			2 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				startDownload(url_,name_,ctype_)
			} else {
				Toasty.warning(this, "Permission Denied, Please allow to proceed !", Toast.LENGTH_LONG).show()

			}
		}
	}
	override fun onConfirmDelete(path: String?) {
	}

	override fun onConfirmDeleteCloud(name: String, isDic: Boolean, id: String) {
		CustomDiaglogFragment.showLoadingDialog(supportFragmentManager)
        if(isDic)
            mPresenter.deleteFolder(userToken,id,cloudType,ctoken)
        else
		    mPresenter.deleteFile(userToken,id,cloudType,ctoken)
	}




	@RequiresApi(Build.VERSION_CODES.M)
	private fun captureImage() {
		if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(arrayOf(Manifest.permission.CAMERA),0)
		}
		else {
			val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
			startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
		}
	}
}