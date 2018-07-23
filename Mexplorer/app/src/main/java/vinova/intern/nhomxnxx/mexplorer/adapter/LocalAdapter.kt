package vinova.intern.nhomxnxx.mexplorer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_folder.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.File
import android.webkit.MimeTypeMap
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri


class LocalAdapter(context: Context): RecyclerView.Adapter<LocalAdapter.LocalViewHolder>() {
    private var fileList: MutableList<File> = mutableListOf()
    var path: String = Environment.getExternalStorageDirectory().absolutePath

    private val ctx = context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocalViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_folder, parent, false)
        return LocalViewHolder(view)
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
                        .load(R.drawable.ic_logo_folder)
                        .into(holder.logo)
                holder.size.visibility = View.GONE
            }
//            "jpg", "png" -> {
//                Glide.with(ctx)
//                        .load(file.url)
//                        .into(holder.logo)
//                holder.size.text = "${file.size} KB"
//            }
            else -> {
                Glide.with(ctx)
                        .load(file.url)
                        .into(holder.logo)
                holder.size.text = "${file.size} KB"
            }
        }

        holder.itemView.setOnClickListener {
            if (java.io.File("$path/${fileList[position].name}").isDirectory) {
                this.path = "$path/${fileList[position].name}"
                //listDir(this.path, holder, position)
                setData()
                notifyDataSetChanged()
            } else if (java.io.File("$path/${fileList[position].name}").isFile) {
                openFile(java.io.File("$path/${fileList[position].name}"))
            }
        }
    }

    class LocalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo: ImageView = itemView.iv_logo
        val name: TextView = itemView.tv_name
        val size: TextView = itemView.tv_size
    }

    private fun getMimeType(uri: Uri): String? {
        val mimeType: String?
        mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            val cr = ctx.contentResolver
            cr.getType(uri)
        } else {
            val regex = Regex("[^A-Za-z0-9 .]")
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString().replace(regex, ""))
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase())
        }
        return mimeType
    }
    fun getData(path: String):List<File>{
        val root = java.io.File(path)
        val files = root.listFiles()
        fileList.clear()
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
        return fileList
    }

    private fun openFile(url: java.io.File){
        val uri = Uri.fromFile(url)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri,getMimeType(uri))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }
}