package vinova.intern.nhomxnxx.mexplorer.api

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import vinova.intern.nhomxnxx.mexplorer.model.Request
import vinova.intern.nhomxnxx.mexplorer.model.User

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

    @POST("/api/v2/users/forgot")
    fun requestNewPass(@Query("email") email : String) : Call<Request>

    @POST("/api/v2/users/facebook")
    fun logInWithFB(@Query("email") email:String,
                    @Query("uid") uid:String,
                    @Query("first_name") first_name: String,
                    @Query("last_name") last_name: String,
                    @Query("android_id") android_id:String,
                    @Query("android_name") android_name:String): Call<Request>
}