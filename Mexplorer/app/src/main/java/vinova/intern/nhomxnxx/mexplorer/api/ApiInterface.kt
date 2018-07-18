package vinova.intern.nhomxnxx.mexplorer.api

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import vinova.intern.nhomxnxx.mexplorer.model.Request
import vinova.intern.nhomxnxx.mexplorer.model.User

interface ApiInterface {
    @POST("/api/v1/users/sign_up")
    fun signUp(@Query("first_name") first_name: String,
                      @Query("last_name") last_name:String,
                      @Query("email") email:String,
                      @Query("password") password:String): Call<Request>

    @POST("/api/v1/users/sign_in")
    fun logIn(@Query("email") email:String,
                       @Query("password") password:String): Call<User>

    @POST("/api/v1/users/logout")
    fun logout(@Header("Access-Token") token:String) : Call<Request>

}