package software.yesaya.passport.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonElement
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray
import software.yesaya.passport.internal.AccessToken
import software.yesaya.passport.repo.AppRepo
import software.yesaya.passport.util.ResponseResult


class LoginViewModel(val appRepo: AppRepo): ViewModel() {
    private var mLoginStatus: MutableLiveData<AccessToken> = MutableLiveData()
    private val mCompositeDisposable = CompositeDisposable()
    private val mResponseResult:MutableLiveData<ResponseResult> = MutableLiveData()

    fun callLoginStatus(email:String,password: String){
        mCompositeDisposable.add(appRepo.loginStatus(email,password)
            .doOnSubscribe {
                mResponseResult.postValue(ResponseResult.LOADING)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({s ->
                Log.d("TESTINGG Response $s","")
                mLoginStatus.value = s
                mResponseResult.postValue(ResponseResult.LOADED)
            },
            { e ->
//                progressStatus.postValue(AppConstants.ERROR)
                Log.d("TESTING Error is: ${e.printStackTrace()}","")
                mResponseResult.postValue(ResponseResult.ERROR)
            },
            { Log.d("onComplete","") }))
    }

    fun getLoginStatus():MutableLiveData<AccessToken> {
        return mLoginStatus
    }


}