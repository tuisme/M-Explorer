package vinova.intern.nhomxnxx.mexplorer.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class ListCloud (

	@SerializedName("time")
	@Expose
	var time: String? = null,
	@SerializedName("status")
	@Expose
	var status: String? = null,
	@SerializedName("message")
	@Expose
	var message: String? = null,
	@SerializedName("data")
	@Expose
	var clouds: List<Cloud>? = null
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
		parcel.writeTypedList(clouds)
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
	var croot: String? = null,
	var cname: String? = null,
	var ctype: String? = null,
	var ctoken: String? = null,
	var used: Long? = null,
	var unused: Long? = null
):Parcelable {
	constructor(parcel: Parcel) : this(
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.readString(),
			parcel.readValue(Long::class.java.classLoader) as? Long,
			parcel.readValue(Long::class.java.classLoader) as? Long)

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(id)
		parcel.writeString(croot)
		parcel.writeString(cname)
		parcel.writeString(ctype)
		parcel.writeString(ctoken)
		parcel.writeValue(used)
		parcel.writeValue(unused)
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