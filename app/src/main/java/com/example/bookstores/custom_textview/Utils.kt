package com.example.bookstores.custom_textview

import android.content.Context
import android.graphics.Typeface

class Utils(context: Context) {
    private var regular: Typeface

    init {
        regular = Typeface.createFromAsset(context.assets, "fonts/AlexBrush-Regular.ttf")
    }

    fun getRegularTypeface(): Typeface {
        return regular
    }
}
