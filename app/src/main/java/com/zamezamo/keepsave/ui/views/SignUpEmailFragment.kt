package com.zamezamo.keepsave.ui.views

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.zamezamo.keepsave.R
import com.zamezamo.keepsave.ui.viewmodels.SignUpEmailViewModel

class SignUpEmailFragment : Fragment() {

    companion object {
        fun newInstance() = SignUpEmailFragment()

        private const val TAG = "SignUpEmailFragment"
    }

    private var buttonSignUpEmail: Button? = null

    private var editTextSignUpEmail: TextInputEditText? = null

    private val viewModel: SignUpEmailViewModel by viewModels()

    private lateinit var inputEmail: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()

        viewModel.setSignUpResultToNotChecked()

        viewModel.signUpState.observe(viewLifecycleOwner) { signUpResult ->

            signUpResult ?: return@observe

            when (signUpResult) {

                SignUpEmailViewModel.SignUpResult.Success -> showEmailConfirmDialog()

                SignUpEmailViewModel.SignUpResult.Error -> showEmailError()

                SignUpEmailViewModel.SignUpResult.NotChecked -> {
                    Log.d(TAG, "Email filling not checked")
                }

            }

        }

    }

    override fun onDestroyView() {

        buttonSignUpEmail = null

        editTextSignUpEmail = null

        viewModel.signUpState.removeObservers(viewLifecycleOwner)

        super.onDestroyView()

    }

    private fun initViews(view: View) {

        buttonSignUpEmail = view.findViewById(R.id.buttonSignUpEmail)

        editTextSignUpEmail = view.findViewById(R.id.editTextSignUpEmail)

    }

    private fun setupListeners() {
        buttonSignUpEmail?.setOnClickListener {
            checkEmail()
            hideKeyboard()
        }
    }

    private fun checkEmail() {

        inputEmail = editTextSignUpEmail?.text.toString()

        viewModel.checkEmail(email = inputEmail)

    }

    private fun showEmailError() {
        editTextSignUpEmail?.error = getString(R.string.emailSignUpError)
    }

    private fun showEmailConfirmDialog() {

        val inputEmail: String = editTextSignUpEmail?.text.toString()

        hideKeyboard()

        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.emailConfirmDialogTitle) + inputEmail)
            .setMessage(getString(R.string.emailConfirmDialogMessage))
            .setNegativeButton(getString(R.string.emailConfirmDialogNegativeButton)) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(getString(R.string.emailConfirmDialogPositiveButton)) { dialog, _ ->
                inflateSignUpPasswordFragment()
                dialog.cancel()
            }

        dialog.show()
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun inflateSignUpPasswordFragment() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.containerSignUpFragment, SignUpPasswordFragment.newInstance(inputEmail))
            .addToBackStack(TAG)
            .commit()
    }


}