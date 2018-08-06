package vinova.intern.nhomxnxx.mexplorer.adapter

import android.content.Context
import android.os.Parcel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.item_ads.view.*
import kotlinx.android.synthetic.main.item_folder.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.databaseSQLite.DatabaseHandler
import vinova.intern.nhomxnxx.mexplorer.model.Cloud
import vinova.intern.nhomxnxx.mexplorer.model.FileSec
import vinova.intern.nhomxnxx.mexplorer.utils.Support
import java.util.*

@Suppress("IMPLICIT_CAST_TO_ANY")
class CloudAdapter(ctx : Context,view : TextView,rot : View,frag : FragmentManager): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	val context = ctx
	val error = view
	val root = rot
	private val sup = frag
	private val bottomSheetBehave = BottomSheetBehavior.from(root)
	var files: ArrayList<FileSec> = arrayListOf()
	private var listCloud : MutableList<Cloud> = mutableListOf()
	private lateinit var listener: CloudAdapter.ItemClickListener
	private val token = DatabaseHandler(context).getToken()

	private val ADNUMBER = 2511
	private val NORMALNUMBER = 12345

	fun setData(list : ArrayList<FileSec>){
		this.files = list
		if (itemCount > 0)
			setAds()
		if(files.isEmpty())
			error.visibility = View.VISIBLE
		else
			error.visibility = View.GONE
	}
	private fun setAds(){
		val file = FileSec.createFromParcel(Parcel.obtain())
		file.mime_type = "ads"
		files.add(0,file)
	}

	fun ClosedRange<Int>.random() =
			Random().nextInt((endInclusive + 1) - start) +  start

	fun setListener(listener: CloudAdapter.ItemClickListener) {
		this.listener = listener
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val inflater = LayoutInflater.from(context)
		return when(viewType){
			ADNUMBER ->{
				val adsView = inflater.inflate(R.layout.item_ads,parent,false)
				ViewHolderAds(adsView)
			}
			else ->{
				val view : View = LayoutInflater.from(parent.context)
						.inflate(R.layout.item_folder,parent,false)
				ViewHolderCloudFolder(view)
			}
		}
	}

	override fun getItemCount(): Int {
		return files.size
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when(holder.itemViewType){
			ADNUMBER ->{
				configureAds(holder as ViewHolderAds)
			}
			NORMALNUMBER -> {
				configureCloud(holder as ViewHolderCloudFolder,position)
			}
		}

	}

	private fun configureCloud(holder : ViewHolderCloudFolder,position: Int){
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
				||file.mime_type?.contains("pdf")!!||file.mime_type?.contains("gif")!!||file.mime_type?.contains("png")!!){
			Glide.with(context)
					.load(file.thumbnail_link)
					.apply ( RequestOptions()
							.optionalCircleCrop()
							.placeholder(setPlaceHolder(file.mime_type!!)))
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

		holder.itemView.setOnLongClickListener {
			val fileSec = files[holder.adapterPosition]
			listener.onLongClick(fileSec)
			true
		}

		holder.itemView.setOnClickListener {
			val fileSec = files[holder.adapterPosition]
			listener.onClick(fileSec)


		}

		holder.logo.setOnClickListener {
			val fileSec = files[holder.adapterPosition]
			listener.onLongClick(fileSec)
		}
	}

	private fun setPlaceHolder(type : String): Int{
		return when(type){
			"mp4" -> R.drawable.ic_mp4
			"pdf" -> R.drawable.ic_pdf_file
			else -> R.drawable.ic_logo_img
		}
	}

	private fun configureAds(holderAds: ViewHolderAds){
		val adResq = AdRequest.Builder().build()
		holderAds.ads.loadAd(adResq)
	}

	override fun getItemViewType(position: Int): Int {
		return  when(files[position].mime_type){
			"ads" -> ADNUMBER
			else -> NORMALNUMBER
		}
	}

	inner class ViewHolderCloudFolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
		override fun onClick(p0: View?) {
			listener.onLongClick(files[adapterPosition])
		}

		val logo : ImageView = itemView.iv_logo
		val name : TextView = itemView.tv_name
		val size : TextView = itemView.tv_size
	}

	inner class ViewHolderAds(adsView : View):RecyclerView.ViewHolder(adsView){
		val ads : AdView = adsView.adView
	}

	interface ItemClickListener {
		fun onClick(file : FileSec)
		fun onLongClick(file: FileSec)
	}
}