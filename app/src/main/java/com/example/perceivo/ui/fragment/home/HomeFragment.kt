package com.example.perceivo.ui.fragment.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.perceivo.R
import com.example.perceivo.api.ApiConfig
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.data.MyResult
import com.example.perceivo.databinding.FragmentHomeBinding
import com.example.perceivo.model.SentimentStatistic
import com.example.perceivo.repository.AuthRepository
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.repository.SentimentStatisticRepository
import com.example.perceivo.viewmodel.ViewModelFactory
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var viewModel: HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentHomeBinding.inflate(inflater, container, false)
        initializeViewModel()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeSentiments()
        observeSentimentStatistic()
        val uniqueId = "some_unique_id" // Replace with actual uniqueId
        fetchSentimentData(uniqueId)

        viewModel.username.observe(viewLifecycleOwner, {username ->
            val welcomeText = "Hello $username,\nWelcome to Perceivo"
            binding.tvWelcomeUser.text = welcomeText
        })
        viewModel.fetchUsername()

    }

    private fun fetchSentimentData(uniqueId: String) {
        viewModel.getAllSentiments()
        viewModel.getSentimentStatistic(uniqueId)

    }

    private fun observeSentimentStatistic() {
        viewModel.sentimentStatistic.observe(viewLifecycleOwner) {result ->
            when (result) {
                is MyResult.Success -> {
                    updateSentimentStatistics(result.data)
                    hideProgressBar()
                }
                is MyResult.Error -> {
                    showToast("Failed to fetch sentiment statistics: ${result.exception.message}")
                    hideProgressBar()
                }
                is MyResult.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun updateSentimentStatistics(statistic: SentimentStatistic) {
        binding.tvPositifValue.text = String.format(Locale.getDefault(), "%d", statistic.positive)
        binding.tvNetralValue.text = String.format(Locale.getDefault(), "%d", statistic.neutral)
        binding.tvNegatifValue.text = String.format(Locale.getDefault(), "%d", statistic.negative)

    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

    }

    private fun observeSentiments() {
        viewModel.sentiments.observe(viewLifecycleOwner){result ->
            when(result) {
                is MyResult.Success -> {
                    updateSentimentCount(result.data.size)
                    hideProgressBar()
                }
                is MyResult.Error -> {
                    showToast("Failed to fetch sentiments: ${result.exception.message}")
                    hideProgressBar()
                }
                is MyResult.Loading -> {
                    showProgressBar()
                }
            }
        }
    }

    private fun updateSentimentCount(count: Int) {
        binding.tvSentimentCount.text = "Sentiment Count: $count"


    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }


    private fun initializeViewModel() {
        val apiService = ApiConfig.getApiService()
        val dataStoreManager = DataStoreManager.getInstance(requireContext())
        val authRepository = AuthRepository(apiService, dataStoreManager)
        val profileRepository = ProfileRepository(apiService, dataStoreManager)
        val sentimentStatisticRepository = SentimentStatisticRepository(apiService, dataStoreManager)
        val factory = ViewModelFactory(authRepository, profileRepository, sentimentStatisticRepository)
        viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)


    }


}