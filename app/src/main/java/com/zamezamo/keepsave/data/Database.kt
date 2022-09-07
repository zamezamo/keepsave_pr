package com.zamezamo.keepsave.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zamezamo.keepsave.domain.Idea
import com.zamezamo.keepsave.domain.User

object Database {

    lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase

    private const val TAG = "data.Database"

    fun initDatabase() {

        database =
            Firebase.database("https://keepsave-pr-default-rtdb.europe-west1.firebasedatabase.app/")

    }

    fun initAuth() {

        auth = FirebaseAuth.getInstance()

    }

    fun createUserInDBIfNotExists() {

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

        val key = database.getReference("users")
            .child("${auth.uid}").child("ideas").push().key
        val dbRef = database.getReference("users")
            .child("${auth.uid}").child("ideas")

        dbRef.child("$key").setValue(idea).addOnSuccessListener {
            Log.d(TAG, "adding idea complete")
        }.addOnFailureListener {
            Log.e(TAG, "adding idea failed")
        }

    }

    fun removeIdeaFromDB(idea: Idea) {

    }

}