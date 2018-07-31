package vinova.intern.nhomxnxx.mexplorer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_device.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.device.DeviceActivity
import vinova.intern.nhomxnxx.mexplorer.model.Devices

class DeviceAdapter(context: Context ): RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    private var listDevices: List<Devices> = mutableListOf()
    private val ctx: Context= context
    private lateinit var listener: DeviceAdapter.ItemClickListener
    fun setData(devices : List<Devices>?){
        this.listDevices = devices!!
        notifyDataSetChanged()
    }
    fun setListener(listener: DeviceAdapter.ItemClickListener) {
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_device, parent, false)
        return DeviceAdapter.DeviceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listDevices.size
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val devices = listDevices[position]
        holder.name.text = devices.name
        holder.location.text = devices.location
        if (devices.type == "android"){
            Glide.with(ctx)
                    .load(R.drawable.ic_android)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.image)
        }
        else if (devices.type == "ios"){
            Glide.with(ctx)
                    .load(R.drawable.ic_ios)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.image)
        }
        else if(devices.type == "windows") {
            Glide.with(ctx)
                    .load(R.drawable.ic_windows)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.image)
        }
        else {
            Glide.with(ctx)
                    .load(R.drawable.ic_emulator)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.image)
        }
        holder.remove.setOnClickListener {
            Toast.makeText(ctx,"hello",Toast.LENGTH_LONG).show()
        }

    }
    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.image_device
        val name : TextView = itemView.name_device
        val location : TextView = itemView.location_device
        val remove: Button = itemView.remove_device
    }
    interface ItemClickListener {
        fun onItemClick(devices : Devices)

    }

}