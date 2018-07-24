package vinova.intern.nhomxnxx.mexplorer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_rv.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.File

@Suppress("IMPLICIT_CAST_TO_ANY")
class CloudAdapter(ctx : Context,view : TextView): RecyclerView.Adapter<CloudAdapter.ViewHolderCloudFolder>() {
	val context = ctx
	val error = view
	var files: List<File> = mutableListOf()
	private lateinit var listener: CloudAdapter.ItemClickListener

	fun setData(list : List<File>){
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
	}

	private fun setIcon(img : ImageView,file: File){
		val a = when(file.type){
			"folder" -> R.drawable.ic_logo_folder
			else -> file.url
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
		fun onItemClick(file : File)
	}
}