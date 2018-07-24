package vinova.intern.nhomxnxx.mexplorer.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class File (

    @SerializedName("id")
    @Expose
    var id: String? = null,
    @SerializedName("name")
    @Expose
    var name: String? = null,
    @SerializedName("size")
    @Expose
    var size: String? = null,
    @SerializedName("type")
    @Expose
    var type: String? = null,
    @SerializedName("url")
    @Expose
    var url: String? = null
)