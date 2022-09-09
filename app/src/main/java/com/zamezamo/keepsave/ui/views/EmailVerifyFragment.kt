package com.zamezamo.keepsave.ui.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zamezamo.keepsave.R
import com.zamezamo.keepsave.ui.viewmodels.EmailVerifyViewModel

class EmailVerifyFragment : Fragment() {

    companion object {

        fun newInstance() = EmailVerifyFragment()

    }

    private var buttonConfirmEmail: Button? = null

    private var textViewEmail: TextView? = null

    private val viewModel: EmailVerifyViewModel by viewModels()

    private val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_email_verify, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupListeners()

        viewModel.emailVerifyState.observe(viewLifecycleOwner) { emailVerifyState ->

            emailVerifyState ?: return@observe

            when (emailVerifyState) {

                EmailVerifyViewModel.EmailVerifyState.NotSent -> {
                    viewModel.sendEmailAndCreateUserInDBIfNotExists()
                }

                EmailVerifyViewModel.EmailVerifyState.Sent -> {
                    setLoading(false)
                }

                EmailVerifyViewModel.EmailVerifyState.Error -> {
                    showEmailError()
                    setLoading(false)
                }

            }

        }


    }

    override fun onDestroyView() {

        super.onDestroyView()

        buttonConfirmEmail = null

        textViewEmail = null

    }

    private fun initViews(view: View) {

        buttonConfirmEmail = view.findViewById(R.id.buttonConfirmEmail)

        textViewEmail = view.findViewById(R.id.textViewEmail)

        textViewEmail?.text = auth.currentUser?.email ?: ""

    }


    private fun setupListeners() {

        setLoading(true)

        buttonConfirmEmail?.setOnClickListener {

            auth.signOut()

            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)

            requireActivity().finish()

        }

    }

    private fun setLoading(loading: Boolean) {
        buttonConfirmEmail?.isEnabled = !loading
    }

    private fun showEmailError() {
        buttonConfirmEmail?.text = getString(R.string.buttonExitEmail)
        Toast.makeText(requireActivity(), getString(R.string.emailSentError), Toast.LENGTH_SHORT)
            .show()
    }

}