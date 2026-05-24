package ru.otus.mvi.mvvm.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.otus.mvi.mvvm.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupInput()
        observeState()
        observeNavigation()

        binding.loginButton.setOnClickListener {
            doLogin()
        }

        binding.retryButton.setOnClickListener {
            doLogin()
        }

        binding.cancelButton.setOnClickListener {
            viewModel.clearError()
            binding.passwordInput.text?.clear()
        }
    }

    private fun setupInput() {
        val checkFields = {
            val login = binding.loginInput.text?.toString().orEmpty()
            val password = binding.passwordInput.text?.toString().orEmpty()
            binding.loginButton.isEnabled = login.isNotBlank() && password.isNotBlank()
        }

        binding.loginInput.doAfterTextChanged { checkFields() }
        binding.passwordInput.doAfterTextChanged { checkFields() }
        checkFields()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loggingIn.collect { isLoading ->
                        binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE
                    }
                }

                launch {
                    viewModel.error.collect { error ->
                        if (error != null) {
                            binding.loginForm.visibility = View.GONE
                            binding.errorView.visibility = View.VISIBLE
                            binding.errorMessage.text = error.message
                            binding.retryButton.isVisible = error.isFatal.not()
                        } else {
                            binding.loginForm.visibility = View.VISIBLE
                            binding.errorView.visibility = View.GONE
                            binding.loginLayout.error = null
                        }
                    }
                }
            }
        }
    }

    private fun observeNavigation() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                for (event in viewModel.navigationEvents) {
                    when (event) {
                        LoginNavigationEvent.NavigateToContent -> {
                            findNavController().navigate("content") {
                                popUpTo("content") { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun doLogin() {
        val login = binding.loginInput.text.toString()
        val password = binding.passwordInput.text.toString()
        viewModel.login(login, password)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
