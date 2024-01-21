package com.example.bookstores.custom_textview

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
class Regular(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {
    init {
        setFontTextView()
    }
    private fun setFontTextView() {
        val typeface: Typeface = Utils(context).getRegularTypeface()
        setTypeface(typeface)
    }
}


