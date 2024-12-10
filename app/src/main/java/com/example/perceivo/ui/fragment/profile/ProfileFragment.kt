package com.example.perceivo.ui.fragment.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.perceivo.api.ApiConfig
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.databinding.FragmentProfileBinding
import com.example.perceivo.model.ChangePasswordRequest
import com.example.perceivo.model.ProfileRequest
import com.example.perceivo.repository.AuthRepository
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.repository.SentimentStatisticRepository
import com.example.perceivo.ui.SignInActivity
import com.example.perceivo.viewmodel.UiState
import com.example.perceivo.viewmodel.ViewModelFactory

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding?= null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileViewModel
    private var isProfileLoaded = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val apiService = ApiConfig.getApiService()
        val dataStoreManager = DataStoreManager.getInstance(requireContext())
        val authRepository = AuthRepository(apiService, dataStoreManager)
        val profileRepository = ProfileRepository(apiService, dataStoreManager)
        val sentimentStatisticRepository = SentimentStatisticRepository(apiService, dataStoreManager)
        val factory = ViewModelFactory(authRepository, profileRepository, sentimentStatisticRepository)
        viewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeProfileData()
        observeUiState()
        observeLogoutState()
        observeChangePasswordStatus()
        observeUpdateProfileStatus()
        setupUiListeners()
        viewModel.getProfile()
    }

    private fun observeUpdateProfileStatus() {
        viewModel.updateProfileStatus.observe(viewLifecycleOwner) { response ->
            if (response?.status == "success") {
                Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeChangePasswordStatus() {
        viewModel.changePasswordStatus.observe(viewLifecycleOwner) { response ->
            when {
                response != null && response.status == "success" -> {
                    Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                }
                response != null && response.status == "error" -> {
                    Toast.makeText(context, "Failed to change password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(context, "Error occurred while changing password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupUiListeners() {
        setupUpdateProfileListener()
        setupChangePasswordListener()
        setupLogoutListener()
    }

    private fun setupLogoutListener() {
        binding.cardLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.logout()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun setupChangePasswordListener() {
        binding.btnChangePassword.setOnClickListener {
            val oldPassword = binding.edSettingOldPassword.text.toString()
            val newPassword = binding.edSettingNewPassword.text.toString()

            if(oldPassword.isNotEmpty()&& newPassword.isNotEmpty()){
                val changePasswordRequest = ChangePasswordRequest(
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )
                viewModel.changePassword(changePasswordRequest)
            } else{
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupUpdateProfileListener() {
        binding.btnUpdateProfile.setOnClickListener {
            val newName = binding.edSettingFullname.text.toString()
            val newAddress = binding.edSettingAddress.text.toString()
            val newUsername = binding.edSettingUsername.text.toString()
            if(newName.isNotEmpty() && newAddress.isNotEmpty() && newUsername.isNotEmpty()) {
                val profileRequest = ProfileRequest(
                    fullname = newName,
                    username = newUsername,
                    address = newAddress
                )
                Log.d("ProfileFragment", "Update profile button clicked with data: $profileRequest")
                viewModel.updateProfile(profileRequest)
            } else{
                Log.e("ProfileFragment", "Update profile failed: Empty fields")
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun observeLogoutState() {
        viewModel.logoutState.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                UiState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    navigateToLoginScreen()
                }
                UiState.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, "Error during logout", Toast.LENGTH_SHORT).show()
                }
                null -> {
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun observeUiState() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                UiState.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
                UiState.SUCCESS -> {
                    binding.progressBar.visibility = View.GONE
                }
                UiState.ERROR -> {
                    binding.progressBar.visibility = View.GONE
                }
                null -> binding.progressBar.visibility = View.GONE
            }
        }
    }

    private fun observeProfileData() {
        viewModel.profileData.observe(viewLifecycleOwner) {profileResponse->
            if (profileResponse !=null ) {
                val profile = profileResponse.data[0]
                binding.edSettingUsername.setText(profile.username)
                binding.edSettingEmail.setText(profile.email)
                binding.edSettingFullname.setText(profile.name)
                binding.edSettingAddress.setText(profile.address)
                isProfileLoaded = true

            } else {
                Log.e("ProfileFragment", "No profile data available")
                }
            }
        }


    private fun navigateToLoginScreen() {
        val intent = Intent(requireContext(), SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}