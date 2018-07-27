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

data class Devices(
        var id: String? = null,
        var name: String? = null,
        var type: String? = null,
        var location: String? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeString(location)
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