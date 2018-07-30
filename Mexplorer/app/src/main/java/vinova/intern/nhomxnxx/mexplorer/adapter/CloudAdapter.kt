package vinova.intern.nhomxnxx.mexplorer.adapter

import android.content.Context
import android.content.Intent
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
import vinova.intern.nhomxnxx.mexplorer.model.FileSec

@Suppress("IMPLICIT_CAST_TO_ANY")
class CloudAdapter(ctx : Context,view : TextView,rot : View,frag : FragmentManager): RecyclerView.Adapter<CloudAdapter.ViewHolderCloudFolder>() {
	val context = ctx
	val error = view
	val root = rot
	private val sup = frag
	private val bottomSheetBehave = BottomSheetBehavior.from(root)
	var files: List<FileSec> = mutableListOf()
	private lateinit var listener: CloudAdapter.ItemClickListener
	private val token = DatabaseHandler(context).getToken()

	fun setData(list : List<FileSec>){
		this.files = list
		if(files.isEmpty())
			error.visibility = View.VISIBLE
	}

	fun setListener(listener: CloudAdapter.ItemClickListener) {
		this.listener = listener
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCloudFolder {
		val view : View = LayoutInflater.from(parent.context)
				.inflate(R.layout.item_rv,parent,false)
		return ViewHolderCloudFolder(view)
	}

	override fun getItemCount(): Int {
		return files.size
	}

	override fun onBindViewHolder(holder: ViewHolderCloudFolder, position: Int) {
		val file  = files[position]
		setIcon(holder.thumb,file)
		holder.name.text = file.name
		holder.process.visibility = View.GONE

		holder.btn.setOnClickListener {
			bottomSheetBehave.state = BottomSheetBehavior.STATE_EXPANDED
			val fileSec = files[holder.adapterPosition]
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
				RenameDialog.newInstanceCloud(fileSec.name!!,fileSec.id!!,token!!).show(sup,"halo")
			}
			root.copyFile.setOnClickListener {

			}
			root.moveFile.setOnClickListener {

			}
			root.openWith.setOnClickListener {

			}
			root.deleteFile.setOnClickListener {
				bottomSheetBehave.state = BottomSheetBehavior.STATE_COLLAPSED
				ConfirmDeleteDialog.newInstanceCloud(fileSec.name!!,fileSec.id!!).show(sup,"halo")
			}
		}
	}

	private fun setIcon(img : ImageView,file: FileSec){
		val a = when(file.mime_type){
			"folder" -> R.drawable.ic_logo_folder
			else -> file.thumbnail_link
		}
		Glide.with(context)
				.load(a)
				.into(img)
	}

	inner class ViewHolderCloudFolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
		val thumb : ImageView = itemView.imageView
		val name : TextView = itemView.nameCloud
		val used : TextView = itemView.used_memory
		val process : ProgressBar = itemView.percentage
		val btn : Button = itemView.btnSetting
		init {
			itemView.setOnClickListener(this)
		}
		override fun onClick(p0: View?) {
			listener.onItemClick(files[adapterPosition])
		}
	}

	interface ItemClickListener {
		fun onItemClick(file : FileSec)
	}
}