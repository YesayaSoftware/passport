package software.yesaya.passport.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import software.yesaya.passport.repo.AppRepo
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
/**
 * Created by ${Chandan} on 04-06-2018.
 */
class ViewModelFactory @Inject constructor(private val appRepo: AppRepo) :  ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(appRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class ")
    }
}