package vinova.intern.nhomxnxx.mexplorer.model

import android.os.Parcel
import android.os.Parcelable

data class ListDevice(
        var time: String? = null,
        var status: String? = null,
        var message: String? = null,
        var data: List<Devices>? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.createTypedArrayList(Devices)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(time)
        parcel.writeString(status)
        parcel.writeString(message)
        parcel.writeTypedList(data)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ListDevice> {
        override fun createFromParcel(parcel: Parcel): ListDevice {
            return ListDevice(parcel)
        }

        override fun newArray(size: Int): Array<ListDevice?> {
            return arrayOfNulls(size)
        }
    }
}
class Devices():Parcelable{
    var id: String? = null
    var device_id: String? = null
    var device_name: String? = null
    var device_type: String? = null
    var device_location: String? = null
    var created_at: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        device_id = parcel.readString()
        device_name = parcel.readString()
        device_type = parcel.readString()
        device_location = parcel.readString()
        created_at = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(device_id)
        parcel.writeString(device_name)
        parcel.writeString(device_type)
        parcel.writeString(device_location)
        parcel.writeString(created_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Devices> {
        override fun createFromParcel(parcel: Parcel): Devices {
            return Devices(parcel)
        }

        override fun newArray(size: Int): Array<Devices?> {
            return arrayOfNulls(size)
        }
    }

}
