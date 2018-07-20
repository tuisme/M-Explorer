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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_view_detail.view.*
import kotlinx.android.synthetic.main.item_rv.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.Cloud

class RvHomeAdapter(ctx : Context,view : View): RecyclerView.Adapter<RvHomeAdapter.ViewHolderCloud>() {
	private var listCloud : MutableList<Cloud> = mutableListOf()
	private val context : Context = ctx
	private val root : View = view
	fun setData(clouds : List<Cloud>?){
		if (clouds!=null)
			this.listCloud = clouds.sortedBy {
				it.cname
			}.toMutableList()
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

	override fun onBindViewHolder(holder: ViewHolderCloud, position: Int) {
		val cl : Cloud = listCloud[position]
		holder.name.text = cl.cname
		val use = cl.used?.toFloat()
		val sum = use?.let { cl.used?.toFloat()?.plus(it) }
		if (use != null && sum != null) holder.process.progress = (use/sum *100).toInt()
		val used = "${cl.used} of $sum"
		holder.used.text = used
		cl.ctype?.let { setIcon(holder.thumb, it) }
		holder.btn.setOnClickListener {
			val bottomSheetBehave = BottomSheetBehavior.from(root)
			bottomSheetBehave.state = BottomSheetBehavior.STATE_EXPANDED
		}
		root.deleteFile.setOnClickListener {

		}
		root.copyFile.setOnClickListener {

		}
		root.share.setOnClickListener {

		}
		root.moveFile.setOnClickListener {

		}
		root
	}

	private fun setIcon(img : ImageView,type:String){
		val a = when(type){
			"dropbox" -> R.drawable.ic_logo_dropbox
			"googledrive" -> R.drawable.ic_logo_google_drive
			"local" -> R.drawable.ic_logo_folder
			else -> R.drawable.ic_logo_onedrive
		}
		Glide.with(context)
				.load(a)
				.into(img)
	}

	class ViewHolderCloud(itemView: View) : RecyclerView.ViewHolder(itemView) {

		val thumb : ImageView = itemView.imageView
		val name : TextView = itemView.nameCloud
		val used : TextView = itemView.used_memory
		val process : ProgressBar = itemView.percentage
		val btn : Button = itemView.btnSetting
	}

}