package vinova.intern.nhomxnxx.mexplorer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_rv.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.Cloud

class rvHomeAdapter(ctx : Context): RecyclerView.Adapter<rvHomeAdapter.ViewHolderCloud>() {
	private var listCloud : MutableList<Cloud> = mutableListOf()
	private val context : Context = ctx
	fun setData(clouds : List<Cloud>?){
		if (clouds!=null)
			this.listCloud = clouds.toMutableList()
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
		holder.name.text = listCloud[position].cname
		holder.used.text = listCloud[position].used
		listCloud[position].ctype?.let { setIcon(holder.thumb, it) }
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


	}

}