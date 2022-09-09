package com.zamezamo.keepsave.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zamezamo.keepsave.data.Database

class EmailVerifyViewModel : ViewModel() {

    companion object {
        private const val TAG = "EmailVerifyViewModel"
    }

    private val _mutableEmailVerifyState =
        MutableLiveData<EmailVerifyState>(EmailVerifyState.NotSent)

    val emailVerifyState: LiveData<EmailVerifyState> get() = _mutableEmailVerifyState

    fun sendEmailAndCreateUserInDBIfNotExists() {

        val auth = Firebase.auth

        auth.currentUser!!.sendEmailVerification().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                Log.d(TAG, "Email sent.")
                _mutableEmailVerifyState.value = EmailVerifyState.Sent
                Database.createUserInDBIfNotExists()
            } else {
                Log.d(TAG, "Email not sent. " + task.exception)
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