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
    @SerializedName("thumbnail")
    @Expose
    var url: String? = null
)

data class SpecificCloud (
    @SerializedName("time")
    @Expose
    var time: String? = null,
    @SerializedName("status")
    @Expose
    var status: String? = null,
    @SerializedName("message")
    @Expose
    var message: Any? = null,
    @SerializedName("data")
    @Expose
    var data: List<File>? = null
)

data class SpecificFile (
        @SerializedName("time")
        @Expose
        var time: String? = null,
        @SerializedName("status")
        @Expose
        var status: String? = null,
        @SerializedName("message")
        @Expose
        var message: Any? = null,
        @SerializedName("data")
        @Expose
        var data: FileSec? = null
)

data class FileSec (

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