package vinova.intern.nhomxnxx.mexplorer.model

data class Request(
		var data: User? = null
) : BaseResponse()

class RequestChangeName(

) : BaseResponse()

data class UpdateUser(
		var user: User?= null
) : BaseResponse()
