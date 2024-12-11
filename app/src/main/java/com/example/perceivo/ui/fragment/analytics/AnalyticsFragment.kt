package com.example.perceivo.ui.fragment.analytics

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.perceivo.R
import com.example.perceivo.api.ApiConfig
import com.example.perceivo.data.DataStoreManager
import com.example.perceivo.model.SentimentRequest
import com.example.perceivo.repository.SentimentRepository
import com.example.perceivo.ui.DetailAnalyticsActivity
import com.example.perceivo.viewmodel.AnalyticsViewModelFactory
import kotlinx.coroutines.launch

class AnalyticsFragment : Fragment() {
    private lateinit var linksContainer: LinearLayout
    private lateinit var addLinkButton: Button
    private lateinit var tagsContainer: LinearLayout
    private lateinit var addTagButton: Button
    private lateinit var tagsListContainer: LinearLayout
    private lateinit var analyzeButton: Button
    private lateinit var tagInput: EditText
    private lateinit var defaultLinkField: EditText
    private lateinit var titleField: EditText

    private var tagList: MutableList<String> = mutableListOf()

    private lateinit var progressBar: ProgressBar

    private val viewModel: AnalyticsViewModel by viewModels {
        AnalyticsViewModelFactory(
            SentimentRepository(
                ApiConfig.getApiService(),
                DataStoreManager.getInstance(requireContext())
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_analytics, container, false)

        // Init views
        progressBar = view.findViewById(R.id.progress_bar_2)
        linksContainer = view.findViewById(R.id.links_container)
        addLinkButton = view.findViewById(R.id.btn_add_link)
        tagsContainer = view.findViewById(R.id.tags_container)
        addTagButton = view.findViewById(R.id.btn_add_tag)
        tagsListContainer = view.findViewById(R.id.tags_list_container)
        analyzeButton = view.findViewById(R.id.btn_analyze)
        tagInput = view.findViewById(R.id.et_tags)
        titleField = view.findViewById(R.id.et_title)

        // Add default link field (cannot be deleted)
        addDefaultLinkField()

        // Set listeners
        addLinkButton.setOnClickListener { addLinkField() }
        addTagButton.setOnClickListener { addTag() }
        analyzeButton.setOnClickListener { analyzeData() }

        // Observe ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.sentimentResponse.collect { response ->
                        response?.let {
                            Toast.makeText(requireContext(), "Success: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                launch {
                    viewModel.errorMessage.collect { error ->
                        error?.let {
                            Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
        return view
    }

    private fun addDefaultLinkField() {
        defaultLinkField = createLinkField() // Save the reference to the default link field
        defaultLinkField.hint = "Enter your link (required)"
        defaultLinkField.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt()
        )
        defaultLinkField.setBackgroundResource(R.drawable.rounded_field)
        defaultLinkField.setTextAppearance(R.style.EditTextPrimary)
        defaultLinkField.setPadding(
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt()
        )
        linksContainer.addView(defaultLinkField)
    }

    private fun addLinkField() {
        val linkLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 16
            }
        }

        val editText = createLinkField()
        editText.hint = "Enter additional link"
        val deleteButton = createDeleteButton()

        deleteButton.setOnClickListener { linksContainer.removeView(linkLayout) }

        linkLayout.addView(editText)
        linkLayout.addView(deleteButton)
        linksContainer.addView(linkLayout)
    }

    private fun createLinkField(): EditText {
        return EditText(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                0,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt()
            ).apply {
                weight = 1f
            }
            hint = "Enter your link"
            setBackgroundResource(R.drawable.rounded_field)
            setTextAppearance(R.style.EditTextPrimary)
            setPadding(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt()
            )
        }
    }

    private fun createDeleteButton(): ImageButton {
        return ImageButton(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt(),
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt()
            ).apply {
                marginStart = 10
            }
            setImageResource(R.drawable.ic_delete)
            setBackgroundResource(R.drawable.rounded_red)
            setPadding(12, 12, 12, 12)
        }
    }

    private fun addTag() {
        val tagText = tagInput.text.toString().trim()

        if (TextUtils.isEmpty(tagText)) {
            Toast.makeText(requireContext(), "Tag cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        if (tagList.contains(tagText)) {
            Toast.makeText(requireContext(), "Tag already added!", Toast.LENGTH_SHORT).show()
            return
        }

        tagList.add(tagText)
        tagInput.text.clear()

        val tagView = createTagView(tagText)
        tagsListContainer.addView(tagView)
    }

    private fun createTagView(tag: String): LinearLayout {
        val tagLayout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
            setPadding(8)
        }

        val tagTextView = TextView(requireContext()).apply {
            text = tag
            layoutParams = LinearLayout.LayoutParams(
                0,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt()
            ).apply {
                weight = 1f
                setBackgroundResource(R.drawable.rounded_field)
                setTextAppearance(R.style.EditTextPrimary)
                setPadding(
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
                    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt()
                )
            }
        }

        val deleteButton = createDeleteButton()
        deleteButton.setOnClickListener {
            tagList.remove(tag)
            tagsListContainer.removeView(tagLayout)
        }

        tagLayout.addView(tagTextView)
        tagLayout.addView(deleteButton)
        return tagLayout
    }

    private fun analyzeData() {
        val links = mutableListOf<String>()
        val titleAnalytic = view?.findViewById<EditText>(R.id.et_title)?.text.toString().trim()
        val tagsAnalytic = tagList.toList() // Catch Tag List
        val commentsCountText = view?.findViewById<EditText>(R.id.et_comments)?.text.toString().trim()
        val commentsCount = if (commentsCountText.isNotEmpty()) commentsCountText.toInt() else 0

        // Catch platform from Spinner
        val platformSpinner = view?.findViewById<Spinner>(R.id.spinner_platform)
        val selectedPlatform = platformSpinner?.selectedItem.toString()
        if (selectedPlatform.isEmpty()) {
            Toast.makeText(requireContext(), "Platform is required!", Toast.LENGTH_SHORT).show()
            return
        }

        val formattedPlatform = selectedPlatform.lowercase().replace(" ", "")

        // Catch link default
        val defaultLink = defaultLinkField.text.toString().trim()
        if (defaultLink.isNotEmpty()) {
            links.add(defaultLink) // Add default link to list
        }

        // Catch additional Link
        for (i in 0 until linksContainer.childCount) {
            val child = linksContainer.getChildAt(i)
            if (child is LinearLayout) {
                val editText = child.getChildAt(0) as? EditText
                val link = editText?.text.toString().trim()
                if (link.isNotEmpty()) {
                    links.add(link)
                }
            }
        }

        // Cek link
        if (links.isEmpty()) {
            Toast.makeText(requireContext(), "At least one link is required!", Toast.LENGTH_SHORT).show()
            return
        }

        // Cek Title
        if (titleAnalytic.isEmpty()) {
            Toast.makeText(requireContext(), "Title is required!", Toast.LENGTH_SHORT).show()
            return
        }

        // Show ProgressBar
        progressBar.visibility = View.VISIBLE
        //Log.d("ProgressBarStatus", "Visibilitas ProgressBar: ${progressBar.visibility}")

        // make analytics
        val request = if (links.size == 1) {
            SentimentRequest(
                link = links[0], //1 link
                platformName = formattedPlatform,
                title = titleAnalytic,
                tags = tagsAnalytic,
                resultLimit = commentsCount
            )
        } else {
            SentimentRequest(
                link = links, // more than 1 link
                platformName = formattedPlatform,
                title = titleAnalytic,
                tags = tagsAnalytic,
                resultLimit = commentsCount
            )
        }

        // Massage when make analytics
        Toast.makeText(requireContext(), "Analyzing ${links.size} links on platform $selectedPlatform with $commentsCount comments!", Toast.LENGTH_SHORT).show()

        // Call viewmodel
        viewModel.fetchSentiment(request)

        // Observe the sentiment response using repeatOnLifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sentimentResponse.collect { response ->
                    response?.let {
                        val sentimentId = it.sentiment_id
                        if (sentimentId.isNotEmpty()) { // Check if sentimentId is not empty
                            navigateToDetailActivity(sentimentId)
                        } else {
                            Toast.makeText(requireContext(), "Sentiment ID not found", Toast.LENGTH_SHORT).show()
                        }
                        progressBar.visibility = View.GONE
                        //Log.d("ProgressBarStatus", "Visibilitas ProgressBar: ${progressBar.visibility}")
                    }
                }
            }
        }
    }

    private fun navigateToDetailActivity(sentimentId: String) {
        val intent = Intent(requireContext(), DetailAnalyticsActivity::class.java)
        intent.putExtra("SENTIMENT_ID", sentimentId)
        startActivity(intent)
    }
}