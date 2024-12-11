package com.example.perceivo.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perceivo.R
import com.example.perceivo.api.ApiConfig
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.repository.SentimentRepository
import com.example.perceivo.ui.DetailAnalyticsActivity
import com.example.perceivo.ui.fragment.history.HistoryAdapter
import com.example.perceivo.ui.fragment.history.HistoryViewModel
import com.example.perceivo.viewmodel.HistoryViewModelFactory

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var emptyTextView: TextView
    private lateinit var progressBar: ProgressBar
    private val viewModel: HistoryViewModel by lazy {
        val apiService = ApiConfig.getApiService()
        val dataStoreManager = DataStoreManager.getInstance(requireContext())
        val repository = SentimentRepository(apiService, dataStoreManager)
        val factory = HistoryViewModelFactory(repository)
        factory.create(HistoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        emptyTextView = view.findViewById(R.id.empty_view)
        progressBar = view.findViewById(R.id.progress_bar)

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = HistoryAdapter(emptyList()) { item ->
            val intent = Intent(activity, DetailAnalyticsActivity::class.java)
            intent.putExtra("SENTIMENT_ID", item.unique_id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe data
        viewModel.sentimentData.observe(viewLifecycleOwner) { data ->
            progressBar.visibility = View.GONE
            if (data.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
                adapter.updateData(data.takeLast(5).reversed()) //take max 5 in home and reverse
            }
        }

        // Observe error
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Catch Sentiment
        progressBar.visibility = View.VISIBLE
        viewModel.fetchSentimentData()
    }
}
