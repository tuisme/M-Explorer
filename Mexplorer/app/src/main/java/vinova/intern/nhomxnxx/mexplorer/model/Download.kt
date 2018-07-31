package vinova.intern.nhomxnxx.mexplorer.model

import android.os.Parcel
import android.os.Parcelable

class Download() : Parcelable {

    var progress: Int = 0
    var currentFileSize: Int = 0
    var totalFileSize: Int = 0

    constructor(parcel: Parcel) : this() {
        progress = parcel.readInt()
        currentFileSize = parcel.readInt()
        totalFileSize = parcel.readInt()
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(progress)
        parcel.writeInt(currentFileSize)
        parcel.writeInt(totalFileSize)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Download> {
        override fun createFromParcel(parcel: Parcel): Download {
            return Download(parcel)
        }

        override fun newArray(size: Int): Array<Download?> {
            return arrayOfNulls(size)
        }
    }

}