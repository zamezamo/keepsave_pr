package com.zamezamo.keepsave.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val applicationContext: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        else -> throw IllegalArgumentException("$modelClass is not registered ViewModel")
    } as T
}