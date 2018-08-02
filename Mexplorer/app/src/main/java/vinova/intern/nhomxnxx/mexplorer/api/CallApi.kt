package vinova.intern.nhomxnxx.mexplorer.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CallApi {
	companion object {
		val Base_URL : String = "https://mexplorer.herokuapp.com"
		private var api : ApiInterface? = null

		fun getInstance():ApiInterface{
			if (api == null)
				api = createService()
			return api as ApiInterface
		}

		fun builder(): Retrofit {
			val gson = GsonBuilder().setLenient().create()
			return Retrofit.Builder()
					.addConverterFactory(GsonConverterFactory.create(gson))
					.baseUrl(Base_URL)
					.build()
		}

		fun createService(): ApiInterface {
			return builder().create(ApiInterface::class.java)
		}
	}
}
class CallApiFaceAuth{
	companion object {

		val Base_URL :String = "https://api-us.faceplusplus.com"
		private var api : ApiFaceAuthInterface? = null

		fun getInstance():ApiFaceAuthInterface{
			if (api == null)
				api = createService()
			return api as ApiFaceAuthInterface
		}

		fun builder(): Retrofit {
			val gson = GsonBuilder().setLenient().create()
			return Retrofit.Builder()
					.addConverterFactory(GsonConverterFactory.create(gson))
					.baseUrl(Base_URL)
					.build()
		}

		fun createService(): ApiFaceAuthInterface {
			return builder().create(ApiFaceAuthInterface::class.java)
		}
	}

}