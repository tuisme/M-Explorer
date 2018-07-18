package vinova.intern.nhomxnxx.mexplorer.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CallApi {
	lateinit var api :ApiInterface
	companion object {
		val Base_URL : String = "https://mexplorer.herokuapp.com"
		fun builder(): Retrofit {
			return Retrofit.Builder()
					.addConverterFactory(GsonConverterFactory.create())
					.baseUrl(Base_URL)
					.build()
		}
		fun createService(): ApiInterface {
			return builder().create(ApiInterface::class.java)
		}
	}
}