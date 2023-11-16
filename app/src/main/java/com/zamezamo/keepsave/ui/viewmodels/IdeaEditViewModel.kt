package com.zamezamo.keepsave.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zamezamo.keepsave.data.Database
import com.zamezamo.keepsave.domain.Idea

class IdeaEditViewModel : ViewModel() {

    private val _mutableSavingIdeaState = MutableLiveData<SavingIdeaResult>()

    val savingIdeaState: LiveData<SavingIdeaResult> get() = _mutableSavingIdeaState


    fun checkForChangesAndSave(ideaBeforeSaving: Idea, ideaToSave: Idea) {
        Log.d("ideaBeforeSaving", "$ideaBeforeSaving")
        Log.d("ideaAfterSaving", "$ideaToSave")
        if (ideaToSave.title.isNullOrEmpty())
            _mutableSavingIdeaState.value = SavingIdeaResult.ErrorFilling
        else if (ideaToSave == ideaBeforeSaving) {
            _mutableSavingIdeaState.value = SavingIdeaResult.ErrorChecking
        } else {
            _mutableSavingIdeaState.value = SavingIdeaResult.Saved
            if (ideaBeforeSaving.title.isNullOrEmpty())
                Database.addIdeaToDB(ideaToSave)
            else
                Database.editIdeaInDB(ideaToSave)
        }

    }

    sealed class SavingIdeaResult {
        object Saved : SavingIdeaResult()
        object ErrorFilling : SavingIdeaResult()
        object ErrorChecking : SavingIdeaResult()
    }

}