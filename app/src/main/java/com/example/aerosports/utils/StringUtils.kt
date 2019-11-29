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
        val baseStop: String = "st"
        val baseRandom: String = "ASrd"
        val baseLine2: String = "ASo2"
        val baseLine3: String = "ASo3"
        val baseSpeed: String = "ASsp"
        val baseSpin: String = "ASsn"
        val baseFeed: String = "ASfr"
        val baseElev: String = "ASel"

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