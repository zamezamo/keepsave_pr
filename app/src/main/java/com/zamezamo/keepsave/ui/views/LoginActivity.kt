package com.zamezamo.keepsave.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zamezamo.keepsave.R

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val auth = Firebase.auth

        val currentUser = auth.currentUser

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