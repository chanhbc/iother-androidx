package com.chanhbc.iother

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Window
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_rate.*

@Suppress("unused", "MemberVisibilityCanBePrivate", "SameParameterValue")
class IRate @JvmOverloads constructor(
    private val mContext: Context,
    private val email: String,
    times: Int = 3
) : Dialog(mContext) {
    private var numberStar = 3
    private var isExit: Boolean = false

    val isRate: Boolean
        get() = isRate(mContext)

    fun setNumberStar(numberStar: Int) {
        this.numberStar = numberStar
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_rate)
        setCancelable(true)
        setCanceledOnTouchOutside(true)
        initDialog()
        if (times > 0) {
            initShowWhenAppOpen(times)
        }
    }

    private fun initShowWhenAppOpen(times: Int) {
        if (!isRate) {
            var countOpen = IShared.getInstance(mContext)
                .getInt(KEY_COUNT_OPEN_APP, VALUE_COUNT_OPEN_APP_DEFAULT)
            countOpen++
            if (countOpen >= times) {
                countOpen = times
                show()
            }
            IShared.getInstance(mContext).putInt(KEY_COUNT_OPEN_APP, countOpen)
        }
    }

    private fun initDialog() {
        val drawable = IOther.getInstance(mContext).applicationDrawable
        if (drawable != null) {
            img_icon_app.setImageDrawable(drawable)
        }
        txt_name_app.text = IOther.getInstance(mContext).appName
        val stars = ratingBar.progressDrawable as LayerDrawable
        stars.getDrawable(2).colorFilter = PorterDuffColorFilter(Color.parseColor("#ff2d54"), PorterDuff.Mode.SRC_ATOP)
        stars.getDrawable(0).colorFilter = PorterDuffColorFilter(Color.parseColor("#B0B0B6"), PorterDuff.Mode.SRC_ATOP)
        btn_ok.setOnClickListener {
            if (ratingBar.rating == 0f) {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.plz_rate_5_star),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (ratingBar.rating > numberStar) {
                    IShared.getInstance(mContext).putBoolean(IConstant.KEY_IS_RATE, true)
                    IOther.getInstance(mContext).openMarket()
                } else {
                    IOther.getInstance(mContext).feedback(email)
                }
                dismiss()
            }
        }
        btn_cancel.setOnClickListener {
            dismiss()
            if (isExit) {
                finish()
            }
        }
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                btn_ok.text = if (rating > numberStar)
                    mContext.getString(R.string.rate)
                else
                    mContext.getString(R.string.feedback)
            }
        setRateNumber(5)
    }

    private fun setRateNumber(rateNumber: Int) {
        ratingBar.numStars = rateNumber
    }

    private fun finish() {
        if (mContext is Activity) {
            mContext.finish()
        }
    }

    fun show(exit: Boolean) {
        this.isExit = exit
        val textId = if (isExit) {
            R.string.exit
        } else {
            R.string.cancel
        }
        btn_cancel.setText(textId)
        this.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (window != null) {
                window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window?.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
        } catch (ignored: Exception) {

        }

    }

    companion object {

        private const val KEY_COUNT_OPEN_APP = "key_count_open_app"
        private const val VALUE_COUNT_OPEN_APP_DEFAULT = 0

        fun isRate(context: Context): Boolean {
            return IShared.getInstance(context).getBoolean(IConstant.KEY_IS_RATE, false)
        }
    }
}