package vinova.intern.nhomxnxx.mexplorer.model

import android.os.Parcel
import android.os.Parcelable

data class User (
        var token: String? = null,
        var email: String? = null,
        var first_name: String? = null,
        var last_name: String? = null,
        var avatar_url: String? = null,
        var mentAuth: String? = null,
        var used: Double? = null,
        var is_vip: Boolean? = null,
        var allocated : Double? = null
):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Double::class.java.classLoader) as? Double,
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readValue(Double::class.java.classLoader) as? Double) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(token)
        parcel.writeString(email)
        parcel.writeString(first_name)
        parcel.writeString(last_name)
        parcel.writeString(avatar_url)
        parcel.writeString(mentAuth)
        parcel.writeValue(used)
        parcel.writeValue(is_vip)
        parcel.writeValue(allocated)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
