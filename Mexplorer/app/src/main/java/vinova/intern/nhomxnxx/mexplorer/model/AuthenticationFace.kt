package vinova.intern.nhomxnxx.mexplorer.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class AuthenticationFace {
        @SerializedName("image_id")
        @Expose
        var imageId: String? = null
        @SerializedName("request_id")
        @Expose
        var requestId: String? = null
        @SerializedName("time_used")
        @Expose
        var timeUsed: Int? = null
        @SerializedName("faces")
        @Expose
        var faces: List<Face>? = null
}

class Face {

    @SerializedName("face_rectangle")
    @Expose
    var faceRectangle: FaceRectangle? = null
    @SerializedName("face_token")
    @Expose
    var faceToken: String? = null
}

class FaceRectangle {

    @SerializedName("width")
    @Expose
    var width: Int? = null
    @SerializedName("top")
    @Expose
    var top: Int? = null
    @SerializedName("left")
    @Expose
    var left: Int? = null
    @SerializedName("height")
    @Expose
    var height: Int? = null
}

class Thresholds {

    @SerializedName("1e-3")
    @Expose
    var _1e3: Double? = null
    @SerializedName("1e-5")
    @Expose
    var _1e5: Double? = null
    @SerializedName("1e-4")
    @Expose
    var _1e4: Double? = null
}

class Compare {

    @SerializedName("time_used")
    @Expose
    var timeUsed: Int? = null
    @SerializedName("confidence")
    @Expose
    var confidence: Double? = null
    @SerializedName("thresholds")
    @Expose
    var thresholds: Thresholds? = null
    @SerializedName("request_id")
    @Expose
    var requestId: String? = null

}

