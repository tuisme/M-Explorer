package vinova.intern.nhomxnxx.mexplorer.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Request {
    @SerializedName("time")
    @Expose
    val time: String? = null
    @SerializedName("status")
    @Expose
    val status: String? = null
    @SerializedName("message")
    @Expose
    val message: String? = null
    @SerializedName("user")
    @Expose
    val user: User? = null
}
