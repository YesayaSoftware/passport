package software.yesaya.passport.internal

import com.squareup.moshi.Json

class AccessToken {
    @Json(name = "token_type")
    lateinit var tokenType: String
    @Json(name = "expires_in")
    var expiresIn: Int = 0
    @Json(name = "access_token")
    var accessToken: String? = null
    @Json(name = "refresh_token")
    var refreshToken: String? = null
}