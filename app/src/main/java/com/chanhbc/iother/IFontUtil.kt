package com.chanhbc.iother

import android.content.Context
import android.graphics.Typeface

@Suppress("unused", "MemberVisibilityCanBePrivate", "SameParameterValue")
object IFontUtil {
    fun getTypeface(context: Context, name: String): Typeface? {
        var typeface = Typeface.createFromAsset(context.assets, name)
        if (typeface != null) {
            return typeface
        } else {
            if (name.contains(IConstant.SLASH) && name.contains(IConstant.PERIOD)) {
                typeface = if (name.endsWith(IConstant.EX_TTF, true)) {
                    val lastIndex = name.lastIndexOf(IConstant.EX_TTF, 0, true)
                    val tmp = name.substring(0, lastIndex) + IConstant.EX_OTF
                    getTypeface(context, tmp)
                } else {
                    Typeface.createFromAsset(context.assets, name)
                }
                return typeface
            } else {
                typeface = if (!name.contains(IConstant.SLASH)) {
                    getTypeface(context, IConstant.FONTS_FOLDER + IConstant.SLASH + name)
                } else {
                    getTypeface(context, name + IConstant.EX_TTF)
                }
                return typeface
            }
        }
    }

    fun getTypeface(context: Context, name: String, format: Int): Typeface? {
        return if (name.contains(IConstant.EX_TTF) || name.contains(IConstant.EX_OTF)) {
            getTypeface(
                context,
                IConstant.FONTS_FOLDER + IConstant.SLASH + name
            )
        } else {
            getTypeface(
                context,
                IConstant.FONTS_FOLDER + IConstant.SLASH + name + if (format == 1) IConstant.EX_TTF else IConstant.EX_OTF
            )
        }
    }

    fun getTypeface(context: Context, fontDefault: Int): Typeface? {
        val fontName: String = when (fontDefault) {
            1 -> IConstant.FONT_QS_BOLD

            2 -> IConstant.FONT_QS_SEMI_BOLD

            3 -> IConstant.FONT_QS_MEDIUM

            4 -> IConstant.FONT_QS_REGULAR

            5 -> IConstant.FONT_QS_LIGHT

            6 -> IConstant.FONT_QS_VARIABLE

            else -> IConstant.FONT_QS_MEDIUM
        }
        return getTypeface(context, IConstant.FONTS_FOLDER + IConstant.SLASH + fontName)
    }
}