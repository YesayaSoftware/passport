package software.yesaya.passport.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import software.yesaya.passport.BuildConfig
import software.yesaya.passport.internal.TokenManager

object RetrofitBuilder {
    private const val BASE_URL = "http://dev.yesaya.software/api/"

    private val client = buildClient()
    val retrofit = buildRetrofit(client)

    private fun buildClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request = chain.request()

                val builder = request.newBuilder()
                    .addHeader("Accept", "application/json")
                    .addHeader("Connection", "close")

                request = builder.build()

                chain.proceed(request)
            }

        if (BuildConfig.DEBUG)
            builder.addNetworkInterceptor(StethoInterceptor())

        return builder.build()

    }

    private fun buildRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    fun <T> createService(service: Class<T>): T {
        return retrofit.create(service)
    }

    fun <T> createServiceWithAuth(service: Class<T>, tokenManager: TokenManager): T {

        val newClient = client.newBuilder().addInterceptor { chain ->
            var request = chain.request()

            val builder = request.newBuilder()

            if (tokenManager.token.accessToken != null) {
                builder.addHeader("Authorization", "Bearer " + tokenManager.token.accessToken!!)
            }
            request = builder.build()
            chain.proceed(request)
        }.authenticator(CustomAuthenticator.getInstance(tokenManager)).build()

        val newRetrofit = retrofit.newBuilder().client(newClient).build()
        return newRetrofit.create(service)

    }

    fun getRetrofits(): Retrofit {
        return retrofit
    }
}