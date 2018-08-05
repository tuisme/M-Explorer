package vinova.intern.nhomxnxx.mexplorer.model

import android.os.Parcel
import android.os.Parcelable


data class ListCloud (
	var time: String? = null,
	var status: String? = null,
	var message: String? = null,
	var data: List<Cloud>? = null
):Parcelable {
	constructor(parcel: Parcel) : this(
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.createTypedArrayList(Cloud))

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(time)
		parcel.writeString(status)
		parcel.writeString(message)
		parcel.writeTypedList(data)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<ListCloud> {
		override fun createFromParcel(parcel: Parcel): ListCloud {
			return ListCloud(parcel)
		}

		override fun newArray(size: Int): Array<ListCloud?> {
			return arrayOfNulls(size)
		}
	}
}

data class Cloud (
	var id: String? = null,
	var root: String? = null,
	var name: String? = null,
	var type: String? = null,
	var token: String? = null,
	var used: Double? = null,
	var allocated: Double? = null
):Parcelable {
	constructor(parcel: Parcel) : this(
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.readValue(Long::class.java.classLoader) as? Double,
			parcel.readValue(Long::class.java.classLoader) as? Double) {
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(id)
		parcel.writeString(root)
		parcel.writeString(name)
		parcel.writeString(type)
		parcel.writeString(token)
		parcel.writeValue(used)
		parcel.writeValue(allocated)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Cloud> {
		override fun createFromParcel(parcel: Parcel): Cloud {
			return Cloud(parcel)
		}

		override fun newArray(size: Int): Array<Cloud?> {
			return arrayOfNulls(size)
		}
	}
}