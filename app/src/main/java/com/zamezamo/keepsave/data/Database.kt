package com.zamezamo.keepsave.data

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.zamezamo.keepsave.domain.Idea
import com.zamezamo.keepsave.domain.User

object Database {

    private const val TAG = "data.Database"

    private const val DB_URL = "https://keepsave-pr-default-rtdb.europe-west1.firebasedatabase.app/"

    fun createUserInDBIfNotExists() {

        val auth = Firebase.auth
        val database =
            Firebase.database(DB_URL)

        val dbRef = database.getReference("users")
        dbRef.child("${auth.uid}").get()
            .addOnSuccessListener {
                if (it.value != null) {
                    Log.i(TAG, "Reading user info at users/${auth.uid} complete, ${it.value}")
                } else {
                    val user = User(email = auth.currentUser?.email.toString())
                    dbRef.child("${auth.uid}").setValue(user)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Error reading user info at /users/${auth.uid}")
            }

    }

    fun addIdeaToDB(idea: Idea) {

        val auth = Firebase.auth
        val database =
            Firebase.database(DB_URL)

        val key = database.getReference("users")
            .child("${auth.uid}").child("ideas").push().key
        val dbRef = database.getReference("users")
            .child("${auth.uid}").child("ideas")

        idea.id = key

        dbRef.child("$key").setValue(idea).addOnSuccessListener {
            Log.d(TAG, "adding idea complete")
        }.addOnFailureListener {
            Log.e(TAG, "adding idea failed")
        }

    }

    fun deleteIdeasFromDB(selected: List<Idea>?) {

        val auth = Firebase.auth
        val database =
            Firebase.database(DB_URL)

        if (selected != null) {
            val dbRef = database.getReference("users")
                .child("${auth.uid}").child("ideas")
            val childUpdates = hashMapOf<String, Any?>()
            for (idea in selected) {
                childUpdates["/${idea.id}"] = null
            }

            dbRef.updateChildren(childUpdates).addOnSuccessListener {
                Log.d(TAG, "deleting ideas completed")
            }.addOnFailureListener {
                Log.d(TAG, "deleting ideas failed")
            }
        }

    }

    fun editIdeaInDB(idea: Idea) {

        val auth = Firebase.auth
        val database =
            Firebase.database(DB_URL)

        val dbRef = database.getReference("users")
            .child("${auth.uid}").child("ideas").child("${idea.id}")

        dbRef.setValue(idea)

    }

}