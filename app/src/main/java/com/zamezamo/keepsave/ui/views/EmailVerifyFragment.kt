package com.zamezamo.keepsave.ui.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.zamezamo.keepsave.R
import com.zamezamo.keepsave.data.Database
import com.zamezamo.keepsave.ui.viewmodels.EmailVerifyViewModel

class EmailVerifyFragment : Fragment() {

    companion object {

        fun newInstance() = EmailVerifyFragment()

    }

    private var buttonConfirmEmail: Button? = null

    private var textViewEmail: TextView? = null

    private val user = Database.auth.currentUser

    private val viewModel: EmailVerifyViewModel by viewModels()

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

        textViewEmail?.text = user?.email ?: ""

    }


    private fun setupListeners() {

        setLoading(true)

        buttonConfirmEmail?.setOnClickListener {

            Database.auth.signOut()

            requireActivity().supportFragmentManager.beginTransaction()
                .remove(this).commitNow()

        }

    }

    private fun setLoading(loading: Boolean) {
        buttonConfirmEmail?.isEnabled = !loading
    }

    private fun showEmailError() {
        Toast.makeText(requireActivity(), getString(R.string.emailSentError), Toast.LENGTH_SHORT)
            .show()
    }

}