package vinova.intern.nhomxnxx.mexplorer.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class User {

    @SerializedName("time")
    @Expose
    var time: String? = null
    @SerializedName("status")
    @Expose
    var status: String? = null
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

}