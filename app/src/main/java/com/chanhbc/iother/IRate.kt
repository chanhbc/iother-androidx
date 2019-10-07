package com.chanhbc.iother

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.Window
import android.widget.*
import kotlinx.android.synthetic.main.dialog_rate.*

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
        val btnOk = findViewById<Button>(R.id.btn_ok)
        val btnNotNow = findViewById<Button>(R.id.btn_cancel)
        val txtAppName = findViewById<TextView>(R.id.txt_name_app)
        val imageIcon = findViewById<ImageView>(R.id.img_icon_app)

        val drawable = IOther.getInstance(mContext).applicationDrawable
        if (drawable != null) {
            imageIcon.setImageDrawable(drawable)
        }
        txtAppName.text = IOther.getInstance(mContext).appName
        val stars = ratingBar.progressDrawable as LayerDrawable
        stars.getDrawable(2).setColorFilter(Color.parseColor("#ff2d54"), PorterDuff.Mode.SRC_ATOP)
        stars.getDrawable(0).setColorFilter(Color.parseColor("#B0B0B6"), PorterDuff.Mode.SRC_ATOP)
        btnOk.setOnClickListener {
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
        btnNotNow.setOnClickListener {
            dismiss()
            if (isExit) {
                finish()
            }
        }
        ratingBar.onRatingBarChangeListener =
            RatingBar.OnRatingBarChangeListener { _, rating, _ ->
                btnOk.text = if (rating > numberStar)
                    mContext.getString(R.string.rate)
                else
                    mContext.getString(R.string.feedback)
            }
    }

    private fun finish() {
        if (mContext is Activity) {
            mContext.finish()
        }
    }

    fun show(exit: Boolean) {
        this.isExit = exit
        this.show()
    }

    override fun onCreate(savedInstanceState: Bundle) {
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