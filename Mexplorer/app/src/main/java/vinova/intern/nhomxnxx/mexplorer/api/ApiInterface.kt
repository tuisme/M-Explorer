package vinova.intern.nhomxnxx.mexplorer.api

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

    @GET("/api/v2/clouds")
    fun addListCloud(@Header("Access-Token") token : String) : Call<ListCloud>

    @GET("/api/v2/folders/{id}")
    fun gotoCloud(@Path("id") id : String, @Header("ctoken") token :String, @Header("Access-Token") userToken : String) : Call<SpecificCloud>

    @PUT("/api/v2/clouds/{id}")
    fun changeNameCloud(@Header("Access-Token") token :String,
                        @Path("id") id : String , @Query("cname") name : String) : Call<RequestChangeName>

    @DELETE("/api/v2/clouds/{id}")
    fun deleteDrive(@Header("Access-Token") token:String,@Path("id") id : String) : Call<RequestChangeName>

    @GET("/api/v2/files/{id}")
    fun getUrlFile(@Path("id") id :String ,@Query("ctoken") token:String,
                   @Header("Access-Token") user_token : String) : Call<SpecificFile>


    @POST("/api/v2/clouds")
    fun getDrive(@Header("Access-Token") user_token : String,
                 @Query("code") serverAuth : String, @Query("cname") name : String,
                 @Query("provider") provider:String) :  Call<RequestChangeName>
    @GET("/api/v2/devices")
    fun getDevices(@Header("Access-Token") token : String) : Call<ListDevice>
}