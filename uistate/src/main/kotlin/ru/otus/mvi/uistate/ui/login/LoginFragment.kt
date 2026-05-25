package ru.otus.mvi.uistate.ui.login

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
import ru.otus.mvi.common.ui.LoginUiState
import ru.otus.mvi.uistate.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        LoginViewModel.Factory
    }

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
            viewModel.login()
        }

        binding.retryButton.setOnClickListener {
            viewModel.login()
        }

        binding.cancelButton.setOnClickListener {
            viewModel.clearError()
        }
    }

    private fun setupInput() {
        binding.loginInput.doAfterTextChanged { 
            viewModel.setUsername(it?.toString().orEmpty())
        }
        binding.passwordInput.doAfterTextChanged { 
            viewModel.setPassword(it?.toString().orEmpty())
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: LoginUiState) {
        when (state) {
            is LoginUiState.Form -> {
                binding.loadingView.isVisible = false
                binding.loginForm.isVisible = true
                binding.errorView.isVisible = false

                binding.loginButton.isEnabled = state.loginEnabled
                // Update text if different to avoid cursor reset or unnecessary events
                if (binding.loginInput.text?.toString() != state.username) {
                    binding.loginInput.setTextKeepState(state.username)
                }
                if (binding.passwordInput.text?.toString() != state.password) {
                    binding.passwordInput.setTextKeepState(state.password)
                }
            }
            is LoginUiState.LoggingIn -> {
                binding.loadingView.isVisible = true
                binding.loginForm.isVisible = true
                binding.errorView.isVisible = false

                binding.loginButton.isEnabled = false
            }
            is LoginUiState.Error -> {
                binding.loadingView.isVisible = false
                binding.loginForm.isVisible = false
                binding.errorView.isVisible = true

                binding.errorMessage.text = state.message
                binding.retryButton.isVisible = state.retryAvailable
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
