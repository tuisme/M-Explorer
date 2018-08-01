package vinova.intern.nhomxnxx.mexplorer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.StatFs
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_view_detail.view.*
import kotlinx.android.synthetic.main.item_rv.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.dialogs.ConfirmDeleteDialog
import vinova.intern.nhomxnxx.mexplorer.dialogs.RenameDialog
import vinova.intern.nhomxnxx.mexplorer.model.Cloud
import vinova.intern.nhomxnxx.mexplorer.utils.Support


class RvHomeAdapter(ctx : Context,view : View,frag : FragmentManager): RecyclerView.Adapter<RvHomeAdapter.ViewHolderCloud>() {
	private var listCloud : MutableList<Cloud> = mutableListOf()
	private val context : Context = ctx
	private val root : View = view
	private val sup = frag
	private lateinit var listener: ItemClickListener
	private val bottomSheetBehave = BottomSheetBehavior.from(root)
	private val token = DatabaseHandler(context).getToken()
	fun setData(clouds : List<Cloud>?){
		if (clouds!=null)
			this.listCloud = clouds.sortedBy {
				it.name
			}.toMutableList()
		notifyDataSetChanged()
	}

	fun setListener(listener: ItemClickListener) {
		this.listener = listener
	}

	fun refreshData(clouds : List<Cloud>?){
		this.listCloud = clouds?.sortedBy {
			it.name
		}?.toMutableList()!!
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCloud {
		val view : View = LayoutInflater.from(parent.context)
				.inflate(R.layout.item_rv,parent,false)
		return ViewHolderCloud(view)
	}

	override fun getItemCount(): Int {
		return listCloud.size
	}

	@SuppressLint("SetTextI18n")
	override fun onBindViewHolder(holder: ViewHolderCloud, position: Int) {
		val cl : Cloud = listCloud[position]
		holder.name.text = cl.name

		cl.type?.let { setIcon(holder.thumb, it) }

		holder.btn.setOnClickListener {
			bottomSheetBehave.state = BottomSheetBehavior.STATE_EXPANDED
			val cloud = listCloud[holder.adapterPosition]
			root.share.setOnClickListener {
				val sharingIntent = Intent(Intent.ACTION_SEND)
				sharingIntent.type = "text/plain"
				val shareBody = "Here is the share content body"
				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here")
				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
				ContextCompat.startActivity(context, Intent.createChooser(sharingIntent, "Share via"), null)
			}
			root.rename.setOnClickListener {
				bottomSheetBehave.state = BottomSheetBehavior.STATE_COLLAPSED
				RenameDialog.newInstanceCloud(cloud.name!!,cloud.id!!,token!!).show(sup,"halo")
			}
			root.copyFile.setOnClickListener {

			}
			root.moveFile.setOnClickListener {

			}
			root.openWith.setOnClickListener {

			}
			root.deleteFile.setOnClickListener {
				bottomSheetBehave.state = BottomSheetBehavior.STATE_COLLAPSED
				ConfirmDeleteDialog.newInstanceCloud(cloud.name!!,cloud.id!!).show(sup,"halo")
			}
		}

		if (cl.type== "local"){
			holder.used.text = "${getAvailableInternalMemorySize()} of ${getTotalInternalMemorySize()}"
		}
		else{
			val sum = Support.getFileSize(cl.used!! + cl.allocated!!)
			val used = "${Support.getFileSize(cl.used!!)} of $sum"
			holder.used.text = used
			holder.process.progress = ((cl.used!!/(cl.used!! + cl.allocated!!))*100).toInt()
			root.share.visibility = View.GONE
			root.available.visibility = View.GONE
		}

	}

	private fun setIcon(img : ImageView,type:String){
		val a = when(type){
			"dropbox" -> R.drawable.ic_logo_dropbox
			"googledrive" -> R.drawable.ic_logo_google_drive
			"local" -> R.drawable.ic_logo_folder
			"box" -> R.drawable.ic_logo_box
			else -> R.drawable.ic_logo_onedrive
		}
		Glide.with(context)
				.load(a)
				.into(img)
	}

	inner class ViewHolderCloud(itemView: View) : RecyclerView.ViewHolder(itemView) , View.OnClickListener{
		val thumb : ImageView = itemView.imageView
		val name : TextView = itemView.nameCloud
		val used : TextView = itemView.used_memory
		val process : ProgressBar = itemView.percentage
		val btn : Button = itemView.btnSetting
		init {
			itemView.setOnClickListener(this)
		}

		override fun onClick(p0: View?) {
			listener.onItemClick(listCloud[adapterPosition])
		}
	}

	private fun getAvailableInternalMemorySize(): String {
		val path = Environment.getDataDirectory()
		val stat = StatFs(path.path)
		val blockSize = stat.blockSizeLong
		val availableBlocks = stat.availableBlocksLong
		return formatSize(availableBlocks * blockSize)
	}

	private fun getTotalInternalMemorySize(): String {
		val path = Environment.getDataDirectory()
		val stat = StatFs(path.path)
		val blockSize = stat.blockSizeLong
		val totalBlocks = stat.blockCountLong
		return formatSize(totalBlocks * blockSize)
	}

	private fun formatSize(size_: Long): String {
		var size = size_
		var suffix: String? = null

		if (size >= 1024) {
			suffix = "KB"
			size /= 1024
			if (size >= 1024) {
				suffix = "MB"
				size /= 1024
			}
		}

		val resultBuffer = StringBuilder(java.lang.Long.toString(size))

		var commaOffset = resultBuffer.length - 3
		while (commaOffset > 0) {
			resultBuffer.insert(commaOffset, ',')
			commaOffset -= 3
		}

		if (suffix != null) resultBuffer.append(suffix)
		return resultBuffer.toString()
	}

	interface ItemClickListener {
		fun onItemClick(cloud : Cloud)
	}
}