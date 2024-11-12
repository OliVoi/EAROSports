package com.example.aerosports.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Typeface
import android.text.TextUtils
import android.util.Log
import android.widget.Toast

class StringUtils {
    companion object {
        @JvmStatic
        val baseStop: String = "ASst"
        const val baseRandom: String = "ASrd"
        const val baseLine2: String = "ASo2"
        const val baseLine3 = "ASo3"
        const val baseSpeed: String = "ASsp"
        const val baseSpin: String = "ASsn"
        const val baseFeed: String = "ASfr"
        const val baseElev: String = "ASel"

        fun setCustomFont(ctx: Context, path: String): Typeface? {
            var tf: Typeface? = null
            try {
                tf = Typeface.createFromAsset(ctx.assets, path)
            } catch (e: Exception) {
                Log.e(ContentValues.TAG, "Could not get typeface: " + e.message)
            }
            return tf
        }

        fun reportMessage(contenxt: Context?, text: String?) {
            contenxt.let {
                if (TextUtils.isEmpty(text)) return
                Toast.makeText(it, text, Toast.LENGTH_SHORT).show()
            }
        }
    }
}