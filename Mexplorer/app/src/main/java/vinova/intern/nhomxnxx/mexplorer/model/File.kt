package vinova.intern.nhomxnxx.mexplorer.model

import android.os.Parcel
import android.os.Parcelable


data class File (
    var id: String? = null,
    var name: String? = null,
    var size: String? = null,
    var type: String? = null,
    var url: String? = null
)

data class SpecificCloud (
    var time: String? = null,
    var status: String? = null,
    var message: Any? = null,
    var data: List<FileSec>? = null
)

data class SpecificFile (
        var data: FileDetail? = null
) : BaseResponse()

class FileSec() :Parcelable {
	var id: String? = null
	var name: String? = null
	var has_thumbnail: Boolean? = null
	var thumbnail_link: String? = null
	var size: String? = null
	var mime_type: String? = null
	var created_time: String? = null

	constructor(parcel: Parcel) : this() {
		id = parcel.readString()
		name = parcel.readString()
		has_thumbnail = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
		thumbnail_link = parcel.readString()
		size = parcel.readString()
		mime_type = parcel.readString()
		created_time = parcel.readString()
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(id)
		parcel.writeString(name)
		parcel.writeValue(has_thumbnail)
		parcel.writeString(thumbnail_link)
		parcel.writeString(size)
		parcel.writeString(mime_type)
		parcel.writeString(created_time)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<FileSec> {
		override fun createFromParcel(parcel: Parcel): FileSec {
			return FileSec(parcel)
		}

		override fun newArray(size: Int): Array<FileSec?> {
			return arrayOfNulls(size)
		}
	}
}

data class FileDetail(
		var id: String? = null,
		var name: String? = null,
		var url: String? = null,
		var size: String? = null,
		var mime_type: String? = null,
		var created_time : String? = null
)

