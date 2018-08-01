package vinova.intern.nhomxnxx.mexplorer.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import vinova.intern.nhomxnxx.mexplorer.model.*

interface ApiInterface {

    @POST("/api/v2/users/signup")
    fun signUp(@Query("first_name") first_name: String,
                      @Query("last_name") last_name:String,
                      @Query("email") email:String,
                      @Query("password") password:String): Call<Request>

    @POST("/api/v2/users/signin")
    fun logIn(@Query("email") email:String,
              @Query("password") password:String,
              @Query("android_id") android_id:String,
              @Query("android_name") android_name:String): Call<Request>

    @POST("/api/v2/users/logout")
    fun logout(@Header("Access-Token") token:String) : Call<Request>


    @POST("/api/v2/users/provider")
    fun logInProvider(@Query("provider") provider:String,
                    @Query("email") email:String,
                    @Query("first_name") first_name: String,
                    @Query("last_name") last_name: String,
                    @Query("android_id") android_id: String,
                    @Query("android_name") android_name: String): Call<Request>

    @GET("/api/v2/clouds")
    fun getListCloud(@Header("Access-Token") token : String) : Call<ListCloud>

    @GET("/api/v2/folders")
    fun gotoCloud(@Query("id") id : String, @Query("ctoken") token :String,
                  @Header("Access-Token") userToken : String, @Query("ctype") type : String) : Call<SpecificCloud>

    @PUT("/api/v2/clouds/{id}")
    fun changeNameCloud(@Header("Access-Token") token :String,
                        @Path("id") id : String , @Query("cname") name : String) : Call<RequestChangeName>

    @DELETE("/api/v2/clouds/{id}")
    fun deleteDrive(@Header("Access-Token") token:String,@Path("id") id : String) : Call<RequestChangeName>

    @GET("/api/v2/files")
    fun getUrlFile(@Query("id") id :String, @Query("ctoken") token:String,
                   @Header("Access-Token") user_token : String, @Query("ctype")ctype:String) : Call<SpecificFile>


    @POST("/api/v2/clouds")
    fun getDrive(@Header("Access-Token") user_token : String,
                 @Query("code") serverAuth : String, @Query("cname") name : String,
                 @Query("provider") provider:String) :  Call<RequestChangeName>

    @GET("/api/v2/devices")
    fun getDevices(@Header("Access-Token") token : String) : Call<ListDevice>

    @Multipart
    @POST("/api/v2/files")
    fun uploadFile(@Header("Access-Token") user_token: String,
                   @Query("id") id: String, @Part file : MultipartBody.Part, @Query("ctype") ctype: String,
                   @Query("ctoken") ctoken : String) : Call<BaseResponse>

	@PUT("/api/v2/files")
	fun renameFile(@Header("Access-Token") user_token: String, @Query("id") id: String,
	               @Query("fname") fname : String, @Query("ctype") ctype: String,
	               @Query("ctoken") ctoken: String) : Call<BaseResponse>

    @POST("/api/v2/folders")
    fun createFolder(@Header("Access-Token") user_token: String,@Query("fname") fname: String,
                     @Query("parent") parent : String,@Query("ctype") ctype: String,
                     @Query("ctoken") ctoken: String) : Call<BaseResponse>
    @DELETE("/api/v2/files")
    fun deleteFile(@Header("Access-Token") user_token: String, @Query("id") id: String,
                   @Query("ctype") ctype: String,
                   @Query("ctoken") ctoken: String) : Call<BaseResponse>
}