package software.yesaya.passport.network

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import software.yesaya.passport.internal.AccessToken

interface ApiService {

    @POST("register")
    @FormUrlEncoded
    fun register(@Field("name") name: String, @Field("email") email: String, @Field("password") password: String): Call<AccessToken>

    @POST("login")
    @FormUrlEncoded
    fun login(@Field("email") username: String, @Field("password") password: String): Call<AccessToken>

    @POST("social_auth")
    @FormUrlEncoded
    fun socialAuth(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("provider") provider: String,
        @Field("provider_user_id") providerUserId: String
    ): Call<AccessToken>

    @POST("refresh")
    @FormUrlEncoded
    fun refresh(@Field("refresh_token") refreshToken: String): Call<AccessToken>
}