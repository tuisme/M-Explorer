package vinova.intern.nhomxnxx.mexplorer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_device.view.*
import vinova.intern.nhomxnxx.mexplorer.R
import vinova.intern.nhomxnxx.mexplorer.model.Devices
import android.text.format.DateUtils
import android.view.View.GONE
import android.view.View.VISIBLE
import java.text.SimpleDateFormat
import java.util.*


class DeviceAdapter(context: Context ): RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {
    private var listDevices: MutableList<Devices> = mutableListOf()
    private val ctx: Context= context
    @SuppressLint("HardwareIds")
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    fun setData(devices : MutableList<Devices>?){
        this.listDevices = devices!!
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view: View = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_device, parent, false)
        return DeviceViewHolder(view)
    }
    override fun getItemCount(): Int {
        return listDevices.size
    }
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val devices = listDevices[position]
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
        val time = sdf.parse(devices.created_at).getTime()
        val now = System.currentTimeMillis()
        val ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS)
        holder.name.text = devices.device_name
        holder.location.text = devices.device_location
        holder.time.text = ago
        if(devices.device_id == androidId) {
            holder.connecting.visibility = VISIBLE
            holder.time.visibility = GONE
        }
        if (devices.device_type == "android"){
            Glide.with(ctx)
                    .load(R.drawable.ic_android)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.image)
        }
        else if (devices.device_type == "ios"){
            Glide.with(ctx)
                    .load(R.drawable.ic_ios)
                    .apply(RequestOptions().circleCrop())
                    .into(holder.image)
        }
        else if(devices.device_type == "windows") {
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
    }
    inner class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image : ImageView = itemView.image_device
        val name : TextView = itemView.name_device
        val time: TextView = itemView.time
        val connecting: TextView = itemView.connecting
        val location : TextView = itemView.location_device
    }
}