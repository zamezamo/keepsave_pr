package com.zamezamo.keepsave.ui.views

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.textfield.TextInputEditText
import com.zamezamo.keepsave.R
import com.zamezamo.keepsave.ui.viewmodels.SignUpPasswordViewModel

class SignUpPasswordFragment(private val email: String) : Fragment() {

    companion object {
        fun newInstance(email: String) = SignUpPasswordFragment(email)
    }

    private var buttonSignUpCreate: Button? = null

    private var editTextSignUpPassword: TextInputEditText? = null
    private var editTextSignUpPasswordRepeat: TextInputEditText? = null

    private val viewModel: SignUpPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()

        viewModel.signUpState.observe(viewLifecycleOwner) { signUpResult ->

            signUpResult ?: return@observe

            when (signUpResult) {

                SignUpPasswordViewModel.SignUpResult.SignUpAttempt -> setLoading(true)

                SignUpPasswordViewModel.SignUpResult.SignUpSuccess -> inflateEmailVerifyFragment()

                SignUpPasswordViewModel.SignUpResult.Error.PasswordFilling -> showPasswordFillingError()

                SignUpPasswordViewModel.SignUpResult.Error.PasswordsAreNotSame -> showPasswordsAreNotSame()

                SignUpPasswordViewModel.SignUpResult.Error.SignUp -> {
                    showSignUpError()
                    setLoading(false)
                }

            }

        }

    }

    private fun initViews(view: View) {

        buttonSignUpCreate = view.findViewById(R.id.buttonSignUpCreate)

        editTextSignUpPassword = view.findViewById(R.id.editTextSignUpPassword)
        editTextSignUpPasswordRepeat = view.findViewById(R.id.editTextSignUpPasswordRepeat)

    }

    private fun setupListeners() {
        buttonSignUpCreate?.setOnClickListener {
            checkPasswords()
            hideKeyboard()
        }
    }

    private fun setLoading(loading: Boolean) {

        buttonSignUpCreate?.isEnabled = !loading

        editTextSignUpPassword?.isEnabled = !loading
        editTextSignUpPasswordRepeat?.isEnabled = !loading

    }

    private fun checkPasswords() {

        val inputPassword: String = editTextSignUpPassword?.text.toString()
        val inputPasswordRepeat: String = editTextSignUpPasswordRepeat?.text.toString()

        viewModel.checkPasswordsAndSignUp(pass1 = inputPassword, pass2 = inputPasswordRepeat, email = email)

    }

    private fun showPasswordFillingError() {
        editTextSignUpPassword?.error = getString(R.string.passwordSignUpFillingError)
    }

    private fun showPasswordsAreNotSame() {
        editTextSignUpPasswordRepeat?.error = getString(R.string.passwordsSignUpAreNotSameError)
    }

    private fun showSignUpError(){
        Toast.makeText(context, getString(R.string.signUpError), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {

        buttonSignUpCreate = null

        editTextSignUpPassword = null
        editTextSignUpPasswordRepeat = null

        viewModel.signUpState.removeObservers(viewLifecycleOwner)

        super.onDestroyView()

    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun inflateEmailVerifyFragment() {

        val countFragmentsInBackStack = requireActivity().supportFragmentManager.backStackEntryCount

        for (i in 1..countFragmentsInBackStack)
            requireActivity().supportFragmentManager.popBackStack()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.containerLoginActivity, EmailVerifyFragment.newInstance())
            .commit()
    }

}