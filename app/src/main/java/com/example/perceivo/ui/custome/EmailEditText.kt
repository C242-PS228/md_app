package com.example.perceivo.ui.custome

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int  = android.R.attr.editTextStyle
): AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateEmail(s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun validateEmail(s: CharSequence?) {
        if (s.isNullOrEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
            setError("Email tidak valid", null)
        } else {
            error = null
        }

    }
}