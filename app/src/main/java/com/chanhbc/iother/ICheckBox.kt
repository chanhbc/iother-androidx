package com.chanhbc.iother

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox

@Suppress("unused")
open class ICheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.checkboxStyle
) : AppCompatCheckBox(context, attrs, defStyleAttr) {

    init {
        init(context, attrs)
    }

    @SuppressLint("Recycle")
    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        try {
            val textStyle =
                attrs.getAttributeIntValue(IConstant.ANDROID_SCHEMA, "textStyle", Typeface.NORMAL)
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.ICheckBox)
            val fontName = attributes.getString(R.styleable.ICheckBox_icb_font_name)
            val typeface: Typeface? = if (fontName.isNullOrEmpty()) {
                val fontDefault = attributes.getInt(R.styleable.ICheckBox_icb_font_default, -1)
                IFontUtil.getTypeface(context, fontDefault)
            } else {
                val format = attributes.getInt(R.styleable.ICheckBox_icb_font_format, -1)
                IFontUtil.getTypeface(context, fontName, format)
            }
            if (typeface != null) {
                setTypeface(typeface, textStyle)
            }
        } catch (e: Exception) {
            ILog.e(e)
        }
    }
}
