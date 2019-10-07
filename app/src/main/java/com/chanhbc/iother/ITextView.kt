package com.chanhbc.iother

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class ITextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.textViewStyle
) : AppCompatTextView(context, attrs, defStyleAttr) {

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
            val attributes = context.obtainStyledAttributes(attrs, R.styleable.ITextView)
            val fontName = attributes.getString(R.styleable.ITextView_itv_font_name)
            val typeface: Typeface? = if (fontName.isNullOrEmpty()) {
                val fontDefault = attributes.getInt(R.styleable.ITextView_itv_font_default, -1)
                IFontUtil.getTypeface(context, fontDefault)
            } else {
                val format = attributes.getInt(R.styleable.ITextView_itv_font_format, -1)
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
