package com.example.perceivo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.perceivo.R
import com.example.perceivo.api.ApiConfig
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.model.CommentsItem
import com.example.perceivo.model.ListAssistances
import com.example.perceivo.model.NegativeItem
import com.example.perceivo.model.PositiveItem
import com.example.perceivo.model.QuestionsItem
import com.example.perceivo.model.SentimentDetailResponse
import com.example.perceivo.repository.SentimentRepository
import com.example.perceivo.ui.fragment.analytics.CommentAdapter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailAnalyticsActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var imgIcon: ImageView
    private lateinit var tvSentimentCategories: TextView
    private lateinit var tvResume: TextView

    private lateinit var positiveKeywordLayout: LinearLayout
    private lateinit var negativeKeywordLayout: LinearLayout

    private lateinit var rvPositiveComments: RecyclerView
    private lateinit var rvNegativeComments: RecyclerView
    private lateinit var rvQuestionsComments: RecyclerView
    private lateinit var rvAssistanceComments: RecyclerView

    private lateinit var lblPositiveComments: TextView
    private lateinit var lblNegativeComments: TextView
    private lateinit var lblQuestionsComments: TextView
    private lateinit var lblAssistanceComments: TextView

    private lateinit var lblPositiveKeywords: TextView
    private lateinit var lblNegativeKeywords: TextView

    private lateinit var barChartSentiment: BarChart
    private lateinit var barChartPositive: HorizontalBarChart
    private lateinit var barChartNegative: HorizontalBarChart

    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_analytics)

        progressBar = findViewById(R.id.progress_bar)
        btnBack = findViewById(R.id.btn_back)
        scrollView = findViewById(R.id.scroll_view_sentiment)

        tvTitle = findViewById(R.id.tv_title)
        imgIcon = findViewById(R.id.img_icon)

        tvSentimentCategories = findViewById(R.id.tv_sentiment_categories)
        tvResume = findViewById(R.id.tv_resume)

        positiveKeywordLayout = findViewById(R.id.positive_keyword)
        negativeKeywordLayout = findViewById(R.id.negative_keyword)

        rvPositiveComments = findViewById(R.id.rv_positive_comments)
        rvNegativeComments = findViewById(R.id.rv_negative_comments)
        rvQuestionsComments = findViewById(R.id.rv_questions_comments)
        rvAssistanceComments = findViewById(R.id.rv_assistance_comments)

        lblPositiveComments = findViewById(R.id.positive_not_found)
        lblNegativeComments = findViewById(R.id.negative_not_found)
        lblQuestionsComments = findViewById(R.id.question_not_found)
        lblAssistanceComments = findViewById(R.id.assistance_not_found)
        lblPositiveKeywords = findViewById(R.id.positivekey_not_found)
        lblNegativeKeywords = findViewById(R.id.negativekey_not_found)

        //chart
        barChartSentiment = findViewById(R.id.barChartSentiment)
        barChartPositive = findViewById(R.id.barChartPositive)
        barChartNegative = findViewById(R.id.barChartNegative)

        // Set layout manager for RecyclerViews
        rvPositiveComments.layoutManager = LinearLayoutManager(this)
        rvNegativeComments.layoutManager = LinearLayoutManager(this)
        rvQuestionsComments.layoutManager = LinearLayoutManager(this)
        rvAssistanceComments.layoutManager = LinearLayoutManager(this)

        val sentimentId = intent.getStringExtra("SENTIMENT_ID")
        if (sentimentId.isNullOrEmpty()) {
            showToast("Sentiment ID is missing!")
            finish() // Exit the activity if the ID is invalid
        } else {
            fetchSentimentDetail(sentimentId)
        }

        // Back Button
        //btnBack.setOnClickListener {
        //    val navController = findNavController(R.id.nav_host_fragment_activity_main)
        //    navController.navigate(R.id.nav_history) // Navigasi ke HistoryFragment
        //}

        btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("nav_history", "HistoryFragment") // Ganti dengan fragment tujuan
            startActivity(intent)
            finish()
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@DetailAnalyticsActivity, MainActivity::class.java)
                intent.putExtra("nav_history", "HistoryFragment") // Ganti dengan fragment tujuan
                startActivity(intent)
                finish() // Menutup activity saat ini
            }
        })
        scrollView.visibility = View.GONE
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

    private fun setupHorizontalBarChart(chart: HorizontalBarChart, data: Map<String, Int>, descriptionText: String) {
        val entries = data.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = BarDataSet(entries, descriptionText)
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val barData = BarData(dataSet)

        chart.data = barData

        // Atur Label X Axis
        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(data.keys.toList()) // Menampilkan nama kategori
        xAxis.position = XAxis.XAxisPosition.BOTTOM // Posisi label di bawah
        xAxis.granularity = 1f // Interval antar label
        xAxis.setDrawGridLines(false) // Menghapus garis grid

        // Atur Label Y Axis
        val yAxisLeft = chart.axisLeft
        val yAxisRight = chart.axisRight
        yAxisRight.isEnabled = false // Nonaktifkan sumbu Y di sebelah kanan
        yAxisLeft.granularity = 1f // Interval antar nilai Y
        yAxisLeft.axisMinimum = 0f // Mulai dari 0
        yAxisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return "${value.toInt()}" // Format label sumbu Y
            }
        }

        // Menambahkan deskripsi
        chart.description = Description().apply { text = descriptionText }

        chart.setFitBars(true)
        chart.animateY(1000)
        chart.invalidate()
    }


    private fun fetchSentimentDetail(sentimentId: String) {
        progressBar.visibility = View.VISIBLE
        val sentimentRepository = SentimentRepository(ApiConfig.getApiService(), DataStoreManager.getInstance(applicationContext))
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sentimentDetailResponse = sentimentRepository.fetchSentimentDetail(sentimentId)
                CoroutineScope(Dispatchers.Main).launch {
                    scrollView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                    updateUI(sentimentDetailResponse)
                }
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.Main).launch {
                    progressBar.visibility = View.GONE
                    Log.e("SentimentDetail", "Error fetching sentiment details", e)
                    showToast("Error: ${e.message}")
                }
            }
        }
    }

    private fun updateUI(response: SentimentDetailResponse) {
        response.data?.let { data ->
            // Set title
            tvTitle.text = data.title ?: getString(R.string.analytic_resume)

            // Set sentiment category
            val sentimentCategory = when {
                data.statistic?.data?.positive ?: 0 > data.statistic?.data?.negative ?: 0 -> {
                    tvSentimentCategories.setBackgroundResource(R.drawable.rounded_green4)
                    getString(R.string.positive)
                }
                data.statistic?.data?.negative ?: 0 > data.statistic?.data?.positive ?: 0 -> {
                    tvSentimentCategories.setBackgroundResource(R.drawable.rounded_red4)
                    getString(R.string.negative)
                }
                else -> {
                    tvSentimentCategories.setBackgroundResource(R.drawable.rounded_blue4)
                    getString(R.string.neutral)
                }
            }

            // Bar Chart positive negative neutral
            val countPositive = data.statistic?.data?.positive ?: 0
            val countNegative = data.statistic?.data?.negative ?: 0
            val countNeutral = data.statistic?.data?.neutral ?: 0

            tvSentimentCategories.text = sentimentCategory

            val dataSentiment = mapOf("Positive" to countPositive, "Neutral" to countNeutral, "Negative" to countNegative)
            setupBarChartSentiment(dataSentiment)

            // barchart keyword
            val graphNegativeData = data.statistic?.data?.key_words?.graph_negative ?: emptyList()
            val graphPositiveData = data.statistic?.data?.key_words?.graph_positive ?: emptyList()

            // Assuming graphNegativeData is a List<YourDataType>
            val graphNegativeMap: Map<String, Int> = graphNegativeData.filterNotNull().associate { item ->
                val tagName = item.tagname ?: "Unknown" // Ensure tagname is a String
                val value = item.value ?: 0 // Ensure value is an Int
                tagName to value // Create a Pair
            }

            val graphPositiveMap: Map<String, Int> = graphPositiveData.filterNotNull().associate { item ->
                val tagName = item.tagname ?: "Unknown" // Ensure tagname is a String
                val value = item.value ?: 0 // Ensure value is an Int
                tagName to value // Create a Pair
            }

            // Setup bar chart dengan data
            try {
                setupHorizontalBarChart(barChartNegative, graphNegativeMap, "Graph Negative Keywords")
                barChartNegative.description.isEnabled = false
                setupHorizontalBarChart(barChartPositive, graphPositiveMap, "Graph Positive Keywords")
                barChartPositive.description.isEnabled = false
            } catch (e: Exception) {
                Log.e("HorizontalBarChartError", e.message ?: "Unknown Error")
            }
            // Set sentiment resume
            tvResume.text = data.statistic?.data?.resume ?: getString(R.string.no_data)

            // Icon Platform
            val platformIcon = when (data.platform) {
                "instagram" -> R.drawable.ic_instagram
                "tiktok" -> R.drawable.ic_tiktok
                "googlemaps" -> R.drawable.ic_gmaps
                else -> R.drawable.ic_youtube
            }

            imgIcon.setImageResource(platformIcon)

            fun adjustRecyclerViewHeight(recyclerView: RecyclerView) {
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
                    }
                    val layoutParams = recyclerView.layoutParams
                    layoutParams.height = totalHeight
                    recyclerView.layoutParams = layoutParams
                    recyclerView.invalidate()
                }
            }

            // Map positive comments
            val positiveComments = mapPositiveToCommentsItem(data.statistic?.data?.topstatus?.positive, data.comments)
            if (positiveComments.isEmpty()) {
                rvPositiveComments.visibility = View.GONE
                lblPositiveComments.visibility = View.VISIBLE
            } else {
                rvPositiveComments.visibility = View.VISIBLE
                lblPositiveComments.visibility = View.GONE
                rvPositiveComments.adapter = CommentAdapter(positiveComments)
                adjustRecyclerViewHeight(rvPositiveComments)
            }

            // Map negative comments
            val negativeComments = mapNegativeToCommentsItem(data.statistic?.data?.topstatus?.negative, data.comments)
            if (negativeComments.isEmpty()) {
                rvNegativeComments.visibility = View.GONE
                lblNegativeComments.visibility = View.VISIBLE
            } else {
                rvNegativeComments.visibility = View.VISIBLE
                lblNegativeComments.visibility = View.GONE
                rvNegativeComments.adapter = CommentAdapter(negativeComments)
                adjustRecyclerViewHeight(rvNegativeComments)
            }

            // Map Question Comments
            val questionComments = mapQuestionToCommentsItem(data.statistic?.data?.questions, data.comments)
            if (questionComments.isEmpty()) {
                rvQuestionsComments.visibility = View.GONE
                lblQuestionsComments.visibility = View.VISIBLE
            } else {
                rvQuestionsComments.visibility = View.VISIBLE
                lblQuestionsComments.visibility = View.GONE
                rvQuestionsComments.adapter = CommentAdapter(questionComments)
                adjustRecyclerViewHeight(rvQuestionsComments)
            }

            // Map Assistance Comments
            val assistanceComments = mapAssistanceToCommentsItem(data.statistic?.data?.assistances, data.comments)
            if (assistanceComments.isEmpty()) {
                rvAssistanceComments.visibility = View.GONE
                lblAssistanceComments.visibility = View.VISIBLE
            } else {
                rvAssistanceComments.visibility = View.VISIBLE
                lblAssistanceComments.visibility = View.GONE
                rvAssistanceComments.adapter = CommentAdapter(assistanceComments)
                adjustRecyclerViewHeight(rvAssistanceComments)
            }

            // Populate positive keywords
            positiveKeywordLayout.removeAllViews()
            if (data.statistic?.data?.key_words?.positive.isNullOrEmpty()) {
                lblPositiveKeywords.visibility = View.VISIBLE
                barChartPositive.visibility = View.GONE
            } else {
                lblPositiveKeywords.visibility = View.GONE
                data.statistic?.data?.key_words?.positive?.forEach { keyword ->
                    val keywordView = layoutInflater.inflate(R.layout.item_keywoard, positiveKeywordLayout, false)
                    keywordView.findViewById<TextView>(R.id.tv_keyword).text = keyword?.tagname ?: getString(R.string.no_data)
                    positiveKeywordLayout.addView(keywordView)
                }
            }

            // Populate negative keywords
            negativeKeywordLayout.removeAllViews()
            if (data.statistic?.data?.key_words?.negative.isNullOrEmpty()) {
                lblNegativeKeywords.visibility = View.VISIBLE
                barChartNegative.visibility = View.GONE
            } else {
                lblNegativeKeywords.visibility = View.GONE
                data.statistic?.data?.key_words?.negative?.forEach { keyword ->
                    val keywordView = layoutInflater.inflate(R.layout.item_keywoard, negativeKeywordLayout, false)
                    keywordView.findViewById<TextView>(R.id.tv_keyword).text = keyword?.tagname ?: getString(R.string.no_data)
                    negativeKeywordLayout.addView(keywordView)
                }
            }


        } ?: run {
            showToast(getString(R.string.error_fetching_data))
        }
    }

    //mapping
    private fun mapPositiveToCommentsItem(
        list: List<PositiveItem?>?,
        comments: List<CommentsItem?>?
    ): List<CommentsItem> {
        return list?.mapNotNull { item ->
            item?.let {
                val matchingComment = comments?.find { it?.text == item.text }
                CommentsItem(
                    uid = matchingComment?.uid,
                    createdAt = matchingComment?.createdAt,
                    text = item.text,
                    avatar = matchingComment?.avatar,
                    username = item.username,
                    likes = matchingComment?.likes
                )
            }
        } ?: emptyList()
    }

    private fun mapNegativeToCommentsItem(
        list: List<NegativeItem?>?,
        comments: List<CommentsItem?>?
    ): List<CommentsItem> {
        return list?.mapNotNull { item ->
            item?.let {
                val matchingComment = comments?.find { it?.text == item.text }
                CommentsItem(
                    uid = matchingComment?.uid,
                    createdAt = matchingComment?.createdAt,
                    text = item.text,
                    avatar = matchingComment?.avatar,
                    username = item.username,
                    likes = matchingComment?.likes
                )
            }
        } ?: emptyList()
    }

    private fun mapQuestionToCommentsItem(
        list: List<QuestionsItem?>?,
        comments: List<CommentsItem?>?
    ): List<CommentsItem> {
        return list?.mapNotNull { item ->
            item?.let {
                val matchingComment = comments?.find { it?.text == item.text }
                CommentsItem(
                    uid = matchingComment?.uid,
                    createdAt = matchingComment?.createdAt,
                    text = item.text,
                    avatar = matchingComment?.avatar,
                    username = item.username,
                    likes = matchingComment?.likes
                )
            }
        } ?: emptyList()
    }

    private fun mapAssistanceToCommentsItem(
        list: List<ListAssistances?>?,
        comments: List<CommentsItem?>?
    ): List<CommentsItem> {
        return list?.mapNotNull { item ->
            item?.let {
                val matchingComment = comments?.find { it?.text == item.text }
                CommentsItem(
                    uid = matchingComment?.uid,
                    createdAt = matchingComment?.createdAt,
                    text = item.text,
                    avatar = matchingComment?.avatar,
                    username = item.username,
                    likes = matchingComment?.likes
                )
            }
        } ?: emptyList()
    }

    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}