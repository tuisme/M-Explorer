package vinova.intern.nhomxnxx.mexplorer.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User() : Parcelable {

    @SerializedName("token")
    @Expose
    var token: String? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("first_name")
    @Expose
    var firstName: String? = null
    @SerializedName("last_name")
    @Expose
    var lastName: String? = null
    @SerializedName("avatar_url")
    @Expose
    var avatarUrl: String? = null
    @SerializedName("verified")
    @Expose
    var verified: String? = null
    @SerializedName("used")
    @Expose
    var used: String? = null
    @SerializedName("is_vip")
    @Expose
    var isVip: String? = null

    constructor(parcel: Parcel) : this() {
        token = parcel.readString()
        email = parcel.readString()
        firstName = parcel.readString()
        lastName = parcel.readString()
        avatarUrl = parcel.readString()
        verified = parcel.readString()
        used = parcel.readString()
        isVip = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(token)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(avatarUrl)
        parcel.writeString(verified)
        parcel.writeString(used)
        parcel.writeString(isVip)
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