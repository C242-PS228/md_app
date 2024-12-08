package com.example.perceivo

import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import androidx.core.widget.TextViewCompat.setTextAppearance

class MainActivity : AppCompatActivity() {
    private lateinit var linksContainer: LinearLayout
    private lateinit var addLinkButton: Button
    private lateinit var tagsContainer: LinearLayout
    private lateinit var addTagButton: Button
    private lateinit var tagsListContainer: LinearLayout
    private lateinit var analyzeButton: Button
    private var tagList: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init views
        linksContainer = findViewById(R.id.links_container)
        addLinkButton = findViewById(R.id.btn_add_link)
        tagsContainer = findViewById(R.id.tags_container)
        addTagButton = findViewById(R.id.btn_add_tag)
        tagsListContainer = findViewById(R.id.tags_list_container)
        analyzeButton = findViewById(R.id.btn_analyze)

        // Add default link field (cannot be deleted)
        addDefaultLinkField()

        // Set listeners
        addLinkButton.setOnClickListener { addLinkField() }
        addTagButton.setOnClickListener { addTag() }
        analyzeButton.setOnClickListener { analyzeData() }
    }

    private fun addDefaultLinkField() {
        val defaultLink = createLinkField()
        defaultLink.hint = "Enter your link (required)"
        defaultLink.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48f, resources.displayMetrics).toInt()
        )
        defaultLink.setBackgroundResource(R.drawable.rounded_field)
        defaultLink.setTextAppearance(R.style.EditTextPrimary)
        defaultLink.setPadding(
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt(),
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, resources.displayMetrics).toInt()
        )
        linksContainer.addView(defaultLink)
    }

    private fun addLinkField() {
        val linkLayout = LinearLayout(this).apply {
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
        return EditText(this).apply {
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
        return ImageButton(this).apply {
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
        val tagInput = findViewById<EditText>(R.id.et_tags)
        val tagText = tagInput.text.toString().trim()

        if (TextUtils.isEmpty(tagText)) {
            Toast.makeText(this, "Tag cannot be empty!", Toast.LENGTH_SHORT).show()
            return
        }

        if (tagList.contains(tagText)) {
            Toast.makeText(this, "Tag already added!", Toast.LENGTH_SHORT).show()
            return
        }

        tagList.add(tagText)
        tagInput.text.clear()

        val tagView = createTagView(tagText)
        tagsListContainer.addView(tagView)
    }

    private fun createTagView(tag: String): LinearLayout {
        val tagLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topMargin = 8
            }
            setPadding(8)
        }

        val tagTextView = TextView(this).apply {
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

        // Get all links (including default)
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

        if (links.isEmpty()) {
            Toast.makeText(this, "At least one link is required!", Toast.LENGTH_SHORT).show()
            return
        }

        if (tagList.isEmpty()) {
            Toast.makeText(this, "At least one tag is required!", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(
            this,
            "Analyzing with ${links.size} links and ${tagList.size} tags!",
            Toast.LENGTH_SHORT
        ).show()
    }
}