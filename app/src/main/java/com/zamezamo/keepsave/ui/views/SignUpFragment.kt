package com.zamezamo.keepsave.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.zamezamo.keepsave.R

class SignUpFragment: Fragment() {

    companion object {
        fun newInstance() = SignUpFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inflateSignUpEmailFragment()

    }

    private fun inflateSignUpEmailFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.containerSignUpFragment, SignUpEmailFragment.newInstance())
            .commit()
    }

}