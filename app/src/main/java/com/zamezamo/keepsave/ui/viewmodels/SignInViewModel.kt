package com.zamezamo.keepsave.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zamezamo.keepsave.ui.views.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignInViewModel : ViewModel() {

    companion object {
        private const val TAG = "SignInViewModel"
    }

    private val _mutableSignInState = MutableLiveData<SignInResult>(SignInResult.Nothing)

    val signInState: LiveData<SignInResult> get() = _mutableSignInState

    fun tryToSignIn(email: String, password: String) {

        viewModelScope.launch {

            withContext(Dispatchers.Main) {

                _mutableSignInState.value = when {
                    email.isEmpty() -> SignInResult.Error.Email
                    password.length < 6 -> SignInResult.Error.Password
                    else -> {
                        initAuth(email, password)
                        SignInResult.SignInAttempt
                    }
                }

            }

        }

    }

    private fun initAuth(email: String, password: String) {
        LoginActivity.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "signInWithEmail:success")
                _mutableSignInState.value = SignInResult.Success
            } else {
                _mutableSignInState.value = SignInResult.Error.SignIn
                Log.w(TAG, "signInWithEmail:failure", task.exception)
            }
        }
    }

    fun setSignInStateToNothing(){
        _mutableSignInState.value = SignInResult.Nothing
    }

    sealed class SignInResult {

        object Success : SignInResult()
        object SignInAttempt : SignInResult()
        object Nothing: SignInResult()

        sealed class Error : SignInResult() {

            object Email : Error()
            object Password : Error()
            object SignIn : Error()

        }

    }

}