package software.yesaya.passport.di

import android.app.Application
import software.yesaya.passport.di.module.AppModule
import software.yesaya.passport.di.module.UtilsModule
import dagger.Component
import software.yesaya.passport.LoginActivity
import javax.inject.Singleton

/**
 * Created by ${Chandan} on 30-05-2018.
 */
@Singleton
@Component(
        modules = [(AppModule::class), (UtilsModule::class)]
)
interface AppComponent {
    fun inject(loginActivity: LoginActivity)

    companion object Factory{
        fun create(app: Application): AppComponent {
            return DaggerAppComponent.builder().
                    appModule(AppModule(app)).
                    utilsModule(UtilsModule()).
                    build()
        }
    }
}