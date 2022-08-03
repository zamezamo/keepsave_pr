package com.zamezamo.keepsave.ui.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.zamezamo.keepsave.R
import com.zamezamo.keepsave.ui.viewmodels.SignInViewModel

class SignInFragment() : Fragment() {

    companion object {
        fun newInstance() = SignInFragment()

        private const val TAG = "SignInFragment"
    }

    private var buttonSignIn: Button? = null
    private var buttonSignUp: TextView? = null

    private var editTextEmail: TextInputEditText? = null
    private var editTextPassword: TextInputEditText? = null

    private val viewModel: SignInViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()

        viewModel.setSignInStateToNothing()

        viewModel.signInState.observe(viewLifecycleOwner) { signInResult ->

            signInResult ?: return@observe

            when (signInResult) {
                SignInViewModel.SignInResult.SignInAttempt -> {
                    setLoading(true)
                }
                SignInViewModel.SignInResult.Success -> {
                    requireActivity().recreate()
                }
                SignInViewModel.SignInResult.Error.Email -> {
                    showEmailError()
                }
                SignInViewModel.SignInResult.Error.Password -> {
                    showPasswordError()
                }
                SignInViewModel.SignInResult.Error.SignIn -> {
                    setLoading(false)
                    showAuthError()
                }
                SignInViewModel.SignInResult.Nothing -> {
                    Log.d(TAG, "Fragment recreated")
                }
            }

        }

    }


    override fun onDestroyView() {

        buttonSignIn = null
        buttonSignUp = null

        editTextEmail = null
        editTextPassword = null

        viewModel.signInState.removeObservers(viewLifecycleOwner)

        super.onDestroyView()

    }

    private fun initViews(view: View) {

        buttonSignIn = view.findViewById(R.id.buttonSignIn)
        buttonSignUp = view.findViewById(R.id.buttonSignUp)

        editTextEmail = view.findViewById(R.id.editTextSignInEmail)
        editTextPassword = view.findViewById(R.id.editTextSignInPassword)

    }

    private fun setupListeners() {
        buttonSignIn?.setOnClickListener {
            tryToSignIn()
            hideKeyboard()
        }
        buttonSignUp?.setOnClickListener {
            openSignUpFragment()
            hideKeyboard()
        }
    }

    private fun setLoading(loading: Boolean) {

        buttonSignIn?.isEnabled = !loading
        buttonSignUp?.isEnabled = !loading

        editTextEmail?.isEnabled = !loading
        editTextPassword?.isEnabled = !loading

    }

    private fun showEmailError() {
        editTextEmail?.error = getString(R.string.emailSignInError)
    }

    private fun showPasswordError() {
        editTextPassword?.error = getString(R.string.passwordSignInError)
    }

    private fun showAuthError() {
        Toast.makeText(context, getString(R.string.signInError), Toast.LENGTH_SHORT).show()
    }

    private fun tryToSignIn() {

        val inputEmail: String = editTextEmail?.text?.toString().orEmpty()
        val inputPassword: String = editTextPassword?.text.toString()

        viewModel.tryToSignIn(email = inputEmail, password = inputPassword)

    }

    private fun openSignUpFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.containerLoginActivity, SignUpFragment.newInstance(), TAG)
            .addToBackStack(TAG)
            .commit()
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

}