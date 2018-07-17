package vinova.intern.nhomxnxx.mexplorer.model

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

class User {

    @SerializedName("id")
    @Expose
    var id: Int? = null
    @SerializedName("email")
    @Expose
    var email: String? = null
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
    @SerializedName("first_name")
    @Expose
    var firstName: String? = null
    @SerializedName("last_name")
    @Expose
    var lastName: String? = null
    @SerializedName("token")
    @Expose
    var token: String? = null

}