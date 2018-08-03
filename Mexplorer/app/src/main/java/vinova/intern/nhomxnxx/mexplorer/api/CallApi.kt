package vinova.intern.nhomxnxx.mexplorer.api

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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
			val client = OkHttpClient.Builder()
					.readTimeout(1,TimeUnit.DAYS)
					.connectTimeout(1,TimeUnit.DAYS)
					.build()
			return Retrofit.Builder()
					.addConverterFactory(GsonConverterFactory.create(gson))
					.baseUrl(Base_URL)
					.client(client)
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
					.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
					.addConverterFactory(GsonConverterFactory.create(gson))
					.baseUrl(Base_URL)
					.build()
		}

		fun createService(): ApiFaceAuthInterface {
			return builder().create(ApiFaceAuthInterface::class.java)
		}
	}

}