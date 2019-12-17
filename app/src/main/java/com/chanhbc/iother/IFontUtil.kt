package com.chanhbc.iother

import android.content.Context
import android.graphics.Typeface

@Suppress("unused", "MemberVisibilityCanBePrivate", "SameParameterValue")
object IFontUtil {
    fun getTypeface(context: Context, name: String): Typeface? {
        return try {
            Typeface.createFromAsset(context.assets, name)
        } catch (e: Exception) {
            null
        }
    }

    fun getTypeface(context: Context, name: String, format: Int): Typeface? {
        return getTypeface(
            context, IConstant.FONTS_FOLDER + IConstant.SLASH + name +
                    IConstant.PERIOD + if (format == 1) IConstant.TTF else IConstant.OTF
        )
    }

    fun getTypeface(context: Context, fontDefault: Int): Typeface? {
        val fontName: String = when (fontDefault) {
            1 -> IConstant.FONT_DEFAULT_BOLD

            2 -> IConstant.FONT_DEFAULT_SEMI_BOLD

            3 -> IConstant.FONT_DEFAULT_MEDIUM

            4 -> IConstant.FONT_DEFAULT_REGULAR

            5 -> IConstant.FONT_DEFAULT_LIGHT

            else -> IConstant.FONT_DEFAULT_MEDIUM
        }
        return getTypeface(context, IConstant.FONTS_FOLDER + IConstant.SLASH + fontName)
    }
}