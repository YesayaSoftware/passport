package software.yesaya.passport.repo

import android.content.Context
import com.google.gson.JsonElement
import io.reactivex.Observable
import org.json.JSONArray
import software.yesaya.passport.internal.AccessToken
import software.yesaya.passport.network.ApiService

/**
 * Created by ${Chandan} on 31-07-2018.
 */
class AppRepo(var context: Context, var apiCallInterface: ApiService) {
    fun loginStatus(email:String,password: String): Observable<AccessToken> {
        return apiCallInterface.login(email,password)
    }
}