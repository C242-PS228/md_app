package com.example.perceivo.ui.fragment.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perceivo.model.ChangePasswordRequest
import com.example.perceivo.model.ChangePasswordResponse
import com.example.perceivo.model.ProfileRequest
import com.example.perceivo.model.ProfileResponse
import com.example.perceivo.model.UpdateProfileResponse
import com.example.perceivo.repository.AuthRepository
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.viewmodel.UiState
import kotlinx.coroutines.launch

class ProfileViewModel(private val profileRepository: ProfileRepository, private val authRepository: AuthRepository) : ViewModel() {
    private val _profileData = MutableLiveData<ProfileResponse>()
    val profileData: LiveData<ProfileResponse> get() = _profileData

    private val _state = MutableLiveData<UiState>()
    val state: LiveData<UiState> get() = _state

    private val _updateProfileStatus = MutableLiveData<UpdateProfileResponse>()
    val updateProfileStatus: LiveData<UpdateProfileResponse> get() = _updateProfileStatus

    private val _changePasswordStatus = MutableLiveData<ChangePasswordResponse>()
    val changePasswordStatus: LiveData<ChangePasswordResponse> get() = _changePasswordStatus

    private val _logoutState = MutableLiveData<UiState>()
    val logoutState: LiveData<UiState> get() = _logoutState



    fun getProfile() {
        viewModelScope.launch {
            try {
                val response = profileRepository.getProfile()
                Log.d("ProfileViewModel", "Profile fetched successfully: ${response.data}")
                _profileData.postValue(response)
                _state.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching profile: ${e.message}", e)
                _state.postValue(UiState.ERROR)
            }
        }
    }

    fun updateProfile(profileRequest: ProfileRequest) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "Updating profile with data: $profileRequest")
                val response = profileRepository.updateProfile(profileRequest)
                _updateProfileStatus.postValue(response)
                _state.postValue(UiState.SUCCESS)

                if (response.status == "success") {
                    val currentData = _profileData.value?.data?.get(0)

                    if (currentData != null) {
                        val updatedProfile = currentData.copy(
                            name = profileRequest.fullname,
                            username = profileRequest.username,
                            address = profileRequest.address
                        )

                        // Update LiveData dengan profil yang diperbarui
                        _profileData.postValue(
                            ProfileResponse(
                                status = response.status,
                                message = response.message,
                                data = listOf(updatedProfile)
                            )
                        )
                    }
                }  else {
                    Log.e("ProfileViewModel", "Failed to update profile: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile: ${e.message}", e)
                _state.postValue(UiState.ERROR)
            }
        }
    }


    fun changePassword (changePasswordRequest: ChangePasswordRequest) {
        viewModelScope.launch {
            try {
                val response = profileRepository.changePassword(changePasswordRequest)
                _changePasswordStatus.postValue(response)
            }catch (e: Exception) {
                Log.e("ProfileViewModel", "Error changing password", e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                _logoutState.postValue(UiState.LOADING)
                authRepository.clearToken()
                Log.d("ProfileViewModel", "Token berhasil dihapus")
                _logoutState.postValue(UiState.SUCCESS)
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error during logout: ${e.message}", e)
                _logoutState.postValue(UiState.ERROR)
            }
        }
    }

}