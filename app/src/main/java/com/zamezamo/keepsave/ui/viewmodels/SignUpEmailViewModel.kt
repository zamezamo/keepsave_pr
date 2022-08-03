package com.zamezamo.keepsave.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpEmailViewModel: ViewModel() {

    companion object {

        private const val REGEX_EMAIL_PATTERN =
            "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)" +
                    "*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]" +
                    "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
                    "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])" +
                    "?|\\[(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])\\.){3}" +
                    "(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]" +
                    ":(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]" +
                    "|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+))"

    }

    private val _mutableSignUpState = MutableLiveData<SignUpResult>()

    val signUpState: LiveData<SignUpResult> get() = _mutableSignUpState

    fun checkEmail(email: String) {

        if (email.contains(Regex(REGEX_EMAIL_PATTERN))) {
            _mutableSignUpState.value = SignUpResult.Success
        } else
            _mutableSignUpState.value = SignUpResult.Error

    }

    fun setSignUpResultToNotChecked() {
        _mutableSignUpState.value = SignUpResult.NotChecked
    }

    sealed class SignUpResult{

        object NotChecked: SignUpResult()
        object Success : SignUpResult()
        object Error : SignUpResult()

    }

}