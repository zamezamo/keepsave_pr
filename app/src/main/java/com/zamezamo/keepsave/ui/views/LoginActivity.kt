package com.zamezamo.keepsave.ui.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.zamezamo.keepsave.R

class LoginActivity : AppCompatActivity() {

    companion object {
        lateinit var auth: FirebaseAuth
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

    }

    override fun onStart() {

        super.onStart()

        auth = FirebaseAuth.getInstance()
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