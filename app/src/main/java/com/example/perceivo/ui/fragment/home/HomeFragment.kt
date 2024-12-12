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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perceivo.R
import com.example.perceivo.api.ApiConfig
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.databinding.FragmentHomeBinding
import com.example.perceivo.repository.AuthRepository
import com.example.perceivo.repository.DashboardRepository
import com.example.perceivo.repository.ProfileRepository
import com.example.perceivo.repository.SentimentRepository
import com.example.perceivo.ui.DetailAnalyticsActivity
import com.example.perceivo.ui.fragment.history.HistoryAdapter
import com.example.perceivo.ui.fragment.history.HistoryViewModel
import com.example.perceivo.viewmodel.HistoryViewModelFactory
import com.example.perceivo.viewmodel.ViewModelFactory
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var emptyTextView: TextView
    private val viewModel: HomeViewModel by lazy {
        val apiService = ApiConfig.getApiService()
        val dataStoreManager = DataStoreManager.getInstance(requireContext())
        val repository = SentimentRepository(apiService, dataStoreManager)
        val authRepository = AuthRepository(apiService, dataStoreManager)
        val profileRepository = ProfileRepository(apiService, dataStoreManager)
        val dashboardRepository = DashboardRepository(apiService, dataStoreManager)
        val factory = ViewModelFactory(authRepository, profileRepository, dashboardRepository, repository)
        factory.create(HomeViewModel::class.java)
    }

    private lateinit var barChartSentiment: BarChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        emptyTextView = binding.emptyView
        barChartSentiment = binding.barChartAllSentiment

        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = HistoryAdapter(emptyList()) { item ->
            val intent = Intent(activity, DetailAnalyticsActivity::class.java)
            intent.putExtra("SENTIMENT_ID", item.unique_id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
        return binding.root
    }

    private fun setupBarChartSentiment(data: Map<String, Int>) {
        val entries = data.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = BarDataSet(entries, "Categories")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val barData = BarData(dataSet)

        barChartSentiment.data = barData
        barChartSentiment.description.isEnabled = false

        // Atur Label X Axis
        val xAxis = barChartSentiment.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(data.keys.toList()) // Menampilkan nama kategori
        xAxis.position = XAxis.XAxisPosition.BOTTOM // Posisi label di bawah
        xAxis.granularity = 1f // Interval antar label
        xAxis.setDrawGridLines(false) // Menghapus garis grid

        // Atur Label Y Axis
        val yAxisLeft = barChartSentiment.axisLeft
        val yAxisRight = barChartSentiment.axisRight
        yAxisRight.isEnabled = false // Nonaktifkan sumbu Y di sebelah kanan
        yAxisLeft.granularity = 1f // Interval antar nilai Y
        yAxisLeft.axisMinimum = 0f // Mulai dari 0
        yAxisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()} comments" // Format label sumbu Y
            }
        }

        barChartSentiment.animateY(1000)
        barChartSentiment.invalidate()
    }

    private fun adjustRecyclerViewHeight(recyclerView: RecyclerView) {
        val adapter = recyclerView.adapter ?: return
        recyclerView.post {
            val totalItemCount = adapter.itemCount
            var totalHeight = 0
            for (i in 0 until totalItemCount) {
                val viewHolder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i))
                adapter.onBindViewHolder(viewHolder, i)
                viewHolder.itemView.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.UNSPECIFIED
                )
                totalHeight += viewHolder.itemView.measuredHeight

                // Tambahkan margin antar item
                val layoutParams = viewHolder.itemView.layoutParams as? ViewGroup.MarginLayoutParams
                if (layoutParams != null) {
                    totalHeight += layoutParams.topMargin + layoutParams.bottomMargin
                }
            }
            val layoutParams = recyclerView.layoutParams
            layoutParams.height = totalHeight
            recyclerView.layoutParams = layoutParams
            recyclerView.invalidate()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe data
        viewModel.sentimentData.observe(viewLifecycleOwner) { data ->
            if (data.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyTextView.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyTextView.visibility = View.GONE
                adapter.updateData(data.takeLast(5).reversed()) //take max 5 in home and reverse
                adjustRecyclerViewHeight(recyclerView)
            }
        }

        // Observe error
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        }

        // Observe username
        viewModel.username.observe(viewLifecycleOwner, {username ->
            val welcomeText = "Hello $username,\nWelcome to Perceivo"
            binding.tvWelcomeUser .text = welcomeText
        })

        // Observe sentiment statistics
        viewModel.sentimentStatistics.observe(viewLifecycleOwner) {sentiment ->
            sentiment?.let {

                // Bar Chart positive negative neutral
                val countPositive = it.positive
                val countNegative = it.negative
                val countNeutral = it.neutral

                val dataSentiment = mapOf("Positive" to countPositive, "Neutral" to countNeutral, "Negative" to countNegative)
                setupBarChartSentiment(dataSentiment)

            }
        }

        // Fetch data
        viewModel.fetchSentimentData()
        viewModel.fetchUsername()
        viewModel.getSentimentStatistics()
    }
}