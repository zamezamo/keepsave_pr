package com.zamezamo.keepsave.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.zamezamo.keepsave.data.Database
import com.zamezamo.keepsave.ui.views.LoginActivity

class SignUpPasswordViewModel: ViewModel() {

    companion object {

        private const val REGEX_PASSWORD_PATTERN =
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{6,}\$"

        private const val TAG = "SignUpPasswordViewModel"

    }

    private val _mutableSignUpState = MutableLiveData<SignUpResult>()

    val signUpState: LiveData<SignUpResult> get() = _mutableSignUpState

    fun checkPasswordsAndSignUp(pass1: String, pass2: String, email: String) {

        if (!(pass1.contains(Regex(REGEX_PASSWORD_PATTERN))))
            _mutableSignUpState.value = SignUpResult.Error.PasswordFilling
        else if (pass1 != pass2)
            _mutableSignUpState.value = SignUpResult.Error.PasswordsAreNotSame
        else {
            _mutableSignUpState.value = SignUpResult.SignUpAttempt
            initCreatingAccount(email = email, password = pass1)
        }

    }

    private fun initCreatingAccount(email: String, password: String) {

        Database.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "createUserWithEmail:success")
                } else {
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    _mutableSignUpState.value = SignUpResult.Error.SignUp
                }
            }

    }

    sealed class SignUpResult {

        object SignUpAttempt : SignUpResult()

        object SignUpSuccess : SignUpResult()

        sealed class Error : SignUpResult() {

            object SignUp : Error()
            object PasswordFilling : Error()
            object PasswordsAreNotSame : Error()

        }

    }

}