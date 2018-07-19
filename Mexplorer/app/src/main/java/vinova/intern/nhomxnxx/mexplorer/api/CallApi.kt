package vinova.intern.nhomxnxx.mexplorer.api

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