package com.zamezamo.keepsave.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zamezamo.keepsave.ui.views.LoginActivity

class EmailVerifyViewModel : ViewModel() {

    companion object {
        private const val TAG = "EmailVerifyViewModel"
    }

    private val _mutableEmailVerifyState =
        MutableLiveData<EmailVerifyState>(EmailVerifyState.NotSent)

    val emailVerifyState: LiveData<EmailVerifyState> get() = _mutableEmailVerifyState

    private val user = LoginActivity.auth.currentUser

    fun sendEmail() {

        user!!.sendEmailVerification().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Log.d(TAG, "Email sent.")
                _mutableEmailVerifyState.value = EmailVerifyState.Sent
            } else {
                _mutableEmailVerifyState.value = EmailVerifyState.Error
            }

        }

    }

    sealed class EmailVerifyState {

        object NotSent : EmailVerifyState()
        object Sent : EmailVerifyState()
        object Error : EmailVerifyState()

    }

}