package vinova.intern.nhomxnxx.mexplorer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_folder.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.File
import vinova.intern.nhomxnxx.mexplorer.utils.Support


class LocalAdapter(context: Context,view : View): RecyclerView.Adapter<LocalAdapter.LocalViewHolder>(){
    private var fileList: MutableList<File> = mutableListOf()
    var path: String = Environment.getExternalStorageDirectory().absolutePath
    val error : TextView = view as TextView
    private var mListener: OnFileItemListener? = null

    private val ctx = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_folder, parent, false)
        return LocalViewHolder(view)
    }

    fun setListener(listener: OnFileItemListener) {
        mListener = listener
    }


    override fun getItemCount(): Int {
        return fileList.size
    }

    fun setData() {
        this.fileList = getData(path) as MutableList<File>
//        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LocalViewHolder, position: Int) {
        val file = fileList[position]
        holder.name.text = file.name
        when (file.type) {
            null -> {
                Glide.with(ctx)
                        .load(R.drawable.ic_folder)
                        .apply(RequestOptions().circleCrop())
                        .into(holder.logo)
                holder.size.visibility = View.GONE
            }
            "zip" -> {
                Glide.with(ctx)
                        .load(R.drawable.ic_zip)
                        .into(holder.logo)
                holder.size.visibility = View.VISIBLE

                holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it) }
            }
            "txt" -> {
                Glide.with(ctx)
                        .load(R.drawable.ic_txt)
                        .into(holder.logo)
                holder.size.visibility = View.VISIBLE

                holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it) }
                }
            "doc"-> {
                Glide.with(ctx)
                        .load(R.drawable.ic_doc)
                        .into(holder.logo)
                holder.size.visibility = View.VISIBLE

                holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it) }
            }
            "jpg", "png","mp4","gif" -> {
                Glide.with(ctx)
                        .load(file.url)
                        .apply ( RequestOptions().circleCrop() )
                        .into(holder.logo)
                holder.size.visibility = View.VISIBLE
                holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it) }
            }
            else -> {
                Glide.with(ctx)
                        .load(R.drawable.ic_doc)
                        .into(holder.logo)
                holder.size.visibility = View.VISIBLE
                holder.size.text = file.size?.toLong()?.let { Support.getFileSize(it)}
            }
        }

        holder.itemView.setOnClickListener {
            mListener?.onClick(java.io.File("$path/${fileList[position].name}"))
        }

        holder.itemView.setOnLongClickListener {
            mListener?.onLongClick(java.io.File("$path/${fileList[position].name}"))
            true
        }
    }

    class LocalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.iv_logo
        val name: TextView = itemView.tv_name
        val size: TextView = itemView.tv_size
    }

    fun getData(path: String):List<File>{
        val root = java.io.File(path)
        val files = root.listFiles()
        fileList.clear()
        if (files.isEmpty())
            error.visibility = View.VISIBLE
        for (file in files) {
            val fileName = file.name
            if (!fileName.startsWith(".")) {
                if(file.isFile) {
                    val regex = Regex("[^A-Za-z0-9 .]")
                    val fileExtension = MimeTypeMap.getFileExtensionFromUrl(file.toString().replace(regex, ""))
                    fileList.add(File(null, fileName,
                            file.length().toString(), fileExtension,file.path))
                }
                else{
                    fileList.add(File(null, fileName,
                            file.length().toString()))
                }
            }
        }
        fileList.sortWith(compareBy ({ it.type }, {it.name}))
        return fileList
    }

    fun refreshData(){
        setData()
        notifyDataSetChanged()
    }

    interface OnFileItemListener {
        fun onClick(file: java.io.File)

        fun onLongClick(file: java.io.File)
    }
}