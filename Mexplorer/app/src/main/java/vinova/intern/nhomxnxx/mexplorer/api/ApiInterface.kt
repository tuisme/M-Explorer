package vinova.intern.nhomxnxx.mexplorer.api

import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*
import vinova.intern.nhomxnxx.mexplorer.model.*

interface ApiInterface {

    @POST("/api/v2/users/forget_password")
    fun forgotPass(@Query("email") email: String) : Call<BaseResponse>

    @POST("/api/v2/users/signup")
    fun signUp(@Query("first_name") first_name: String,
                      @Query("last_name") last_name:String,
                      @Query("email") email:String,
                      @Query("password") password:String): Call<Request>

    @POST("/api/v2/users/signin")
    fun logIn(@Query("email") email:String,
              @Query("password") password:String,
              @Query("device_id") android_id:String,
              @Query("device_name") android_name:String,
              @Query("device_type") type : String,
              @Query("device_location") location : String): Call<Request>

    @DELETE("/api/v2/users")
    fun logout(@Header("Access-Token") token:String) : Call<Request>


    @POST("/api/v2/users/provider")
    fun logInProvider(@Query("provider") provider:String,
                    @Query("email") email:String,
                    @Query("first_name") first_name: String,
                    @Query("last_name") last_name: String,
                    @Query("device_id") android_id: String,
                    @Query("device_name") android_name: String,
                    @Query("device_type") type : String,
                    @Query("device_location") location : String): Call<Request>

    @GET("/api/v2/clouds")
    fun getListCloud(@Header("Access-Token") token : String) : Call<ListCloud>

    @GET("/api/v2/folders")
    fun gotoCloud(@Query("id") id : String, @Query("token") token :String,
                  @Header("Access-Token") userToken : String, @Query("type") type : String) : Call<SpecificCloud>

    @PUT("/api/v2/clouds/{id}")
    fun changeNameCloud(@Header("Access-Token") token :String,
                        @Path("id") id : String , @Query("name") name : String) : Call<RequestChangeName>

    @DELETE("/api/v2/clouds/{id}")
    fun deleteDrive(@Header("Access-Token") token:String,@Path("id") id : String) : Call<RequestChangeName>

    @GET("/api/v2/files")
    fun getUrlFile(@Query("id") id :String, @Query("token") token:String,
                   @Header("Access-Token") user_token : String, @Query("type")type:String) : Call<SpecificFile>

    @POST("/api/v2/clouds")
    fun getDrive(@Header("Access-Token") user_token : String,
                 @Query("code") serverAuth : String, @Query("name") name : String,
                 @Query("provider") provider:String) :  Call<RequestChangeName>

    @GET("/api/v2/devices")
    fun getDevices(@Header("Access-Token") token : String) : Call<ListDevice>
    @DELETE("/api/v2/devices/{id}")
    fun deleteDevices(@Header("Access-Token") token : String,
                      @Path("id") id: String) : Call<ListDevice>

    @POST("/api/v2/files")
    @Multipart
    fun uploadFile(@Header("Access-Token") user_token: String,
                   @Query("id") id: String, @Part file : MultipartBody.Part, @Query("type") type: String,
                   @Query("token") token : String) : Call<BaseResponse>

	@PUT("/api/v2/files")
	fun renameFile(@Header("Access-Token") user_token: String, @Query("id") id: String,
	               @Query("name") fname : String, @Query("type") type: String,
	               @Query("token") token: String) : Call<BaseResponse>

    @PUT("/api/v2/folders")
    fun renameFolder(@Header("Access-Token") user_token: String, @Query("id") id: String,
                   @Query("name") fname : String, @Query("type") type: String,
                   @Query("token") token: String) : Call<BaseResponse>

    @POST("/api/v2/folders")
    fun createFolder(@Header("Access-Token") user_token: String,
                     @Query("name") fname: String,
                     @Query("parent") parent : String,
                     @Query("type") type: String,
                     @Query("token") token: String) : Call<requestUploadFolder>

    @DELETE("/api/v2/files")
    fun deleteFile(@Header("Access-Token") user_token: String, @Query("id") id: String,
                   @Query("type") type: String,
                   @Query("token") token: String) : Call<BaseResponse>

    @POST("/api/v2/folders/zip")
    @Multipart
    fun uploadFolder(@Header("Access-Token") user_token: String,
                     @Query("id") id: String,
                     @Query("json") json : String,
                     @Part zip : MultipartBody.Part, @Query("type") type: String,
                     @Query("token") token : String) : Call<BaseResponse>
    @DELETE("/api/v2/folders")
    fun deleteFolder(@Header("Access-Token") user_token: String, @Query("id") id: String,
                   @Query("type") type: String,
                   @Query("token") token: String) : Call<BaseResponse>
    @POST("/api/v2/jobs/copy_1_cloud")
    fun copyFile(@Header("Access-Token") user_token: String, @Query("id") id: String,
                 @Query("type") type: String, @Query("token") token: String,
                 @Query("id_dest") id_dest:String, @Query("mime_type") mime_type:String) : Call<BaseResponse>

    @POST("/api/v2/jobs/move_1_cloud")
    fun moveFile(@Header("Access-Token") user_token: String, @Query("id") id: String,
                 @Query("type") type: String, @Query("token") token: String,
                 @Query("id_dest") id_dest:String, @Query("mime_type") mime_type:String) : Call<BaseResponse>
}

interface ApiFaceAuthInterface
{
    @POST("/facepp/v3/detect")
    @Multipart
    fun getFace(@Query("api_key") apiKey :String,
                @Query("api_secret") apiSec: String,
                @Part file: MultipartBody.Part) :Observable<AuthenticationFace>

    @POST("/facepp/v3/compare")
    fun compare(@Query("api_key") apiKey :String,
                @Query("api_secret") apiSec: String,
                @Query("face_token1") ft1: String,
                @Query("face_token2") ft2:String) :Observable<Compare>
}