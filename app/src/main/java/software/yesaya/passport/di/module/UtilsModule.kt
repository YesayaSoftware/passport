package software.yesaya.passport.di.module

import android.app.Application
import software.yesaya.passport.repo.AppRepo
import software.yesaya.passport.viewmodels.ViewModelFactory
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import software.yesaya.passport.network.ApiService
import javax.inject.Singleton



/**
 * Created by ${Chandan} on 30-05-2018.
 */
@Module
class UtilsModule {

    @Provides
    @Singleton
    fun provideRepo(context: Application,apiCallInterface: ApiService): AppRepo {
        return AppRepo(context,apiCallInterface)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.setLenient().create()
    }

    @Provides
    @Singleton
    fun getApiCallInterface(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
                .baseUrl("http://dev.yesaya.software/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    /*@Provides
    @Singleton
    fun getRequestHeader(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(Interceptor {
            val original: Request = it.request()
            val request = original.newBuilder()
//                    .addHeader("Authorization","Client-ID 85773d89a536a2f52f525b051eee6ea5b7ef7c202ccc531a8ccd5fe14c71e5fe")
//                    .addHeader("Authorization","563492ad6f91700001000001b57fd74e0c2043eb9eda5679173f158e")
                    .build()
            return@Interceptor it.proceed(request)
        })
                .connectTimeout(100,TimeUnit.SECONDS)
                .writeTimeout(100,TimeUnit.SECONDS)
                .readTimeout(300,TimeUnit.SECONDS)
        return builder.build()

    }*/

    @Provides
    @Singleton
    fun provideViewModelFactory(appRepo: AppRepo): ViewModelFactory {
        return ViewModelFactory(appRepo)
    }

}