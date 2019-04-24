package software.yesaya.passport

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import okhttp3.ResponseBody
import retrofit2.Call
import android.util.Log
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Callback
import retrofit2.Response
import software.yesaya.passport.internal.AccessToken
import software.yesaya.passport.internal.TokenManager
import software.yesaya.passport.internal.Utils
import software.yesaya.passport.network.ApiService
import software.yesaya.passport.network.RetrofitBuilder
import android.widget.LinearLayout




class RegisterActivity : AppCompatActivity() {

    private val TAG = "RegisterActivity"

    private var service: ApiService? = null
    var call: Call<AccessToken>? = null
    var validator: AwesomeValidation? = null
    var tokenManager: TokenManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        service = RetrofitBuilder.createService(ApiService::class.java)
        validator = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", Context.MODE_PRIVATE))

        setupRules()

        if (tokenManager?.getTokens()?.accessToken != null) {
            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
            finish()
        }

        btn_register.setOnClickListener {
            val name = til_name.editText?.text.toString()
            val email = til_email?.editText?.text.toString()
            val password = til_password?.editText?.text.toString()

            til_name?.error = null
            til_email?.error = null
            til_password?.error = null

            validator?.clear()

            if (validator?.validate()!!) {
                call = service!!.register(name, email, password)

                call!!.enqueue(object : Callback<AccessToken> {
                    override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {

                        Log.w(TAG, "onResponse: $response")

                        if (response.isSuccessful) {
                            Log.w(TAG, "onResponse: " + response.body())
                            tokenManager?.saveToken(response.body()!!)
                            startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                            finish()
                        } else {
                            handleErrors(response.errorBody()!!)
                        }
                    }

                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        Log.w(TAG, "onFailure: " + t.message)
                    }
                })
            }
        }
    }

    private fun handleErrors(response: ResponseBody) {
        val apiError = Utils.convertErrors(response)

        for (error in apiError?.errors?.entries!!) {
            if (error.key == "name") {
                til_name?.error = error.value[0]
            }
            if (error.key == "email") {
                til_email?.error = error.value[0]
            }
            if (error.key == "password") {
                til_password?.error = error.value[0]
            }
        }
    }

    private fun setupRules() {
        validator?.addValidation(this, R.id.til_name, RegexTemplate.NOT_EMPTY, R.string.err_name)
        validator?.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email)
        validator?.addValidation(this, R.id.til_password, "[a-zA-Z0-9]{6,}", R.string.err_password)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (call != null) {
            call!!.cancel()
            call = null
        }
    }
}

