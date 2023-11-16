package com.zamezamo.keepsave.ui.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zamezamo.keepsave.R

class IdeasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ideas)
        setSupportActionBar(findViewById(R.id.topAppBarIdeasActivity))
        openFragment(IdeasFragment.newInstance())

    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerIdeasActivity, fragment)
            .commitNow()
    }

}