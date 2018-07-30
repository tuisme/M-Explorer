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
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_view_detail.view.*
import kotlinx.android.synthetic.main.item_folder.view.*
import kotlinx.android.synthetic.main.item_rv.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.dialogs.ConfirmDeleteDialog
import vinova.intern.nhomxnxx.mexplorer.dialogs.RenameDialog
import vinova.intern.nhomxnxx.mexplorer.model.FileSec
import vinova.intern.nhomxnxx.mexplorer.utils.Support

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
		else error.visibility = View.GONE
	}

	fun setListener(listener: CloudAdapter.ItemClickListener) {
		this.listener = listener
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCloudFolder {
		val view : View = LayoutInflater.from(parent.context)
				.inflate(R.layout.item_folder,parent,false)
		return ViewHolderCloudFolder(view)
	}

	override fun getItemCount(): Int {
		return files.size
	}

	override fun onBindViewHolder(holder: ViewHolderCloudFolder, position: Int) {
		val file  = files[position]
		holder.name.text = file.name
		if (file.mime_type?.contains("folder")!!){
			Glide.with(context)
					.load(R.drawable.ic_folder)
					.apply(RequestOptions().circleCrop())
					.into(holder.logo)
			holder.size.visibility = View.GONE
		}
		else if (file.mime_type?.contains("zip")!!||file.mime_type?.contains("rar")!!){
			Glide.with(context)
					.load(R.drawable.ic_zip)
					.into(holder.logo)
			holder.size.visibility = View.VISIBLE
			holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it) }
		}
		else if (file.mime_type?.contains("txt")!!){
			Glide.with(context)
					.load(R.drawable.ic_txt)
					.into(holder.logo)
			holder.size.visibility = View.VISIBLE

			holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it) }
		}

		else if (file.mime_type?.contains("doc")!!){
			Glide.with(context)
					.load(R.drawable.ic_doc)
					.into(holder.logo)
			holder.size.visibility = View.VISIBLE

			holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it) }
		}
		else if(file.mime_type?.contains("jpeg")!!||file.mime_type?.contains("mp4")!!
				||file.mime_type?.contains("pdf")!!||file.mime_type?.contains("gif")!!){
			Glide.with(context)
					.load(file.thumbnail_link)
					.apply ( RequestOptions().circleCrop() )
					.into(holder.logo)
			holder.size.visibility = View.VISIBLE
			holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it) }
		}
		else {
			Glide.with(context)
					.load(R.drawable.ic_doc)
					.into(holder.logo)
			holder.size.visibility = View.VISIBLE
			holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it)}
		}

//
//		holder.btn.setOnClickListener {
//			bottomSheetBehave.state = BottomSheetBehavior.STATE_EXPANDED
//			val fileSec = files[holder.adapterPosition]
//			root.share.setOnClickListener {
//				val sharingIntent = Intent(Intent.ACTION_SEND)
//				sharingIntent.type = "text/plain"
//				val shareBody = "Here is the share content body"
//				sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here")
//				sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody)
//				ContextCompat.startActivity(context, Intent.createChooser(sharingIntent, "Share via"), null)
//			}
//			root.rename.setOnClickListener {
//				bottomSheetBehave.state = BottomSheetBehavior.STATE_COLLAPSED
//				RenameDialog.newInstanceCloud(fileSec.name!!,fileSec.id!!,token!!).show(sup,"halo")
//			}
//			root.copyFile.setOnClickListener {
//
//			}
//			root.moveFile.setOnClickListener {
//
//			}
//			root.openWith.setOnClickListener {
//
//			}
//			root.deleteFile.setOnClickListener {
//				bottomSheetBehave.state = BottomSheetBehavior.STATE_COLLAPSED
//				ConfirmDeleteDialog.newInstanceCloud(fileSec.name!!,fileSec.id!!).show(sup,"halo")
//			}
//		}
		holder.itemView.setOnLongClickListener {
			val fileSec = files[holder.adapterPosition]
			listener.onLongClick(fileSec)
			true
		}
		holder.itemView.setOnClickListener {
			val fileSec = files[holder.adapterPosition]
			listener.onClick(fileSec)


		}

	}

	inner class ViewHolderCloudFolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val logo : ImageView = itemView.iv_logo
		val name : TextView = itemView.tv_name
		val size : TextView = itemView.tv_size
	}

	interface ItemClickListener {
		fun onClick(file : FileSec)
		fun onLongClick(file: FileSec)
	}
}