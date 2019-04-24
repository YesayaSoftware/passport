package software.yesaya.passport.network

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import software.yesaya.passport.internal.TokenManager
import java.io.IOException

class CustomAuthenticator private constructor(private val tokenManager: TokenManager) : Authenticator {


    @Throws(IOException::class)
    override fun authenticate(route: Route, response: Response): Request? {

        if (responseCount(response) >= 3) {
            return null
        }

        val token = tokenManager.token

        val service = RetrofitBuilder.createService(ApiService::class.java)
        val call = service.refresh(token.refreshToken!! + "a")
        val res = call.execute()

        return if (res.isSuccessful) {
            val newToken = res.body()
            tokenManager.saveToken(newToken!!)

            response.request().newBuilder().header("Authorization", "Bearer " + res.body()!!.accessToken!!)
                .build()
        } else {
            null
        }
    }

    private fun responseCount(response: Response?): Int {
        var result = 1
        while ((response!!.priorResponse()) != null) {
            result++
        }
        return result
    }

    companion object {
        private var INSTANCE: CustomAuthenticator? = null

        @Synchronized
        internal fun getInstance(tokenManager: TokenManager): CustomAuthenticator {
            if (INSTANCE == null) {
                INSTANCE = CustomAuthenticator(tokenManager)
            }

            return INSTANCE as CustomAuthenticator
        }
    }
}