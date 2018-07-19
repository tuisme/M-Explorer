package vinova.intern.nhomxnxx.mexplorer.api

import retrofit2.Call
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import vinova.intern.nhomxnxx.mexplorer.model.Request
import vinova.intern.nhomxnxx.mexplorer.model.User

interface ApiInterface {
<<<<<<< HEAD
    @POST("/api/v2/users/sign_up")
=======
    @POST("/api/v2/users/signup")
>>>>>>> a1d1286b7ca5df82c31866743329195f3c9370b6
    fun signUp(@Query("first_name") first_name: String,
                      @Query("last_name") last_name:String,
                      @Query("email") email:String,
                      @Query("password") password:String): Call<Request>

<<<<<<< HEAD
    @POST("/api/v2/users/sign_in")
=======
    @POST("/api/v2/users/signin")
>>>>>>> a1d1286b7ca5df82c31866743329195f3c9370b6
    fun logIn(@Query("email") email:String,
              @Query("password") password:String,
              @Query("android_id") android_id:String,
              @Query("android_name") android_name:String): Call<Request>

    @POST("/api/v2/users/logout")
    fun logout(@Header("Access-Token") token:String) : Call<Request>

    @POST("/api/v2/users/forgot")
    fun requestNewPass(@Query("email") email : String) : Call<Request>

<<<<<<< HEAD
    @POST("/api/v2/users/google")
    fun logInGoogle(@Query("email") email:String,
                    @Query("first_name") first_name: String,
                    @Query("last_name") last_name: String )
=======
    @POST("/api/v2/users/facebook")
    fun logInWithFB(@Query("email") email:String,
                    @Query("uid") uid:String,
                    @Query("first_name") first_name: String,
                    @Query("last_name") last_name: String,
                    @Query("android_id") android_id:String,
                    @Query("android_name") android_name:String): Call<Request>
>>>>>>> a1d1286b7ca5df82c31866743329195f3c9370b6
}