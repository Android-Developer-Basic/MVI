package ru.otus.mvi.uistate.ui.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import ru.otus.mvi.common.ui.ContentUiState
import ru.otus.mvi.uistate.R
import ru.otus.mvi.uistate.databinding.FragmentContentBinding

class ContentFragment : Fragment() {

    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContentFragmentViewModel by viewModels()

    override fun onCreateView(
        layoutInflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    render(state)
                }
            }
        }

        // Observe navigation events using Channel directly
        // We use a loop to receive events. This ensures one-time consumption.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                for (event in viewModel.navigationEvents) {
                    when(event) {
                        ContentNavigationEvent.NavigateToLogin -> onLogin()
                        ContentNavigationEvent.NavigateToLogout -> onLogout()
                    }
                }
            }
        }

        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun render(state: ContentUiState) {
        binding.loadingIndicator.isVisible = state is ContentUiState.Loading
        binding.contentGroup.isVisible = state is ContentUiState.Content
        
        if (state is ContentUiState.Content) {
            binding.userNameText.text = getString(R.string.welcome_user, state.username)
            binding.logoutButton.isEnabled = state.logoutEnabled
        }
    }

    private fun onLogin() {
        findNavController().navigate("login")
    }

    private fun onLogout() {
        findNavController().navigate("logout")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
