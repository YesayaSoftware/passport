package software.yesaya.passport.internal
import android.content.SharedPreferences

class TokenManager private constructor(private val prefs: SharedPreferences) {
    private val editor: SharedPreferences.Editor

    val token: AccessToken
        get() {
            val token = AccessToken()
            token.accessToken = prefs.getString("ACCESS_TOKEN", null)
            token.refreshToken = prefs.getString("REFRESH_TOKEN", null)
            return token
        }

    init {
        this.editor = prefs.edit()
    }

    fun saveToken(token: AccessToken) {
        editor.putString("ACCESS_TOKEN", token.accessToken).commit()
        editor.putString("REFRESH_TOKEN", token.refreshToken).commit()
    }

    fun deleteToken() {
        editor.remove("ACCESS_TOKEN").commit()
        editor.remove("REFRESH_TOKEN").commit()
    }

    companion object {

        private var INSTANCE: TokenManager? = null

        @Synchronized
        internal fun getInstance(prefs: SharedPreferences): TokenManager {
            if (INSTANCE == null) {
                INSTANCE = TokenManager(prefs)
            }
            return INSTANCE as TokenManager
        }
    }

    fun getTokens() : AccessToken {
        val token = AccessToken()
        token.accessToken = prefs.getString("ACCESS_TOKEN", null)
        token.refreshToken = prefs.getString("REFRESH_TOKEN", null)
        return token
    }
}