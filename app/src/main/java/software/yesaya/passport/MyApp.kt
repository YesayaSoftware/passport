package software.yesaya.passport

import android.app.Application

import com.facebook.stetho.Stetho
import software.yesaya.passport.di.AppComponent

class MyApp : Application() {
    companion object {
        @JvmStatic lateinit var appComponent: AppComponent
    }
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        appComponent = AppComponent.create(this)
    }
}