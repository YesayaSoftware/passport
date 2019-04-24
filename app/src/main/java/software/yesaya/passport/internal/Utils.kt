package software.yesaya.passport.internal

import okhttp3.ResponseBody
import retrofit2.Converter
import software.yesaya.passport.network.RetrofitBuilder

object Utils {
    fun convertErrors(response: ResponseBody): ApiError? {
        val converter : Converter<ResponseBody, ApiError> =
            RetrofitBuilder.getRetrofits().responseBodyConverter(ApiError::class.java, arrayOfNulls<Annotation>(0))

        var apiError: ApiError? = null

        try {
            apiError = converter.convert(response)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return apiError
    }
}