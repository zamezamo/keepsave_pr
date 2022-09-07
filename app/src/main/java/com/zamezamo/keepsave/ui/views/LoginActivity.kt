package com.zamezamo.keepsave.ui.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.zamezamo.keepsave.R
import com.zamezamo.keepsave.data.Database
import com.zamezamo.keepsave.domain.User

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

    }

    override fun onStart() {

        super.onStart()

        Database.initAuth()

        val currentUser = Database.auth.currentUser

        if (currentUser == null)
            openFragment(SignInFragment.newInstance())
        else if (currentUser.isEmailVerified) {
            openIdeasActivity()
            finish()
        } else
            openFragment(EmailVerifyFragment.newInstance())

    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerLoginActivity, fragment)
            .commitNow()
    }

    private fun openIdeasActivity() {
        val intent = Intent(this, IdeasActivity::class.java)
        startActivity(intent)
        finish()
    }

}