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
import com.chanhbc.iother.databinding.DialogRateBinding

@Suppress("unused", "MemberVisibilityCanBePrivate", "SameParameterValue")
class IRate @JvmOverloads constructor(
    private val mContext: Context,
    private val email: String,
    times: Int = 3
) : Dialog(mContext) {
    private var numberStar = 3
    private var isExit: Boolean = false
    private val mBinding = DialogRateBinding.inflate(layoutInflater)

    val isRate: Boolean
        get() = isRate(mContext)

    fun setNumberStar(numberStar: Int) {
        this.numberStar = numberStar
    }

    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        initDialog()
        setOnDismissListener {
            if (mBinding.icbDoNotShowAgain.isChecked) {
                IShared.getInstance(mContext).putBoolean(IConstant.KEY_IS_RATE, true)
            }
        }
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
            mBinding.imgIconApp.setImageDrawable(drawable)
        }
        mBinding.txtNameApp.text = IOther.getInstance(mContext).appName
        val stars = mBinding.ratingBar.progressDrawable as LayerDrawable
        stars.getDrawable(2).colorFilter =
            PorterDuffColorFilter(Color.parseColor("#ff2d54"), PorterDuff.Mode.SRC_ATOP)
        stars.getDrawable(0).colorFilter =
            PorterDuffColorFilter(Color.parseColor("#B0B0B6"), PorterDuff.Mode.SRC_ATOP)
        mBinding.btnOk.setOnClickListener {
            if (mBinding.ratingBar.rating == 0f) {
                Toast.makeText(
                    mContext,
                    mContext.getString(R.string.plz_rate_5_star),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                IShared.getInstance(mContext).putBoolean(IConstant.KEY_IS_RATE, true)
                if (mBinding.ratingBar.rating > numberStar) {
                    IOther.getInstance(mContext).openMarket()
                } else {
                    IOther.getInstance(mContext).feedback(email)
                }
                dismiss()
            }
        }
        mBinding.btnCancel.setOnClickListener {
            dismiss()
            if (isExit) {
                finish()
            }
        }
        mBinding.ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                mBinding.btnOk.text = if (rating > numberStar)
                    mContext.getString(R.string.rate)
                else
                    mContext.getString(R.string.feedback)
            }
        setRateNumber(5)
    }

    private fun setRateNumber(rateNumber: Int) {
        mBinding.ratingBar.numStars = rateNumber
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
        mBinding.btnCancel.setText(textId)
        this.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (window != null) {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        setCancelable(true)
        setCanceledOnTouchOutside(true)
    }

    companion object {
        private const val KEY_COUNT_OPEN_APP = "key_count_open_app"
        private const val VALUE_COUNT_OPEN_APP_DEFAULT = 0

        fun isRate(context: Context): Boolean {
            return IShared.getInstance(context).getBoolean(IConstant.KEY_IS_RATE, false)
        }
    }
}