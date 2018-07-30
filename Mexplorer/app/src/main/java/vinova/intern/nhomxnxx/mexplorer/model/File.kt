package vinova.intern.nhomxnxx.mexplorer.model


data class File (
    var id: String? = null,
    var name: String? = null,
    var size: String? = null,
    var type: String? = null,
    var url: String? = null
)

data class SpecificCloud (
    var time: String? = null,
    var status: String? = null,
    var message: Any? = null,
    var data: List<FileSec>? = null
)

data class SpecificFile (
        var data: FileDetail? = null
) : BaseResponse()

data class FileSec (
        var id: String? = null,
        var name: String? = null,
        var has_thumbnail:Boolean? = null,
        var thumbnail_link: String? = null,
        var size: String? = null,
        var mime_type: String? = null,
        var created_time : String? = null
)

data class FileDetail(
		var id: String? = null,
		var name: String? = null,
		var url: String? = null,
		var size: String? = null,
		var mime_type: String? = null,
		var created_time : String? = null
)

