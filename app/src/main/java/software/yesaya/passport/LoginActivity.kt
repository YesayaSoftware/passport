package software.yesaya.passport

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.loader
import kotlinx.android.synthetic.main.activity_login.til_email
import kotlinx.android.synthetic.main.activity_login.til_password
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import software.yesaya.passport.internal.AccessToken
import software.yesaya.passport.internal.TokenManager
import software.yesaya.passport.internal.Utils
import software.yesaya.passport.network.ApiService
import software.yesaya.passport.network.RetrofitBuilder

class LoginActivity :  AppCompatActivity() {
    private val TAG = "LoginActivity"

    private var service: ApiService? = null
    var call: Call<AccessToken>? = null
    var validator: AwesomeValidation? = null
    var tokenManager: TokenManager? = null

    var formContainer: LinearLayout? = null
    //var facebookManager : FacebookManager = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        service = RetrofitBuilder.createService(ApiService::class.java)
        validator = AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT)
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", Context.MODE_PRIVATE))

        setupRules()

        if (tokenManager?.getTokens()?.accessToken != null) {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }

        btn_login.setOnClickListener {
            val email = til_email?.editText?.text.toString()
            val password = til_password?.editText?.text.toString()

            til_email?.error = null
            til_password?.error = null

            validator?.clear()

            if (validator?.validate()!!) {
                showLoading()

                call = service!!.login(email, password)

                call!!.enqueue(object : Callback<AccessToken> {
                    override fun onResponse(call: Call<AccessToken>, response: Response<AccessToken>) {

                        Log.w(TAG, "onResponse: $response")

                        if (response.isSuccessful) {
                            Log.w(TAG, "onResponse: " + response.body())
                            tokenManager?.saveToken(response.body()!!)
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {
                            handleErrors(response.errorBody()!!, response.code())
                            showForm()
                        }
                    }

                    override fun onFailure(call: Call<AccessToken>, t: Throwable) {
                        Log.w(TAG, "onFailure: " + t.message)
                        showForm()
                    }
                })
            }
        }

        go_to_register.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }

    private fun setupRules() {
        validator?.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email)
        validator?.addValidation(this, R.id.til_password, "[a-zA-Z0-9]{6,}", R.string.err_password)
    }

    private fun handleErrors(response: ResponseBody, code : Int) {
        val apiError = Utils.convertErrors(response)

        if (code == 422) {
            for (error in apiError?.errors?.entries!!) {
                if (error.key == "email") {
                    til_email?.error = error.value[0]
                }
                if (error.key == "password") {
                    til_password?.error = error.value[0]
                }
            }
        } else if (code == 401) {
            til_email?.error = apiError?.message
        }
    }

    private fun showLoading() {
        TransitionManager.beginDelayedTransition(container)
        formContainer?.visibility = View.GONE
        loader.visibility = View.VISIBLE
    }

    private fun showForm() {
        TransitionManager.beginDelayedTransition(container)
        formContainer?.visibility = View.VISIBLE
        loader.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (call != null) {
            call!!.cancel()
            call = null
        }
    }
}
